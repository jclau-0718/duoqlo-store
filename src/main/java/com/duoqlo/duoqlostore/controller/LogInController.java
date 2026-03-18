package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.User;
import com.duoqlo.duoqlostore.model.UserDAO;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.Parent;

public class LogInController implements EventHandler<ActionEvent> {
    private UserDAO userDAO = new UserDAO();

   @Override
   public void handle(ActionEvent e){
       if(e.getSource() instanceof Button button){
            switch(button.getText()){
                case "Log In" -> System.out.println("Log In Button Clicked");
                case "Sign Up?" -> System.out.println("Sign Up Button Clicked");
            }
       }
   }

   public boolean checkUsername(String username){
       return userDAO.usernameExists(username);
   }

   public boolean checkCredentials(String username, String password){
       return userDAO.checkCredentials(username, password);
   }


   public Parent openDashboard(String username, String password){
       System.out.println("Trying login: " + username + ", " + password);

       //Authentication valid
       if(checkCredentials(username, password)){
           int userID = userDAO.getIDByUsername(username);
           String role;

            role = userDAO.getRole(userID);
            if(role != null && role.equals("CUSTOMER")){
                return SceneManager.createUserDash();
            } else if(role != null && role.equals("ADMIN")){
                return SceneManager.createAdminDash();
            } else {
                System.out.println("Role not recognized for userID: " + userID);
            }
       }
       else {
           System.out.println("Invalid username or password!");
       }

       return null;
   }
}
