package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.model.Order;
import com.duoqlo.duoqlostore.model.OrderItem;
import com.duoqlo.duoqlostore.model.User;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.font.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.properties.UnitValue;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class ReceiptExporter {
    private DeviceRgb ORANGE = new DeviceRgb(254, 108, 1);

    private User user;
    private Order order;

    private PdfFont bold;
    private PdfFont normal;

    private final int logoWidth = 150;

    public ReceiptExporter(User user, Order order) {
        this.user = user;
        this.order = order;

        try {
            bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Cell buildInfoCell(String title) {
        Paragraph titlePar = new Paragraph(title);
        titlePar.setFont(bold);
        titlePar.setFontSize(12);

        SolidLine lineDrawer = new SolidLine();
        lineDrawer.setColor(ORANGE);
        lineDrawer.setLineWidth(1f);

        LineSeparator line = new LineSeparator(lineDrawer);
        line.setMarginTop(2);
        line.setMarginBottom(5);

        Cell infoCell = new Cell();
        infoCell.add(titlePar);
        infoCell.add(line);

        return infoCell;
    }

    private Paragraph normalParagraph(String text) {
        Paragraph paragraph = new Paragraph(text);
        paragraph.setFont(normal);
        paragraph.setFontSize(10);

        return paragraph;
    }

    private Paragraph labelValueParagraph(String label, String value) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Text(label));
        paragraph.add(new Text(value).setBold());
        paragraph.setFont(normal);
        paragraph.setFontSize(10);

        return paragraph;
    }

    private Table buildInfoSection() {
        //Customer Info
        Paragraph name = labelValueParagraph("Name: ", user.getFullName());

        Paragraph email = normalParagraph("Email: " + user.getEmail());

        Paragraph address = normalParagraph("Shipping Address:" + user.getFullAddress());

        Cell customerCell = buildInfoCell("Customer Info");
        customerCell.add(name);
        customerCell.add(email);
        customerCell.add(address);
        customerCell.setBorder(Border.NO_BORDER);

        //Order Info
        Paragraph orderId = normalParagraph("Order ID: " + order.getOrderId());

        Paragraph orderDate = normalParagraph("Order Date: " + order.getOrderDateString());
        Cell orderCell = buildInfoCell("Order Info");
        orderCell.add(orderId);
        orderCell.add(orderDate);
        orderCell.setBorder(Border.NO_BORDER);
        orderCell.setPaddingLeft(40);

        float[] infoTableWidths = {300F, 1};
        Table infoTable = new Table(infoTableWidths).useAllAvailableWidth();
        infoTable.addCell(customerCell);
        infoTable.addCell(orderCell);
        infoTable.setBorder(Border.NO_BORDER);
        infoTable.setMarginBottom(10);

        return infoTable;
    }


    private void addHeaderCell(Table table, String text) {
        Cell cell = new Cell()
                .add(new Paragraph(text)
                .setFont(bold)
                .setFontSize(12))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(Border.NO_BORDER)
                .setBorderBottom(new SolidBorder(ORANGE,1)); // line under header

        table.addCell(cell);
    }

    private Cell createCell(String text, TextAlignment align) {
        return new Cell()
                .add(new Paragraph(text)
                .setFont(normal)
                .setFontSize(10))
                .setTextAlignment(align)
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(8)
                .setPaddingBottom(8);
    }

    private Table buildOrderTable() {
        float[] columnWidths = {4, 1, 1.5f, 2, 2};
        Table table = new Table(columnWidths);
        table.useAllAvailableWidth();

        addHeaderCell(table, "NAME");
        addHeaderCell(table, "SIZE");
        addHeaderCell(table, "QUANTITY");
        addHeaderCell(table, "UNIT PRICE");
        addHeaderCell(table, "SUB-TOTAL (RM)");

        for (OrderItem item : order.getOrderItemList()) {

            double unitPrice = item.getSubTotal();
            int qty = item.getQuantity();
            double subtotal = unitPrice * qty;

            //Name
            table.addCell(createCell(item.getProduct().getName(), TextAlignment.LEFT));

            //Size
            table.addCell(createCell(item.getSize(), TextAlignment.CENTER));

            //Quantity
            table.addCell(createCell(String.valueOf(qty), TextAlignment.RIGHT));

            //Unit Price
            table.addCell(createCell(String.format("%.2f", unitPrice), TextAlignment.RIGHT));

            //Subtotal (RIGHT)
            table.addCell(createCell(String.format("%.2f", subtotal), TextAlignment.RIGHT));

            Cell bottomLine = new Cell(1, 5)
                    .add(new Paragraph(""))
                    .setBorder(Border.NO_BORDER)
                    .setBorderTop(new SolidBorder(ORANGE, 1));
            table.addCell(bottomLine);
        }

        return table;
    }

    private Table buildTotalSection() {
        //Payment details
        Paragraph paymentMethod = normalParagraph("Payment Method: CREDIT CARD");

        Paragraph statusPar = labelValueParagraph("Status: ", "PAID");

        Cell paymentCell = new Cell();
        paymentCell.add(paymentMethod);
        paymentCell.add(statusPar);
        paymentCell.setTextAlignment(TextAlignment.LEFT);
        paymentCell.setBorder(Border.NO_BORDER);

        double shipping = 50;
        double total = order.getTotalPrice();
        double subtotal =  total - shipping;

        Paragraph shipFee = normalParagraph("Shipping Fee: " + String.format("RM %.2f", shipping));

        Paragraph subTotal = normalParagraph("Sub-Total: " +String.format("RM %.2f", subtotal));

        Paragraph totalPar = labelValueParagraph("Total: ", String.format("RM %.2f", total));

        Cell totalCell = new Cell();
        totalCell.add(shipFee);
        totalCell.add(subTotal);
        totalCell.add(totalPar);
        totalCell.setTextAlignment(TextAlignment.RIGHT);
        totalCell.setBorder(Border.NO_BORDER);
//        totalCell.setMarginLeft(80);

        Table table = new Table(2);
        table.useAllAvailableWidth();
        table.setBorder(Border.NO_BORDER);
        table.setMarginTop(20);
        table.addCell(paymentCell);
        table.addCell(totalCell);

        return table;
    }

    private void generate(String filePath) {
        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.setMargins(30, 40, 30, 40);

            URL url = getClass().getResource("/logo.png");

            ImageData logoData = ImageDataFactory.create(url);

            Image logo = new Image(logoData);
            logo.setWidth(logoWidth);
            logo.setMinWidth(logoWidth);
            logo.setMaxWidth(logoWidth);
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(logo);

            Paragraph receiptTitle = new Paragraph("RECEIPT");
            receiptTitle.setFont(bold);
            receiptTitle.setFontSize(25);
            receiptTitle.setTextAlignment(TextAlignment.CENTER);
            document.add(receiptTitle);

            Table infoSection = buildInfoSection();
            document.add(infoSection);

            Table ordertable = buildOrderTable();
            document.add(ordertable);

            Table totalSection = buildTotalSection();
            document.add(totalSection);

            Paragraph footer = new Paragraph("Thank you for your purchase!");
            footer.setFont(normal);
            footer.setFontSize(10);
            footer.setTextAlignment(TextAlignment.CENTER);
            footer.setMarginTop(10);
            document.add(footer);

            document.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void export(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Receipt");               //Set dialog title
        fileChooser.setInitialFileName("receipt.pdf"); //Default filename

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
