package com.megapro.invoicesync.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PrintInvoiceService {

    public static void generatePdf(ByteArrayOutputStream outputStream, String htmlContent) throws IOException {
        String tempHtmlFileName = "temp.html";
        String outputPdfFileName = "output.pdf";

        try (java.io.FileWriter fileWriter = new java.io.FileWriter(tempHtmlFileName)) {
            fileWriter.write(htmlContent);
        }

        // Jalankan wkhtmltopdf melalui ProcessBuilder
        ProcessBuilder processBuilder = new ProcessBuilder("wkhtmltopdf", tempHtmlFileName, outputPdfFileName);
        Process process = processBuilder.start();

        // Tunggu proses selesai
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Baca hasil PDF ke ByteArrayOutputStream
        try (java.io.FileInputStream fileInputStream = new java.io.FileInputStream(outputPdfFileName)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        }

        new File(tempHtmlFileName).delete();
        new File(outputPdfFileName).delete();
    }

    public static String readHtmlTemplate(String templatePath) throws IOException {
        byte[] encodedHtml = Files.readAllBytes(Paths.get(templatePath));
        return new String(encodedHtml, StandardCharsets.UTF_8);
    }

    // public static void generatePdf(ByteArrayOutputStream outputStream, String htmlContent) throws IOException {
    //     try (PDDocument document = new PDDocument()) {
    //         PDPage page = new PDPage();
    //         document.addPage(page);

    //         try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
    //             contentStream.beginText();
    //             contentStream.setFont(PDType1Font.HELVETICA, 12);
    //             float startX = 50;
    //             float startY = page.getMediaBox().getHeight()-50;
    //             contentStream.newLineAtOffset(startX, startY);
    //             contentStream.showText(htmlContent);
    //             contentStream.endText();
    //         }

    //         document.save(outputStream);
    //     }
    // }


    // public static String readHtmlTemplate(String templatePath) throws IOException {
    //     // Baca isi file HTML
    //     byte[] encodedHtml = Files.readAllBytes(Paths.get(templatePath));
    //     String htmlContent = new String(encodedHtml, StandardCharsets.UTF_8);

    //     // Load dokumen HTML menggunakan Jsoup
    //     Document doc = Jsoup.parse(htmlContent);

    //     // Ambil semua elemen <style> dan gabungkan menjadi satu string
    //     StringBuilder cssBuilder = new StringBuilder();
    //     Elements styleElements = doc.select("style");
    //     for (Element styleElement : styleElements) {
    //         cssBuilder.append(styleElement.html()).append("\n");
    //     }

    //     // Tambahkan CSS ke dalam tag <head>
    //     Element head = doc.head();
    //     head.append("<style>")
    //         .append(cssBuilder.toString())
    //         .append("</style>");

    //     // Konversi dokumen Jsoup kembali menjadi HTML
    //     String modifiedHtmlContent = doc.toString();

    //     return modifiedHtmlContent;
    // }
}
