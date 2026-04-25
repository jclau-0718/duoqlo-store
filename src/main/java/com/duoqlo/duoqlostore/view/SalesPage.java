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
    import javafx.geometry.VPos;
    import javafx.scene.chart.*;
    import javafx.scene.control.*;
    import javafx.scene.layout.*;
    import javafx.scene.paint.Color;
    import javafx.stage.Stage;
    import org.kordamp.ikonli.javafx.FontIcon;
    
    class SalesTableView extends TableView<SalesRecord> {
        private FilterBy filter;
        private Freq frequency;
        private String labelTitle;
    
        private Runnable onDataChanged;
    
        public SalesTableView(FilterBy filter, Freq frequency) {
            this.filter = filter;
            this.frequency = frequency;

            setLabelTitle();
    
            buildColumns();
        }

        public void setOnDataChanged(Runnable onDataChanged) {
            this.onDataChanged = onDataChanged;
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

            labelCol.setPrefWidth(200);
            labelCol.setMaxWidth(200);
    
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
    
            if (onDataChanged != null) onDataChanged.run();
        }
    }
    
    public class SalesPage extends ApplicationPage {
        private AdminDashController controller;

        private GridPane contentGrid;

        private VBox tableDisplayBox = new VBox(10);
        private SalesTableView salesTable;

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

        private VBox chartBox;
        private XYChart<String, Number> revenueChart;
        private XYChart<String, Number> itemsChart;
        private XYChart<String, Number> ordersChart;

        private Runnable updateTitle;

        private String title = "DAILY SALES";

        public SalesPage(AdminDashController controller) {
            this.controller = controller;
        }

        public void setUpdateTitle(Runnable updateTitle) {
            this.updateTitle = updateTitle;
        }

        public void setTitle(String title) { this.title = title; }

        public FilterBy getCurrentFilter() { return this.currentFilter; }

        public Freq getCurrentFreq() { return this.currentFreq; }

        public HBox buildFilterSection() {
            int filterButtonWidth = 120;
            int freqButtonWidth = 80;
    
            filterGroup = new ToggleGroup();
    
            genderButton = new ToggleButton("By Genders");
            genderButton.setOnAction(e -> {
                if(genderButton.isSelected()) {
                    currentFilter = FilterBy.GENDER;

                    if (!isFrequencySelected()) {
                        currentFreq = Freq.NONE;

                        salesTable.update(currentFilter, currentFreq);

                        hideFilterCombo();
    
                        salesTable.setData(controller.getSalesByGenders());
                    } else {
                        salesTable.update(currentFilter, currentFreq);

                        showFilterCombo();
                    }
                } else {
                    currentFilter = FilterBy.NONE;

                    if(!isFrequencySelected()) {
                        currentFreq = Freq.DAILY;

                        salesTable.update(currentFilter, currentFreq);

                        //Base case - show daily sales (No filter and frequency selected
                        salesTable.setData(controller.getDailySales());
                    } else {
                        hideFilterCombo();
    
                        showFrequencyTable(currentFreq);
                    }
                }

                updateTitle.run();
            });
    
            categoryButton = new ToggleButton("By Categories");
            categoryButton.setOnAction(e -> {
                if(categoryButton.isSelected()) {
                    currentFilter = FilterBy.CATEGORY;

                    if (!isFrequencySelected()) {
                        currentFreq = Freq.NONE;

                        salesTable.update(currentFilter, currentFreq);

                        hideFilterCombo();
    
                        salesTable.setData(controller.getSalesByCategories());
                    } else {
                        showFilterCombo();
                    }
                } else {
                    currentFilter = FilterBy.NONE;

                    if(!isFrequencySelected()) {
                        currentFreq = Freq.DAILY;

                        salesTable.update(currentFilter, currentFreq);

                        //Base case - show daily sales (No filter and frequency selected
                        salesTable.setData(controller.getDailySales());
                    } else {
                        hideFilterCombo();
    
                        showFrequencyTable(currentFreq);
                    }
                }

                updateTitle.run();
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
                        showFilterCombo();  //Will show sales table with filter as well
                    }
                } else {
                    hideFilterCombo();
    
                    if(!isFilterSelected()) {
                        currentFreq = Freq.DAILY;
                        salesTable.update(currentFilter, currentFreq);

                        //Base case - show daily sales (No filter and frequency selected
                        salesTable.setData(controller.getDailySales());
                    } else {
                        currentFreq = Freq.NONE;
                        salesTable.update(currentFilter, currentFreq);

                        showFilterTable(currentFilter);
                    }
                }

                updateTitle.run();
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
                    hideFilterCombo();
    
                    if(!isFilterSelected()) {
                        currentFreq = Freq.DAILY;
                        salesTable.update(currentFilter, currentFreq);

                        //Base case - show daily sales (No filter and frequency selected
                        salesTable.setData(controller.getDailySales());
                    } else {
                        currentFreq = Freq.NONE;
                        salesTable.update(currentFilter, currentFreq);

                        showFilterTable(currentFilter);
                    }
                }

                updateTitle.run();
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
                    hideFilterCombo();
    
                    if(!isFilterSelected()) {
                        currentFreq = Freq.DAILY;
                        salesTable.update(currentFilter, currentFreq);

                        //Base case - show daily sales (No filter and frequency selected
                        salesTable.setData(controller.getDailySales());
                    } else {
                        currentFreq = Freq.NONE;
                        salesTable.update(currentFilter, currentFreq);

                        showFilterTable(currentFilter);
                    }
                }

                updateTitle.run();
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
            tableDisplayBox.getChildren().remove(filterCombo);
    
            filterCombo = new ComboBox<>();
    
            if(!tableDisplayBox.getChildren().contains(filterCombo)) {
                tableDisplayBox.getChildren().add(0, filterCombo);
            }
    
            setupFilerCombo();
        }
    
        private void hideFilterCombo() {
            tableDisplayBox.getChildren().remove(filterCombo);
        }

        private void fitTableHeight(TableView<?> table) {
            double headerHeight = 42.6;
            double rowHeight = 41.6;
            int rowCount = table.getItems().size();

            double totalHeight = headerHeight + (rowHeight * rowCount);

            table.setMinHeight(totalHeight);
            table.setPrefHeight(totalHeight);
            table.setMaxHeight(totalHeight);
        }
    
        private void showTable() {
            salesTable = new SalesTableView(currentFilter, currentFreq);
            salesTable.setData(controller.getDailySales());
            fitTableHeight(salesTable);

            currentFilter = FilterBy.NONE;
            currentFreq = Freq.DAILY;

            salesTable.setOnScroll(event -> event.consume());

            tableDisplayBox.getStyleClass().add("display-box");
            tableDisplayBox.setMaxHeight(Region.USE_PREF_SIZE);
            tableDisplayBox.setAlignment(Pos.TOP_CENTER);
            tableDisplayBox.getChildren().clear();
            tableDisplayBox.getChildren().add(salesTable);
        }
    
        public Button buildExportButton() {
            FontIcon pdfIcon = new FontIcon("far-file-pdf");
            pdfIcon.setIconSize(16);
            pdfIcon.setIconColor(Color.WHITE);
    
            Button exportButton = new Button("Export to PDF", pdfIcon);
            exportButton.getStyleClass().add("orange-button");
            exportButton.setOnAction(e -> {
                Stage stage = (Stage) exportButton.getScene().getWindow();

                SalesReportExporter exporter = new SalesReportExporter();
                exporter.setTitle(this.title);
                if(tableDisplayBox.getChildren().contains(filterCombo)) {
                    exporter.setSubTitle(filterCombo.getValue());
                }
                exporter.setAdminInfo(controller.getAdminName(), controller.getAdminId());
                exporter.setSalesData(controller.getSales());
                exporter.setCharts(revenueChart, itemsChart, ordersChart);

                exporter.export(stage);
            });
    
            return exportButton;
        }

        private void addChartToolTip(XYChart.Data<String, Number> data, String firstLine, String secondLine) {
            data.nodeProperty().addListener((obs, old, node) -> {
                if (node != null) {
                    Tooltip.install(node, new Tooltip(firstLine + "\n" + secondLine));
                }
            });
        }

        private void buildCharts(ObservableList<SalesRecord> data) {
            //Filter out the TOTAL row
            ObservableList<SalesRecord> chartData = data.filtered(r -> !r.isTotal());
    
            XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> itemsSeries = new XYChart.Series<>();
            XYChart.Series<String, Number> ordersSeries = new XYChart.Series<>();
    
            for (SalesRecord record : chartData) {
                String label = record.getLabelValue();
    
                XYChart.Data<String, Number> revenueData = new XYChart.Data<>(label, record.getRevenue());
                XYChart.Data<String, Number> itemsData   = new XYChart.Data<>(label, record.getTotalItems());
                XYChart.Data<String, Number> ordersData  = new XYChart.Data<>(label, record.getOrders());
    
                //Attach tooltips for points
                String revenueValue = showPrice(record.getRevenue());
                String itemsValue = record.getTotalItems() + " items";
                String ordersValue = record.getOrders() + " orders";

                addChartToolTip(revenueData, label, revenueValue);
                addChartToolTip(itemsData, label, itemsValue);
                addChartToolTip(ordersData, label, ordersValue);

                revenueSeries.getData().add(revenueData);
                itemsSeries.getData().add(itemsData);
                ordersSeries.getData().add(ordersData);
            }
    
            revenueChart.getData().clear();
            itemsChart.getData().clear();
            ordersChart.getData().clear();
    
            revenueChart.getData().add(revenueSeries);
            itemsChart.getData().add(itemsSeries);
            ordersChart.getData().add(ordersSeries);
        }

        private LineChart<String, Number> buildLineChart(String title, String xLabel, String yLabel) {
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel(xLabel);
            xAxis.setTickLabelRotation(-45); // Rotate labels so doesn't overlap

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(yLabel);
            yAxis.setForceZeroInRange(true);
            yAxis.setMinorTickVisible(true);

            xAxis.setTickLabelFont(javafx.scene.text.Font.font("Arial", 12));
            yAxis.setTickLabelFont(javafx.scene.text.Font.font("Arial", 12));

            LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
            chart.setTitle(title);
            chart.setLegendVisible(false);
            chart.setAnimated(false);
            chart.setPrefHeight(400);
            chart.getStyleClass().add("sales-chart");
    
            return chart;
        }

        private BarChart<String, Number> buildBarChart(String title, String xLabel, String yLabel) {
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel(xLabel);

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(yLabel);
            yAxis.setForceZeroInRange(true);

            xAxis.setTickLabelFont(javafx.scene.text.Font.font("Arial", 12));
            yAxis.setTickLabelFont(javafx.scene.text.Font.font("Arial", 12));

            BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
            chart.setTitle(title);
            chart.setLegendVisible(false);
            chart.setAnimated(false);
            chart.setPrefHeight(400);
            chart.getStyleClass().add("sales-chart");

            return chart;
        }
    
        private void initCharts() {
            if (chartBox == null) {
                chartBox = new VBox(20); // create only once
            }

            if (currentFreq.equals(Freq.NONE)) {
                String title;

                if (currentFilter.equals(FilterBy.CATEGORY)) {
                    title = "Category";
                } else if (currentFilter.equals(FilterBy.GENDER)){
                    title = "Gender";
                } else {
                    title = "Date";
                }

                revenueChart = buildBarChart("Revenue (RM)", title, "RM");
                itemsChart   = buildBarChart("Items Sold", title, "Num of Items");
                ordersChart  = buildBarChart("Orders", title, "Num of Orders");
            } else {
                revenueChart = buildLineChart("Revenue (RM)", "Date", "RM");
                itemsChart   = buildLineChart("Items Sold",   "Date", "Num of Items");
                ordersChart  = buildLineChart("Orders",       "Date", "Num of Orders");
            }
    
            revenueChart.getStyleClass().add("sales-chart");
            itemsChart.getStyleClass().add("sales-chart");
            ordersChart.getStyleClass().add("sales-chart");

            chartBox.getStyleClass().add("display-box");
            chartBox.getChildren().setAll(revenueChart, itemsChart, ordersChart);
        }

        private void buildContentGrid() {
            contentGrid = new GridPane();
            contentGrid.setHgap(30);

            contentGrid.add(tableDisplayBox, 0, 0);
            contentGrid.add(chartBox, 1, 0);

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(45);

            ColumnConstraints col2 = new ColumnConstraints();
            col2.setPercentWidth(55);

            contentGrid.getColumnConstraints().addAll(col1, col2);
            GridPane.setValignment(tableDisplayBox, VPos.TOP);
            GridPane.setMargin(tableDisplayBox, new Insets(0, 0, 0, 22));
            GridPane.setMargin(chartBox, new Insets(0, 22, 0, 0));
        }

        public VBox getContent() {
            showTable();
            initCharts();
            buildCharts(salesTable.getItems());

            // Wire chart updates to table data changes
            salesTable.setOnDataChanged(() -> {
                initCharts();
                buildCharts(salesTable.getItems());
                fitTableHeight(salesTable);
            });

            buildContentGrid();

            VBox bodyVBox = new VBox(20);
            bodyVBox.getChildren().addAll(contentGrid);
            bodyVBox.setAlignment(Pos.CENTER);
    
            return bodyVBox;
        }
    }
