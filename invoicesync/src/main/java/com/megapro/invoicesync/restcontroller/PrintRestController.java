package com.megapro.invoicesync.restcontroller;

import com.megapro.invoicesync.utils.PrintInvoice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class PrintRestController {

    @GetMapping("/download-invoice")
    public ResponseEntity<byte[]> generatePdf() {
        try {
            // Generate PDF content
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintInvoice.generatePdf(outputStream);

            // Set HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice.pdf");

            // Return PDF content as ResponseEntity
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
