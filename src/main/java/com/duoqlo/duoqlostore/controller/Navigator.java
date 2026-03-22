package com.duoqlo.duoqlostore.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Stack;

public class Navigator {
    private static Stage stage;
    private static Stack<Scene> backStack = new Stack<>();
    private static Stack<Scene> forwardStack = new Stack<>();

    public static void setStage(Stage primaryStage){
        stage = primaryStage;
    }

    public static void goTo(Scene newScene){
        if(stage != null){
            Scene currentScene = stage.getScene();

            if(currentScene != null){
                backStack.push(stage.getScene());
            } else {
                System.out.println("Current Scene is null.");
            }

            forwardStack.clear();
            stage.setScene(newScene);
        } else {
            System.out.println("Stage not set!");
        }

    }

    public static void goBack() {
        if (stage != null && !backStack.isEmpty()) {
            forwardStack.push(stage.getScene());
            stage.setScene(backStack.pop());
        }
    }

    public static void goForward(){
        if(stage != null && !forwardStack.isEmpty()){
            backStack.push(stage.getScene());
            stage.setScene(forwardStack.pop());
        }
    }

    public static boolean backIsEmpty(){
        if(backStack.isEmpty()){
            return true;
        }

        return false;
    }

    public static boolean forwardIsEmpty(){
        if(forwardStack.isEmpty()){
            return true;
        }

        return false;
    }
}
