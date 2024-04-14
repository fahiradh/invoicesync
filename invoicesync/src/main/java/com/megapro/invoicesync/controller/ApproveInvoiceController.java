package com.megapro.invoicesync.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.megapro.invoicesync.dto.ApprovalMapper;
import com.megapro.invoicesync.dto.FileMapper;
import com.megapro.invoicesync.dto.InvoiceMapper;
import com.megapro.invoicesync.dto.request.UpdateApprovalRequestDTO;
import com.megapro.invoicesync.dto.response.ReadApprovalResponseDTO;
import com.megapro.invoicesync.dto.response.ReadFileResponseDTO;
import com.megapro.invoicesync.dto.response.ReadInvoiceResponse;
import com.megapro.invoicesync.model.Approval;
import com.megapro.invoicesync.model.FileModel;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.model.Tax;
import com.megapro.invoicesync.repository.EmployeeDb;
import com.megapro.invoicesync.repository.UserAppDb;
import com.megapro.invoicesync.service.ApprovalService;
import com.megapro.invoicesync.service.FilesStorageService;
import com.megapro.invoicesync.service.InvoiceService;
import com.megapro.invoicesync.service.TaxService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ApproveInvoiceController {

    @Autowired
    UserAppDb userAppDb;

    @Autowired
    EmployeeDb employeeDb;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    InvoiceMapper invoiceMapper;

    @Autowired
    ApprovalMapper approvalMapper;

    @Autowired
    ApprovalService approvalService;

    @Autowired
    TaxService taxService;

    @Autowired
    FilesStorageService fileService;

    @Autowired
    FileMapper fileMapper;

    @GetMapping("/approval")
    public String approveInvoicePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var employee = employeeDb.findByEmail(email);
        String role = employee.getRole().getRole();
        model.addAttribute("role", role);

        List<ReadInvoiceResponse> invoiceDTOList = new ArrayList<>();
        var approvalList = employee.getListApproval();
        for(Approval approval : approvalList){
            if(approval.isShown()){
                var invoice = approval.getInvoice();
    
                ReadInvoiceResponse invoiceDTO = invoiceMapper.readInvoiceToInvoiceResponse(invoice);
                invoiceDTO.setApprovalId(approval.getApprovalId());
                System.out.println("Ini approval id yang dibawa " + invoiceDTO.getApprovalId());
                invoiceDTOList.add(invoiceDTO);
            }
        }
        model.addAttribute("invoices", invoiceDTOList);

        return "approve-invoice/list-approval.html";
    }
    
    // Detail invoice untuk diapprove
    @GetMapping("/approval/{invoiceNumber}")
    public String getApprovalDetail(
                            @PathVariable("invoiceNumber") String encodedInvoiceNumber, 
                            int approvalId,
                            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var user = userAppDb.findByEmail(email);
        String role = user.getRole().getRole();
        String invoiceNumber = encodedInvoiceNumber.replace('_', '/');

        // Bagian detail invoice
        var invoice = invoiceService.getInvoiceByInvoiceNumber(invoiceNumber);
        List<Product> listProduct = invoiceService.getListProductInvoice(invoice);
        List<Tax> taxList = taxService.findAllTaxes();
        var invoiceDTO = invoiceMapper.readInvoiceToInvoiceResponse(invoice);
        var date = invoiceDTO.getInvoiceDate();
        var approvalStatus = approvalService.findApprovalByApprovalId(approvalId).getApprovalStatus();

        model.addAttribute("image", invoice.getSignature());
        model.addAttribute("status", approvalStatus);
        model.addAttribute("email", email);
        model.addAttribute("role", role);
        model.addAttribute("listProduct", listProduct);
        model.addAttribute("date", String.format("%02d/%02d/%04d", date.getDayOfMonth(),  date.getMonth().getValue(), date.getYear()));
        model.addAttribute("taxList", taxList);
        model.addAttribute("invoice", invoiceDTO);
        model.addAttribute("dateInvoice", invoiceService.parseDate(invoiceDTO.getInvoiceDate()));

        // Bagian logs
        var approvals = invoice.getListApproval();
        List<ReadApprovalResponseDTO> approvalLogs = new ArrayList<>();
        var approvalDTO = new UpdateApprovalRequestDTO();
        for(Approval approval:approvals){
            if(approval.getApprovalStatus()==null || approval.getApprovalStatus().isEmpty()){
                approvalDTO = approvalMapper.approvalToUpdateApprovalRequestDTO(approval);
                break;
            }
            var filesLog = approval.getApprovalFiles();
            System.out.println("Ini approval " + approval.getEmployee().getEmail() + " punya file? " + filesLog.isEmpty());
            List<ReadFileResponseDTO> filesDTO = new ArrayList<>();
            if(filesLog != null || filesLog.size()!=0){
                for(FileModel fileModel : filesLog){
                    var fileDTO = fileMapper.fileModelToReadFileResponseDTO(fileModel);
                    filesDTO.add(fileDTO);
                }
            }
            var approvalLog = approvalMapper.approvalToReadApprovalResponseDTO(approval);
            approvalLog.setFilesDTO(filesDTO);
            approvalLogs.add(approvalLog);
        }

        model.addAttribute("approvalDTO", approvalDTO);
        model.addAttribute("approvalLogs", approvalLogs);

        return "approve-invoice/approval-page.html";
    }

    @PostMapping("/approve-invoice")
    public String approve(UpdateApprovalRequestDTO updateApprovalRequestDTO, RedirectAttributes redirectAttributes) {

        var approval = approvalService.findApprovalByApprovalId(updateApprovalRequestDTO.getApprovalId());
        approval.setApprovalStatus("Approved");
        approval.setApprovalTime(LocalDate.now());
        approvalService.saveApproval(approval);

        var invoice = approval.getInvoice();
        invoice.setStatus("Approved");
        invoiceService.updateInvoice(invoice);

        var invoiceNumber = approval.getInvoice().getInvoiceNumber().replace('/', '_');
        System.out.println("Attempting to redirect to: /approval/" + invoiceNumber);

        redirectAttributes.addFlashAttribute("message", "Invoice Approved Successfully");

        return "redirect:/approval/" + invoiceNumber + "?approvalId=" + approval.getApprovalId();
    }

    @PostMapping("/revision-invoice")
    public String reviseInvoice(UpdateApprovalRequestDTO updateApprovalDTO,
                                @RequestParam("files") MultipartFile[] files) {
        var approval = approvalService.findApprovalByApprovalId(updateApprovalDTO.getApprovalId());
        approval.setApprovalStatus("Need Revision");
        approval.setComment(updateApprovalDTO.getComment());
        approval.setApprovalTime((LocalDate.now()));

        fileService.save(files, updateApprovalDTO.getApprovalId());

        approvalService.saveApproval(approval);

        var invoice = approval.getInvoice();
        invoice.setStatus("Need Revision");
        invoiceService.updateInvoice(invoice);

        var invoiceNumber = approval.getInvoice().getInvoiceNumber().replace('/', '_');
        
        return "redirect:/approval/"+invoiceNumber+"?approvalId="+updateApprovalDTO.getApprovalId();
    }
    
    @PostMapping("/reject-invoice")
    public String rejectInvoice(UpdateApprovalRequestDTO updateApprovalDTO) {
        var approval = approvalService.findApprovalByApprovalId(updateApprovalDTO.getApprovalId());
        approval.setApprovalStatus("Rejected");
        approval.setApprovalTime((LocalDate.now()));
        approvalService.saveApproval(approval);

        var invoice = approval.getInvoice();
        invoice.setStatus("Rejected");
        invoiceService.updateInvoice(invoice);

        var invoiceNumber = approval.getInvoice().getInvoiceNumber().replace('/', '_');
        
        return "redirect:/approval/"+invoiceNumber+"?approvalId="+updateApprovalDTO.getApprovalId();
    }

    @GetMapping("/approve-invoice/download-files/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable UUID fileId) {
        FileModel fileModel = fileService.getFile(fileId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileModel.getFileName() + "\"")
            .body(fileModel.getFileData());
    }

}
