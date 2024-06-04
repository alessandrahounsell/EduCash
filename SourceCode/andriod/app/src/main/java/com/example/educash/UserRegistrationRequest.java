package com.example.educash;

public class UserRegistrationRequest {
    private String username;
    private String pword;
    private String pin;
    private boolean lip;

    public UserRegistrationRequest(String username, String password, String  pin, boolean lip) {
        this.username = username;
        this.pword = password;
        this.pin = pin;
        this.lip = false;
    }

    // Getter methods for fields
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return pword;
    }

    public String getPin() {
        return pin;
    }

    public boolean isLip() {
        return lip;
    }
}

