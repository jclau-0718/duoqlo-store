package com.duoqlo.duoqlostore.controller;

import com.duoqlo.duoqlostore.model.User;
import com.duoqlo.duoqlostore.model.UserDAO;

public class AuthService {
    UserDAO userDAO = new UserDAO();

    public User login(String username, String password){
        int userID = userDAO.getIDByUsername(username);

        if(userDAO.checkCredentials(username, password)){
            return new User(userID);
        }

        return null;
    }

}
