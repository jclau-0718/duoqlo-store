package com.duoqlo.duoqlostore.view;

import com.duoqlo.duoqlostore.controller.AdminDashController;
import com.duoqlo.duoqlostore.model.FilterBy;
import com.duoqlo.duoqlostore.model.Freq;
import com.duoqlo.duoqlostore.model.SalesRecord;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

class SalesTableView extends TableView<SalesRecord> {

    private FilterBy filter;
    private Freq frequency;
    private String labelTitle;

    public SalesTableView(FilterBy filter, Freq frequency) {
        this.filter = filter;
        this.frequency = frequency;

        setLabelTitle();
        System.out.println(labelTitle);

        buildColumns();
    }

    private void setLabelTitle() {
        if(frequency.equals(Freq.NONE)) {
            switch(filter) {
                case FilterBy.GENDER -> labelTitle = "Genders";
                case FilterBy.CATEGORY -> labelTitle = "Categories";
                default -> labelTitle = "Date";
            }
        } else if(frequency.equals(Freq.DAILY)) {
            labelTitle = "Date";
        } else {
            labelTitle = "Date Range";
        }
    }

    public void update(FilterBy filter, Freq frequency) {
        this.filter = filter;
        this.frequency = frequency;

        setLabelTitle();

        getColumns().clear();
        buildColumns();
    }

    private void buildColumns() {
        TableColumn<SalesRecord, String> labelCol = new TableColumn<>(labelTitle);
        labelCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getLabelValue()));

        TableColumn<SalesRecord, Double> revenueCol = new TableColumn<>("Revenue (RM)");
        revenueCol.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getRevenue()).asObject());
        //Set two decimal places
        revenueCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", value));
                }
            }
        });

        TableColumn<SalesRecord, Integer> itemsSoldCol = new TableColumn<>("Items Sold");
        itemsSoldCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getTotalItems()).asObject());

        TableColumn<SalesRecord, Integer> ordersCol = new TableColumn<>("Orders");
        ordersCol.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getOrders()).asObject());

        getColumns().addAll(labelCol, revenueCol, itemsSoldCol, ordersCol);

        TableUtils.highlightZeroRows(this, SalesRecord::getTotalItems);

        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }
}

public class SalesPage {
    private AdminDashController controller;

    private VBox displayBox;

    //Filter Group
    private ToggleGroup filterGroup;
    private ToggleButton genderButton;
    private ToggleButton categoryButton;

    private FilterBy currentFilter = FilterBy.NONE;

    //Frequency Group
    private ToggleGroup freqGroup;
    private ToggleButton dailyButton;
    private ToggleButton weeklyButton;
    private ToggleButton monthlyButton;

    private Freq currentFreq = Freq.NONE;

    private ComboBox<String> filterCombo;

    private SalesTableView salesTable;

    private int sidePad = 35;

    public SalesPage(AdminDashController controller) {
        this.controller = controller;
    }

    private HBox buildTableTitle() {
        Label titleLabel = new Label("Sales Report");
        titleLabel.getStyleClass().add("title");

        Region rightLine = new Region();
        rightLine.setStyle("-fx-background-color: #808080");
        rightLine.setMaxHeight(3);

        Region leftLine = new Region();
        leftLine.setStyle("-fx-background-color: #808080");
        leftLine.setMaxHeight(3);

        HBox titleBox = new HBox(20, leftLine, titleLabel, rightLine);
        titleBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(rightLine, Priority.ALWAYS);
        HBox.setHgrow(leftLine, Priority.ALWAYS);

        return titleBox;
    }

