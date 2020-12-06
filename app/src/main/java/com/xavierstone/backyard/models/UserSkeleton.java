package com.xavierstone.backyard.models;

public class UserSkeleton {
    private String name;
    private String email;
    private String hash;

    public UserSkeleton(String name, String email, String hash) {
        this.name = name;
        this.email = email;
        this.hash = hash;
    }

    public String getName() {return name;}
    public String getEmail() {return email;}
    public String getHash() {return hash;}
}
