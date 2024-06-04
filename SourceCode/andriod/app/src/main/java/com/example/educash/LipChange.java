package com.example.educash;

import com.google.gson.annotations.SerializedName;

public class LipChange {
    @SerializedName("username")
    private String username;

    @SerializedName("lip")
    private boolean lip;

    public String getUsername() {
        return username;
    }

    public boolean isLip() {
        return lip;
    }
}

