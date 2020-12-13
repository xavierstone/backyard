package com.xavierstone.backyard.db;

import com.xavierstone.backyard.models.User;

public class LoginResponse {
    User userData;
    boolean success;

    public LoginResponse(boolean success){
        userData = null;
        this.success = success;
    }

    public LoginResponse(boolean success, User user){
        userData = user;
        this.success = success;
    }

    public boolean getResult() { return success; }
    public User getData() { return userData; }
}
