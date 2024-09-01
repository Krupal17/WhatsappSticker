package com.kp.bright.whatsapptickers.tabulas.detecttable;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfTableExtractor {
    public static List<String> extractTablesFromPdf(String pdfPath) throws IOException {
        List<String> tableRows = new ArrayList<>();

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfPath));
        int numberOfPages = pdfDoc.getNumberOfPages();

        for (int i = 1; i <= numberOfPages; i++) {
            ITextExtractionStrategy strategy = new LocationTextExtractionStrategy();
            String pageText = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i), strategy);

            // This is a simple extraction; you may need to parse and organize this text into table rows and columns
            String[] rows = pageText.split("\n");
            for (String row : rows) {
                tableRows.add(row);
            }
        }

        pdfDoc.close();
        return tableRows;
    }
}