    private HBox buildFilterSection() {
        int filterButtonWidth = 120;
        int freqButtonWidth = 80;

        filterGroup = new ToggleGroup();

        genderButton = new ToggleButton("By Genders");
        genderButton.setOnAction(e -> {
            if(genderButton.isSelected()) {
                currentFilter = FilterBy.GENDER;
                salesTable.update(currentFilter, currentFreq);

                if (!isFrequencySelected()) {
                    hideFilterCombo();

                    salesTable.setItems(controller.getSalesByGenders());
                } else {
                    showFilterCombo();

//                    showFrequencyTable(currentFreq);
                }
            } else {
                currentFilter = FilterBy.NONE;
                salesTable.update(currentFilter, currentFreq);

                if(!isFrequencySelected()) {
                    //Base case - show daily sales (No filter and frequency selected
                    salesTable.setItems(controller.getDailySales());
                } else {
                    hideFilterCombo();

                    showFrequencyTable(currentFreq);
                }
            }
        });

        categoryButton = new ToggleButton("By Categories");
        categoryButton.setOnAction(e -> {
            if(categoryButton.isSelected()) {
                currentFilter = FilterBy.CATEGORY;
                salesTable.update(currentFilter, currentFreq);

                if (!isFrequencySelected()) {
                    hideFilterCombo();

                    salesTable.setItems(controller.getSalesByCategories());
                } else {
                    showFilterCombo();

//                    showFrequencyTable(currentFreq);
                }
            } else {
                currentFilter = FilterBy.NONE;
                salesTable.update(currentFilter, currentFreq);

                if(!isFrequencySelected()) {
                    //Base case - show daily sales (No filter and frequency selected
                    salesTable.setItems(controller.getDailySales());
                } else {
                    hideFilterCombo();

                    showFrequencyTable(currentFreq);
                }
            }
        });

        genderButton.setToggleGroup(filterGroup);
        categoryButton.setToggleGroup(filterGroup);

        freqGroup = new ToggleGroup();

        dailyButton = new ToggleButton("Daily");
        dailyButton.setOnAction(e -> {
            if(dailyButton.isSelected()) {
                currentFreq = Freq.DAILY;
                salesTable.update(currentFilter, currentFreq);

                if(!isFilterSelected()) {
                    hideFilterCombo();

                    salesTable.setItems(controller.getDailySales());
                } else {
                    showFilterCombo();

                    //Will do controller.getSalesByFilter as well
                }
            } else {
                currentFreq = Freq.NONE;
                salesTable.update(currentFilter, currentFreq);

                hideFilterCombo();

                if(!isFilterSelected()) {
                    //Base case - show daily sales (No filter and frequency selected
                    salesTable.setItems(controller.getDailySales());
                } else {
                    showFilterTable(currentFilter);
                }
            }
        });

        weeklyButton = new ToggleButton("Weekly");
        weeklyButton.setOnAction(e -> {
            if(weeklyButton.isSelected()) {
                currentFreq = Freq.WEEKLY;
                salesTable.update(currentFilter, currentFreq);

                if(!isFilterSelected()) {
                    hideFilterCombo();

                    salesTable.setItems(controller.getWeeklySales());
                } else {
                    showFilterCombo();
                }

            } else {
                currentFreq = Freq.NONE;
                salesTable.update(currentFilter, currentFreq);

                hideFilterCombo();

                if(!isFilterSelected()) {
                    //Base case - show daily sales (No filter and frequency selected
                    salesTable.setItems(controller.getDailySales());
                } else {
                    showFilterTable(currentFilter);
                }
            }
        });

        monthlyButton = new ToggleButton("Monthly");
        monthlyButton.setOnAction(e -> {
            if(monthlyButton.isSelected()) {
                currentFreq = Freq.MONTHLY;
                salesTable.update(currentFilter, currentFreq);

                if(!isFilterSelected()) {
                    hideFilterCombo();

                    salesTable.setItems(controller.getMonthlySales());
                } else {
                    showFilterCombo();

                    //Will do controller.getSalesByFilter as well
                }

            } else {
                currentFreq = Freq.NONE;
                salesTable.update(currentFilter, currentFreq);

                hideFilterCombo();

                if(!isFilterSelected()) {
                    //Base case - show daily sales (No filter and frequency selected
                    salesTable.setItems(controller.getDailySales());
                } else {
                    showFilterTable(currentFilter);
                }
            }
        });

        dailyButton.setToggleGroup(freqGroup);
        weeklyButton.setToggleGroup(freqGroup);
        monthlyButton.setToggleGroup(freqGroup);

        //Set filter button width
        genderButton.setPrefWidth(filterButtonWidth);
        categoryButton.setPrefWidth(filterButtonWidth);

        //Set frequency button width
        dailyButton.setPrefWidth(freqButtonWidth);
        weeklyButton.setPrefWidth(freqButtonWidth);
        monthlyButton.setPrefWidth(freqButtonWidth);

        HBox filterBox = new HBox(10);
        filterBox.getChildren().addAll(genderButton, categoryButton);

        HBox frequencyBox = new HBox(10);
        frequencyBox.getChildren().addAll(dailyButton, weeklyButton, monthlyButton);

        HBox filterSection = new HBox(40);
        filterSection.getChildren().addAll(filterBox, frequencyBox);

        return filterSection;
    }

