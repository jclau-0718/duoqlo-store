package com.duoqlo.duoqlostore.view;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.*;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.font.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.duoqlo.duoqlostore.model.SalesRecord;
import com.itextpdf.layout.properties.VerticalAlignment;
import javafx.collections.ObservableList;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.layout.element.Image;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.LineChart;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

public class ExportPDF {

    private DeviceRgb orange = new DeviceRgb(254, 108, 1);

    private PdfFont bold;
    private PdfFont normal;

    public ExportPDF() {
        try {
            bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Table createMetaTable(Cell leftCell, Cell rightCell) {
        float[] metaWidths = {1, 1};
        Table metaTable = new Table(metaWidths).useAllAvailableWidth();

        metaTable.addCell(leftCell);

        metaTable.addCell(rightCell);

        metaTable.setMarginBottom(10);

        return metaTable;
    }

    private Table buildHeader() {
        URL url = getClass().getResource("/logo.png");

        ImageData imageData = ImageDataFactory.create(url);

        Image image = new Image(imageData);
        image.setWidth(100);
        image.setAutoScale(true);

        Cell imageCell = new Cell().add(image);
        imageCell.setBorder(Border.NO_BORDER);
        imageCell.setPaddingRight(15);
        imageCell.setVerticalAlignment(VerticalAlignment.MIDDLE);

        Paragraph title = new Paragraph("SALES REPORT");
        title.setFont(bold);
        title.setFontSize(20);
        title.setMarginBottom(0);
        title.setTextAlignment(TextAlignment.LEFT);

        DeviceRgb gray = new DeviceRgb(176, 176, 176);

        Paragraph subTitle = new Paragraph("ROUND-NECK SHIRT");
        subTitle.setFont(bold);
        subTitle.setFontSize(18);
        subTitle.setMarginTop(2);
        subTitle.setFontColor(gray);
        subTitle.setTextAlignment(TextAlignment.LEFT);

        SolidLine lineDrawer = new SolidLine();
        lineDrawer.setColor(orange);

        LineSeparator line = new LineSeparator(lineDrawer);
        line.setMarginTop(2);
        line.setMarginBottom(5);

        Cell titleCell = new Cell();
        titleCell.add(title);
        titleCell.add(subTitle);
        titleCell.add(line);
        titleCell.setBorder(Border.NO_BORDER);
        titleCell.setPaddingLeft(15);

        Table header = createMetaTable(imageCell, titleCell);

        return header;
    }

    private Table buildReportInfo() {
        Paragraph preparedParagraph = new Paragraph("Prepared by: \nJonathan Lau (5101)");
        preparedParagraph.setFont(normal);
        preparedParagraph.setTextAlignment(TextAlignment.LEFT);

        Cell preparedCell = new Cell();
        preparedCell.add(preparedParagraph);
        preparedCell.setBorder(Border.NO_BORDER);

        Paragraph dateParagraph = new Paragraph("Date: " + LocalDate.now());
        dateParagraph.setFont(normal);
        dateParagraph.setTextAlignment(TextAlignment.RIGHT);

        Cell dateCell = new Cell();
        dateCell.add(dateParagraph);
        dateCell.setBorder(Border.NO_BORDER);

        Table info = createMetaTable(preparedCell, dateCell);

        return info;
    }


    private Cell styledCell(String text, PdfFont font, DeviceRgb bgColor) {
        return new Cell()
                .add(new Paragraph(text).setFont(font))
                .setBackgroundColor(bgColor)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(Border.NO_BORDER);
    }

    public void export(ObservableList<SalesRecord> salesData, String filterLabel, String outputPath,
                              LineChart<String, Number> revenueChart,
                              LineChart<String, Number> itemsChart,
                              LineChart<String, Number> ordersChart) {
        try {
            PdfWriter writer = new PdfWriter(outputPath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            //Title
            document.add(buildHeader());

            //Report info (Prepared by and Date)
            document.add(buildReportInfo());

            //Sales table
            float[] columnWidths = {120F, 100F, 80F, 80F};
            Table table = new Table(columnWidths).useAllAvailableWidth();

            DeviceRgb headerColor = new DeviceRgb(254, 108, 1); // #FE6C01 from your CSS
            DeviceRgb rowEven    = new DeviceRgb(255, 213, 184);  // rgba(254,108,1,0.2) approximated
            DeviceRgb rowOdd     = new DeviceRgb(255, 240, 230);

            //Table Header
            String[] headers = {"Date / Label", "Revenue (RM)", "Units Sold", "Orders"};
            for (String h : headers) {
                table.addHeaderCell(
                        new Cell().add(new Paragraph(h).setFont(bold).setFontColor(ColorConstants.WHITE))
                                .setBackgroundColor(headerColor)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setBorder(Border.NO_BORDER)
                );
            }

            //Table Data
            int rowIndex = 0;
            for (SalesRecord record : salesData) {
                DeviceRgb rowBg = (rowIndex % 2 == 0) ? rowEven : rowOdd;

                PdfFont dataFont = normal;

                if(record.getLabelValue().equals("TOTAL")) {
                    dataFont = bold;
                }

                table.addCell(styledCell(record.getLabelValue(), dataFont, rowBg));
                table.addCell(styledCell(String.format("%.2f", record.getRevenue()), dataFont, rowBg));
                table.addCell(styledCell(String.valueOf(record.getTotalItems()), dataFont, rowBg));
                table.addCell(styledCell(String.valueOf(record.getOrders()), dataFont, rowBg));

                rowIndex++;
            }

            document.add(table);

            addChartImage(document, revenueChart);
            addChartImage(document, itemsChart);
            addChartImage(document, ordersChart);

            //Footer
            document.add(new Paragraph("END")
                    .setFont(normal)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10));

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addChartImage(Document document, LineChart<String, Number> chart) {
        try {
            //Snapshot the JavaFX chart node into a WritableImage
            WritableImage writableImage = chart.snapshot(null, null);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

            //Convert BufferedImage to byte array
            ByteArrayOutputStream base = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", base);
            byte[] imageBytes = base.toByteArray();

            //Insert into PDF, scale to page width
            Image pdfImage = new Image(ImageDataFactory.create(imageBytes));
            pdfImage.setWidth(document.getPdfDocument().getDefaultPageSize().getWidth() - 80);
            pdfImage.setAutoScaleHeight(true);
            pdfImage.setMarginTop(5);
            pdfImage.setMarginBottom(5);

            document.add(pdfImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void exportPDFWithChooser(Stage stage, ObservableList<SalesRecord> salesData, String filterLabel,
                                            LineChart<String, Number> revenueChart,
                                            LineChart<String, Number> itemsChart,
                                            LineChart<String, Number> ordersChart) {
        FileChooser fileChooser = new FileChooser();

        //Set dialog title
        fileChooser.setTitle("Save Sales Report");

        //Default filename
        fileChooser.setInitialFileName("sales-report.pdf");

        //Filter only pdf
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf")
        );

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        //Show save dialog
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            //Ensure .pdf extension
            String path = file.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf")) {
                path += ".pdf";
            }

            export(salesData, filterLabel, path, revenueChart, itemsChart, ordersChart);
        }
    }
}
