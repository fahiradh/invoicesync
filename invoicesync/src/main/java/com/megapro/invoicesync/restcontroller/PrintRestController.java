package com.megapro.invoicesync.restcontroller;

import com.megapro.invoicesync.model.Employee;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;
import com.megapro.invoicesync.service.InvoiceService;
import com.megapro.invoicesync.service.ProductService;
import com.megapro.invoicesync.service.UserService;
import com.megapro.invoicesync.utils.PrintInvoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PrintRestController {
    @Autowired
    InvoiceService invoiceService;

    @Autowired
    UserService userService;

    @GetMapping("/download-invoice/{invoiceId}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable("invoiceId") UUID invoiceId) {
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
        
        try {
            String htmlTemplate = PrintInvoice.readHtmlTemplate("src/main/resources/data/template.html");
            String processedHtml = processInvoiceData(htmlTemplate, invoice);
            String processedProduct = processInvoiceProduct(processedHtml, invoice);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintInvoice.generatePdf(outputStream, processedProduct);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice.pdf");
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String processInvoiceData(String htmlTemplate, Invoice invoice) {
        String invoiceDate = invoiceService.parseDate(invoice.getInvoiceDate());
        String dueDate = invoiceService.parseDate(invoice.getDueDate());
        Employee employee = userService.findByEmail(invoice.getStaffEmail());
        String employeeName = String.format("%s %s", employee.getFirst_name(), employee.getLast_name());
        String grandTotal = String.format("Rp%,.2f", invoice.getGrandTotal());
        String subtotal = String.format("Rp%,.2f", invoice.getSubtotal());
        htmlTemplate = htmlTemplate.replace("{invoiceNumber}", invoice.getInvoiceNumber());
        htmlTemplate = htmlTemplate.replace("{customerName}", invoice.getCustomer().getName());
        htmlTemplate = htmlTemplate.replace("{customerAddress}", invoice.getCustomer().getAddress());
        htmlTemplate = htmlTemplate.replace("{customerPhone}", invoice.getCustomer().getPhone());
        htmlTemplate = htmlTemplate.replace("{customerContact}", invoice.getCustomer().getContact());
        htmlTemplate = htmlTemplate.replace("{dueDate}", dueDate);
        htmlTemplate = htmlTemplate.replace("{invoiceDate}", invoiceDate);
        htmlTemplate = htmlTemplate.replace("{subtotal}", subtotal);
        htmlTemplate = htmlTemplate.replace("{taxes}", invoice.getTaxTotal().toString());
        htmlTemplate = htmlTemplate.replace("{grandTotal}", grandTotal);
        htmlTemplate = htmlTemplate.replace("{totalDiscount}", String.valueOf(invoice.getTotalDiscount()));
        htmlTemplate = htmlTemplate.replace("{status}", invoice.getStatus());
        htmlTemplate = htmlTemplate.replace("{totalWords}", invoice.getTotalWords());
        htmlTemplate = htmlTemplate.replace("{accountNumber}", invoice.getAccountNumber());
        htmlTemplate = htmlTemplate.replace("{accountName}", invoice.getAccountName());
        htmlTemplate = htmlTemplate.replace("{bankName}", invoice.getBankName());
        htmlTemplate = htmlTemplate.replace("{city}", invoice.getCity());
        htmlTemplate = htmlTemplate.replace("{signature}", "data:image/jpeg;base64,"+invoice.getSignature());
        htmlTemplate = htmlTemplate.replace("{employeeName}", employeeName);
        return htmlTemplate;
    }

    private String processInvoiceProduct(String htmlTemplate, Invoice invoice) {
        var listProduct = invoiceService.getListProductInvoice(invoice);
        StringBuilder html = new StringBuilder("<tbody class=\"list-product\">");
        for (int i = 0; i < listProduct.size(); i++) {
            html.append("<tr>")
                .append(String.format("<td>%s</td>", i + 1))
                .append(String.format("<td>%s</td>", listProduct.get(i).getDescription()))
                .append(String.format("<td>%s</td>", listProduct.get(i).getQuantity()))
                .append(String.format("<td>%s</td>", listProduct.get(i).getPrice()))
                .append(String.format("<td>%s</td>", listProduct.get(i).getTotalPrice()))
                .append("</tr>");
        }
        html.append("</tbody>");
        htmlTemplate = htmlTemplate.replace("<tbody class=\"list-product\"></tbody>", html.toString());
        return htmlTemplate;
    }
    
}