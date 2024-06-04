package com.example.educash;



import static com.example.educash.Login_Page.API_RESPONSE_KEY;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "YourPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Check if the user has a token
        String token = sharedPreferences.getString(API_RESPONSE_KEY, "");
        Log.d("YourActivity", "Stored API Response: " + token);


        if (token != null) {
            // User has a token, fetch Lip information
            fetchLipInfo(token);
        } else {
            // No token found, navigate to Login_Page activity
            startActivity(new Intent(MainActivity.this, Login_Page.class));
            finish(); // Finish the current activity to prevent the user from returning here
        }
    }

    private void fetchLipInfo(String token) {
        ApiManager apiManager = new ApiManager(MainActivity.this);
        apiManager.getUserLipInfo(token, new Callback<List<LIPInfo>>() {
            @Override
            public void onResponse(Call<List<LIPInfo>> call, Response<List<LIPInfo>> response) {
                if (response.isSuccessful()) {
                    // Check Lip info
                    List<LIPInfo> lipInfoList = response.body();
                    if (lipInfoList != null && !lipInfoList.isEmpty()) {
                        LIPInfo lipInfo = lipInfoList.get(0);
                        if (lipInfo.isLip()) {
                            // Lip is enabled, navigate to Pin_Login activity
                            startActivity(new Intent(MainActivity.this, Pin_Login.class));
                        } else {
                            // Lip is not enabled, navigate to HomeScreen activity
                            startActivity(new Intent(MainActivity.this, HomeScreen.class));
                        }
                    } else {
                        // No Lip info found, navigate to Login_Page activity
                        startActivity(new Intent(MainActivity.this, Login_Page.class));
                    }
                } else {
                    // Failed to get Lip info, navigate to Login_Page activity
                    startActivity(new Intent(MainActivity.this, Login_Page.class));
                }
                finish(); // Finish the current activity to prevent the user from returning here
            }

            @Override
            public void onFailure(Call<List<LIPInfo>> call, Throwable t) {
                // Handle failure, navigate to Login_Page activity
                startActivity(new Intent(MainActivity.this, Login_Page.class));
                finish(); // Finish the current activity to prevent the user from returning here
            }
        });
    }

    }