    private void showFrequencyTable(Freq freq) {
        switch(freq) {
            case Freq.DAILY -> salesTable.setItems(controller.getDailySales());
            case Freq.WEEKLY -> salesTable.setItems(controller.getWeeklySales());
            case Freq.MONTHLY -> salesTable.setItems(controller.getMonthlySales());
        }
    }

    private void showFilterTable(FilterBy filter) {
        switch(filter) {
            case FilterBy.GENDER -> salesTable.setItems(controller.getSalesByGenders());
            case FilterBy.CATEGORY -> salesTable.setItems(controller.getSalesByCategories());
        }
    }

    private boolean isFilterSelected() {
        return filterGroup.getSelectedToggle() != null;
    }

    private boolean isFrequencySelected() {
        return freqGroup.getSelectedToggle() != null;
    }

    private void setupFilerCombo() {
        //Remove previous listeners to prevent stacking listeners
        filterCombo.valueProperty().removeListener(filterComboListener);

        if(currentFilter.equals(FilterBy.GENDER)) {
            filterCombo.setItems(controller.getAllGenders());
        } else {
            filterCombo.setItems(controller.getAllCategories());
        }

        //Set initial value as initial option
        filterCombo.getSelectionModel().select(0);

        String value = filterCombo.getValue();
        if (value != null) {
            ObservableList<SalesRecord> filtered = controller.getSalesByFilter(currentFilter, value);

            if(!filtered.isEmpty()) {
                salesTable.setItems(filtered);
            } else {
                salesTable.setItems(FXCollections.observableArrayList());

                Label emptyLabel = new Label("No sales data");
                emptyLabel.getStyleClass().add("empty-label");
                salesTable.setPlaceholder(emptyLabel);
            }
        }

        filterCombo.valueProperty().addListener(filterComboListener);
    }

    private final ChangeListener<String> filterComboListener = (obs, oldVal, newVal) -> {
        if (newVal != null) {
            ObservableList<SalesRecord> filtered = controller.getSalesByFilter(currentFilter, newVal);

            if(!filtered.isEmpty()) {
                salesTable.setItems(filtered);
            } else {
                salesTable.setItems(FXCollections.observableArrayList());

                Label emptyLabel = new Label("No sales data");
                emptyLabel.getStyleClass().add("empty-label");
                salesTable.setPlaceholder(emptyLabel);
            }
        }
    };

    private void showFilterCombo() {
        displayBox.getChildren().remove(filterCombo);

        filterCombo = new ComboBox<>();

        if(!displayBox.getChildren().contains(filterCombo)) {
            displayBox.getChildren().add(0, filterCombo);
        }

        setupFilerCombo();
    }

    private void hideFilterCombo() {
        displayBox.getChildren().remove(filterCombo);
    }

    private void showTable() {
        salesTable = new SalesTableView(currentFilter, currentFreq);
        salesTable.setItems(controller.getDailySales());

        displayBox.getChildren().clear();
        displayBox.getChildren().add(salesTable);
    }

    private Button buildExportButton() {
        FontIcon pdfIcon = new FontIcon("far-file-pdf");
        pdfIcon.setIconSize(16);
        pdfIcon.setIconColor(Color.WHITE);

        Button exportButton = new Button("Export as PDF", pdfIcon);
        exportButton.getStyleClass().add("orange-button");
        exportButton.setOnAction(e -> {
            ExportPDF.export(controller.getSales(), "test", "report.pdf");
        });

        return exportButton;
    }

    public VBox getContent() {
        HBox titleBox = buildTableTitle();

        HBox filterSection = buildFilterSection();

        displayBox = new VBox(10);
        displayBox.getStyleClass().add("display-box");

        showTable();

        String totalRevText = "Total Revenue: RM " + String.format("%.2f", controller.getTotalRevenue());
        Label totalRevLabel = new Label(totalRevText);
        totalRevLabel.getStyleClass().add("total-rev");

        displayBox.getChildren().add(totalRevLabel);

        Button exportButton = buildExportButton();

        VBox bodyVBox = new VBox(10, titleBox, filterSection, displayBox, exportButton);
        bodyVBox.setAlignment(Pos.CENTER);
        bodyVBox.setPadding(new Insets(0, sidePad, 20, sidePad));
        VBox.setMargin(titleBox, new Insets(10, 0, 10, 0));

        return bodyVBox;
    }
}
