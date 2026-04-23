package com.duoqlo.duoqlostore.view;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.*;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
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
import javafx.scene.chart.XYChart;
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

public class SalesReportExporter {

    private DeviceRgb ORANGE = new DeviceRgb(254, 108, 1);

    private PdfFont bold;
    private PdfFont normal;

    private String titleText;
    private String subTitleText = "NONE";

    private String adminName = "";
    private int adminId;

    private final int logoWidth = 150;

    private ObservableList<SalesRecord> salesData;
    private XYChart<String, Number> revenueChart;
    private XYChart<String, Number> itemsChart;
    private XYChart<String, Number> ordersChart;

    public SalesReportExporter() {
        try {
            bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTitle(String text) { this.titleText = text; }

    public void setSubTitle(String text) { this.subTitleText = text; }

    public void setSalesData(ObservableList<SalesRecord> salesData) { this.salesData = salesData; }

    public void setCharts(XYChart<String, Number> revenueChart,
                          XYChart<String, Number> itemsChart,
                          XYChart<String, Number> ordersChart) {
        this.revenueChart = revenueChart;
        this.itemsChart = itemsChart;
        this.ordersChart = ordersChart;
    }

    private Table createTitleTable(Cell leftCell, Cell rightCell) {
        float[] metaWidths = {170f, 1};
        Table metaTable = new Table(metaWidths).useAllAvailableWidth();

        metaTable.addCell(leftCell);

        metaTable.addCell(rightCell);

        metaTable.setMarginBottom(10);

        return metaTable;
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

        ImageData logoData = ImageDataFactory.create(url);

        Image logo = new Image(logoData);
        logo.setWidth(logoWidth);
        logo.setMinWidth(logoWidth);
        logo.setMaxWidth(logoWidth);

        Cell logoCell = new Cell().add(logo);
        logoCell.setBorder(Border.NO_BORDER);
        logoCell.setPaddingRight(15);
        logoCell.setVerticalAlignment(VerticalAlignment.MIDDLE);

        Paragraph title = new Paragraph(titleText);
        title.setFont(bold);
        title.setFontSize(20);
        title.setMarginBottom(0);
        title.setTextAlignment(TextAlignment.LEFT);

        DeviceRgb gray = new DeviceRgb(176, 176, 176);

        SolidLine lineDrawer = new SolidLine();
        lineDrawer.setColor(ORANGE);
        lineDrawer.setLineWidth(2f);

        LineSeparator line = new LineSeparator(lineDrawer);
        line.setMarginTop(2);
        line.setMarginBottom(5);

        Cell titleCell = new Cell();
        titleCell.add(title);

        if(!subTitleText.equals("NONE")) {
            Paragraph subTitle = new Paragraph(subTitleText);
            subTitle.setFont(bold);
            subTitle.setFontSize(18);
            subTitle.setMarginTop(2);
            subTitle.setFontColor(gray);
            subTitle.setTextAlignment(TextAlignment.LEFT);

            titleCell.add(subTitle);
        }

        titleCell.add(line);
        titleCell.setBorder(Border.NO_BORDER);
        titleCell.setPaddingLeft(15);

        Table header = createTitleTable(logoCell, titleCell);

        return header;
    }

    public void setAdminInfo(String name, int id) {
        this.adminName = name;
        this.adminId = id;
    }

    private Table buildReportInfo() {
        String preparedText = "Prepared by: \n";
        Paragraph preparedParagraph = new Paragraph(preparedText);
        preparedParagraph.setFont(bold);
        preparedParagraph.setTextAlignment(TextAlignment.LEFT);

        String adminInfoText = adminName + " (" + adminId + ")";
        Paragraph adminInfoParagraph = new Paragraph(adminInfoText);
        adminInfoParagraph.setFont(normal);
        adminInfoParagraph.setTextAlignment(TextAlignment.LEFT);

        Cell preparedCell = new Cell();
        preparedCell.add(preparedParagraph);
        preparedCell.add(adminInfoParagraph);
        preparedCell.setBorder(Border.NO_BORDER);

        Paragraph dateParagraph = new Paragraph("Date: " + LocalDate.now());
        dateParagraph.setFont(bold);
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

    public void generate(String outputPath) {
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
            Table table = new Table(columnWidths);
            table.useAllAvailableWidth();

            DeviceRgb headerColor = new DeviceRgb(254, 108, 1); // #FE6C01 from your CSS
            DeviceRgb rowEven    = new DeviceRgb(255, 213, 184);  // rgba(254,108,1,0.2) approximated
            DeviceRgb rowOdd     = new DeviceRgb(255, 240, 230);

            //Table Header
            String[] headers = {"Date / Label", "Revenue (RM)", "Units Sold", "Orders"};
            for (String headerText : headers) {
                Paragraph headerParagraph = new Paragraph(headerText);
                headerParagraph.setFont(bold);
                headerParagraph.setFontColor(ColorConstants.WHITE);

                Cell headerCell = new Cell();
                headerCell.add(headerParagraph);
                headerCell.setBackgroundColor(headerColor);
                headerCell.setTextAlignment(TextAlignment.CENTER);
                headerCell.setBorder(Border.NO_BORDER);

                table.addHeaderCell(headerCell);
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
            document.add(new Paragraph("-- END --")
                    .setFont(normal)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10));

            document.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void addChartImage(Document document, XYChart<String, Number> chart) {
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
            System.err.println(e.getMessage());
        }
    }

    public void export(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Sales Report");          //Set dialog title
        fileChooser.setInitialFileName("sales-report.pdf"); //Default filename

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

            generate(path);
        }
    }
}
