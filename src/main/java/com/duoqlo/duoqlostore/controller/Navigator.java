package com.duoqlo.duoqlostore.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Stack;

public class Navigator {
    private static Stage stage;
    private static Stack<Scene> history = new Stack<>();

    public static void setStage(Stage primaryStage){
        stage = primaryStage;
    }

    public static void navigate(Scene newScene){

    }

}
