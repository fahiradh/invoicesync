package com.megapro.invoicesync.service;

import java.util.List;
import java.util.ArrayList;
import com.megapro.invoicesync.dto.FileMapper;
import com.megapro.invoicesync.dto.response.ReadFileResponseDTO;
import com.megapro.invoicesync.model.Employee;
import com.megapro.invoicesync.model.FileModel;
import com.megapro.invoicesync.model.Invoice;
import com.megapro.invoicesync.model.Product;

import org.springframework.stereotype.Service;
import java.io.IOException;

import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.Locale;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class PrintService {
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private UserService userService;

    @Autowired
    private FilesStorageService fileService;

    @Autowired
    private FileMapper fileMapper;

    public byte[] generateInvoice(Invoice invoice) throws IOException {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        Context context = new Context();
        String invoiceDate = invoiceService.parseDate(invoice.getInvoiceDate());
        String dueDate = invoiceService.parseDate(invoice.getDueDate());
        Employee employee = userService.findByEmail(invoice.getStaffEmail());
        String employeeName = String.format("%s %s", employee.getFirst_name(), employee.getLast_name());
        String grandTotal = currencyFormat.format(invoice.getGrandTotal());
        String subtotal = currencyFormat.format(invoice.getSubtotal());
        String taxes = currencyFormat.format(invoice.getTaxTotal());
        List<Product> listProduct = invoiceService.getListProductInvoice(invoice);
        var files = fileService.findByFileInvoice(invoice);
        List<ReadFileResponseDTO> documents = new ArrayList<>();
        for (FileModel addedFile : files){
            if (addedFile != null){
                var addedFileDTO = fileMapper.fileModelToReadFileResponseDTO(addedFile);
                documents.add(addedFileDTO);
            }
        }
        Path source = Paths.get("/static/assets/krida-logo.png");
        // From image to byte[]
        BufferedImage bi = ImageIO.read(source.toFile());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);
        byte[] originByteArray = baos.toByteArray();
        // From byte[] to base64
        // byte[] encoded = Base64.getEncoder().encode(originByteArray);
        String stringBase64 = Base64.getEncoder().encodeToString(originByteArray);
        // System.out.println("byte[] Base64 encoded: " + encoded);
        // System.out.println("String Base64 encoded: " + stringBase64);
        // String logo = encodedImageToString("/assets/krida-logo.png");
        context.setVariable("invoiceNumber", invoice.getInvoiceNumber());
        context.setVariable("customerName", invoice.getCustomer().getName());
        context.setVariable("customerAddress", invoice.getCustomer().getAddress());
        context.setVariable("customerPhone", invoice.getCustomer().getPhone());
        context.setVariable("customerContact", invoice.getCustomer().getContact());
        context.setVariable("dueDate", dueDate);
        context.setVariable("invoiceDate", invoiceDate);
        context.setVariable("subtotal", subtotal);
        context.setVariable("taxes", taxes);
        context.setVariable("grandTotal", grandTotal);
        context.setVariable("totalDiscount", String.valueOf(invoice.getTotalDiscount()));
        context.setVariable("status", invoice.getStatus());
        context.setVariable("totalWords", invoice.getTotalWords());
        context.setVariable("accountNumber", invoice.getAccountNumber());
        context.setVariable("accountName", invoice.getAccountName());
        context.setVariable("bankName", invoice.getBankName());
        context.setVariable("city", invoice.getCity());
        context.setVariable("signature", "data:image/jpeg;base64,"+invoice.getSignature());
        context.setVariable("employeeName", employeeName);
        context.setVariable("listProduct", listProduct);
        context.setVariable("documents", documents);
        context.setVariable("logo", stringBase64);

        String processedHtml = templateEngine.process("data/template", context);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        HtmlConverter.convertToPdf(processedHtml, stream);

        byte[] bytes = stream.toByteArray();
        stream.flush();

        return bytes;
    }
}