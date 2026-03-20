package com.duoqlo.duoqlostore.view;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class AdminDashboard {
    public HBox createPage(){
        Label label = new Label("ADMIN PAGE");
        return new HBox(label);
    }
}
