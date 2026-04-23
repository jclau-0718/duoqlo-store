package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.ConnectDB;
import com.duoqlo.duoqlostore.model.TableCreator;
import com.duoqlo.duoqlostore.model.UserDAO;
import com.duoqlo.duoqlostore.view.*;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Navigator.setStage(stage);

        //LogIn Page
        LogInPage logInPage = new LogInPage();

        //Create database
        TableCreator.createTable();
        TableCreator.initSequence();
        TableCreator.initTable();

        //Initialize admin
        UserDAO userDAO = new UserDAO();
        if(!userDAO.adminExists()) {
            userDAO.initAdmin();
        }

        stage.setTitle("DUOQLO");
        Navigator.goTo(logInPage.initialize());
        stage.show();
        stage.centerOnScreen();
        stage.setMaximized(true);
    }

    @Override
    public void stop() {
        ConnectDB.closeConnection();
    }

    public static void main(String[] args){
        launch(args);
    }
}
