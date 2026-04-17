package com.duoqlo.duoqlostore.view;

import javafx.scene.control.*;

import java.util.function.Function;

public class TableUtils {
    public static <S, T> void addColToolTip(TableColumn<S, T> column) {
        column.setCellFactory(col -> new TableCell<>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(T value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    String text = value.toString();
                    setText(text);

                    tooltip.setText(text);
                    setTooltip(tooltip);
                }
            }
        });
    }

    public static <S> void addTwoDecimalFormatting(TableColumn<S, Double> column) {
        column.setCellFactory(col -> new TableCell<>() {

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
    }

    public static <T> void highlightZeroRows(TableView<T> table,
                                             Function<T, Number> extractor) {

        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                } else {
                    Number value = extractor.apply(item);

                    if (value != null && value.doubleValue() == 0.0) {
                        getStyleClass().add("zero-row");
                    } else {
                        getStyleClass().remove("zero-row");
                    }
                }
            }
        });
    }
}