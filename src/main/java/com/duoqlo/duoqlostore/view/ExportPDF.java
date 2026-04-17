package com.duoqlo.duoqlostore.view;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.font.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.duoqlo.duoqlostore.model.SalesRecord;
import javafx.collections.ObservableList;


public class ExportPDF {

    public static void export(ObservableList<SalesRecord> salesData, String filterLabel, String outputPath) {
        try {
            PdfWriter writer = new PdfWriter(outputPath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // ===== TITLE =====
            document.add(new Paragraph("Sales Report")
                    .setFont(bold)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER));

            // ===== SUBTITLE (filter label e.g. "General / Daily") =====
            document.add(new Paragraph(filterLabel)
                    .setFont(normal)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));

            // ===== META =====
            document.add(new Paragraph("Prepared by: Admin")
                    .setFont(normal)
                    .setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("Date: " + java.time.LocalDate.now())
                    .setFont(normal)
                    .setTextAlignment(TextAlignment.RIGHT));

            // ===== TABLE =====
            // Match your 5 TableView columns
            float[] columnWidths = {120F, 100F, 80F, 80F, 100F};
            Table table = new Table(columnWidths);

            DeviceRgb headerColor = new DeviceRgb(254, 108, 1); // #FE6C01 from your CSS
            DeviceRgb rowEven    = new DeviceRgb(255, 213, 184);  // rgba(254,108,1,0.2) approximated
            DeviceRgb rowOdd     = new DeviceRgb(255, 240, 230);

            // --- Header Row ---
            String[] headers = {"Date / Label", "Revenue (RM)", "Units Sold", "Orders"};
            for (String h : headers) {
                table.addHeaderCell(
                        new Cell().add(new Paragraph(h).setFont(bold).setFontColor(ColorConstants.WHITE))
                                .setBackgroundColor(headerColor)
                                .setTextAlignment(TextAlignment.CENTER)
                );
            }

            // --- Data Rows ---
            int rowIndex = 0;
            for (SalesRecord record : salesData) {
                DeviceRgb rowBg = (rowIndex % 2 == 0) ? rowEven : rowOdd;

                table.addCell(styledCell(record.getLabelValue(),    normal, rowBg));
                table.addCell(styledCell(String.format("%.2f", record.getRevenue()), normal, rowBg));
                table.addCell(styledCell(String.valueOf(record.getTotalItems()),   normal, rowBg));
                table.addCell(styledCell(String.valueOf(record.getOrders()),  normal, rowBg));

                rowIndex++;
            }

            document.add(table);

            // ===== FOOTER =====
            document.add(new Paragraph("End of Report")
                    .setFont(normal)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10));

            document.close();
            System.out.println("PDF exported to: " + outputPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper: create a styled cell
    private static Cell styledCell(String text, PdfFont font, DeviceRgb bgColor) {
        return new Cell()
                .add(new Paragraph(text).setFont(font))
                .setBackgroundColor(bgColor)
                .setTextAlignment(TextAlignment.CENTER);
    }
}
