package com.duoqlo.duoqlostore.controller;

import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;


public class LogInPageHandler implements EventHandler<ActionEvent> {
       @Override
       public void handle(ActionEvent e){
           if(e.getSource() instanceof Button button){
                switch(button.getText()){
                    case "Log In" -> System.out.println("Log In Button Clicked");
                    case "Sign Up?" -> System.out.println("Sign Up Button Clicked");
                }
           }
       }
}
