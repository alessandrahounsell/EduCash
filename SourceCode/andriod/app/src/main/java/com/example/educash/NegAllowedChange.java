package com.example.educash;

import com.google.gson.annotations.SerializedName;
public class NegAllowedChange {

    @SerializedName("username")
    private String username;

    @SerializedName("NegAllowed")
    private boolean NegAllowed;

    public String getUsername() {
        return username;
    }

    public boolean isNegAllowed() {
        return NegAllowed;
    }
}
