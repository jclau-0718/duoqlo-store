package com.duoqlo.duoqlostore;

import javafx.scene.paint.Color;

import java.time.format.DateTimeFormatter;

public abstract class AppConfig {
    public static Color themeColor = Color.web("FE6C01");

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
}
