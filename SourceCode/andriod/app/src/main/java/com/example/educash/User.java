package com.example.educash;

public class User {
    private  String username;

    private boolean lip;

    private boolean negAllowed;

    public User(String username, boolean lip, boolean negAllowed) {
        this.username = username;
        this.lip = lip;
        this.negAllowed = negAllowed;
    }

    public String getUsername() {
        return username;
    }

    public boolean getLip() {
        return lip;
    }

    public boolean getNegAllowed() {
        return negAllowed;
    }
}
