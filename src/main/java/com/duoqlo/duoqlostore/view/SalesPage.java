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

    public void setData(ObservableList<SalesRecord> salesData) {
        double totalRevenue = 0;
        int totalItemsSold = 0;
        int totalOrders = 0;

        for(SalesRecord salesRecord: salesData) {
            totalRevenue += salesRecord.getRevenue();
            totalItemsSold += salesRecord.getTotalItems();
            totalOrders += salesRecord.getOrders();
        }

        SalesRecord totalRow = new SalesRecord("TOTAL", totalRevenue, totalItemsSold, totalOrders);
        
        salesData.add(totalRow);
        
        setItems(salesData);
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

    public SalesPage(AdminDashController controller) {
        this.controller = controller;
    }

    public HBox getFilterSection() {
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

                    salesTable.setData(controller.getSalesByGenders());
                } else {
                    showFilterCombo();

//                    showFrequencyTable(currentFreq);
                }
            } else {
                currentFilter = FilterBy.NONE;
                salesTable.update(currentFilter, currentFreq);

                if(!isFrequencySelected()) {
                    //Base case - show daily sales (No filter and frequency selected
                    salesTable.setData(controller.getDailySales());
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

                    salesTable.setData(controller.getSalesByCategories());
                } else {
                    showFilterCombo();

//                    showFrequencyTable(currentFreq);
                }
            } else {
                currentFilter = FilterBy.NONE;
                salesTable.update(currentFilter, currentFreq);

                if(!isFrequencySelected()) {
                    //Base case - show daily sales (No filter and frequency selected
                    salesTable.setData(controller.getDailySales());
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

                    salesTable.setData(controller.getDailySales());
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
                    salesTable.setData(controller.getDailySales());
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

                    salesTable.setData(controller.getWeeklySales());
                } else {
                    showFilterCombo();  //Will show sales table with filter as well
                }

            } else {
                currentFreq = Freq.NONE;
                salesTable.update(currentFilter, currentFreq);

                hideFilterCombo();

                if(!isFilterSelected()) {
                    //Base case - show daily sales (No filter and frequency selected
                    salesTable.setData(controller.getDailySales());
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

                    salesTable.setData(controller.getMonthlySales());
                } else {
                    showFilterCombo();  //Will show sales table with filter as well
                }

            } else {
                currentFreq = Freq.NONE;
                salesTable.update(currentFilter, currentFreq);

                hideFilterCombo();

                if(!isFilterSelected()) {
                    //Base case - show daily sales (No filter and frequency selected
                    salesTable.setData(controller.getDailySales());
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
            case Freq.DAILY -> salesTable.setData(controller.getDailySales());
            case Freq.WEEKLY -> salesTable.setData(controller.getWeeklySales());
            case Freq.MONTHLY -> salesTable.setData(controller.getMonthlySales());
        }
    }

    private void showFilterTable(FilterBy filter) {
        switch(filter) {
            case FilterBy.GENDER -> salesTable.setData(controller.getSalesByGenders());
            case FilterBy.CATEGORY -> salesTable.setData(controller.getSalesByCategories());
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
            ObservableList<SalesRecord> filtered = controller.getSalesByFilterAndFreq(currentFilter, currentFreq, value);

            if(!filtered.isEmpty()) {
                salesTable.setData(filtered);
            } else {
                salesTable.setData(FXCollections.observableArrayList());

                Label emptyLabel = new Label("No sales data");
                emptyLabel.getStyleClass().add("empty-label");
                salesTable.setPlaceholder(emptyLabel);
            }
        }

        filterCombo.valueProperty().addListener(filterComboListener);
    }

    private final ChangeListener<String> filterComboListener = (obs, oldVal, newVal) -> {
        if (newVal != null) {
            ObservableList<SalesRecord> filtered = controller.getSalesByFilterAndFreq(currentFilter, currentFreq, newVal);;

            if(!filtered.isEmpty()) {
                salesTable.setData(filtered);
            } else {
                salesTable.setData(FXCollections.observableArrayList());

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
        salesTable.setData(controller.getDailySales());

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
        displayBox = new VBox();

        showTable();

        Button exportButton = buildExportButton();

        VBox bodyVBox = new VBox(10);
        bodyVBox.getChildren().addAll(displayBox, exportButton);
        bodyVBox.setAlignment(Pos.CENTER);

        return bodyVBox;
    }
}
