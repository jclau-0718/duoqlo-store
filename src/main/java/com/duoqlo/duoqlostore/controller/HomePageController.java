package com.duoqlo.duoqlostore.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class HomePageController implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent e){
        if(e.getSource() instanceof Button button) {
            switch (button.getText()) {
                case "Add to Cart" -> System.out.println("Add to Cart Button Clicked");
                case "Buy Now" -> System.out.println("Buy Now Button Clicked");
            }
        }
    }
}
