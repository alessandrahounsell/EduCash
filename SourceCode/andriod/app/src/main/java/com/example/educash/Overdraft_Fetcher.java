package com.example.educash;


import static com.example.educash.Login_Page.API_RESPONSE_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.educash.Login_Page.API_RESPONSE_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Overdraft_Fetcher extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "OverdraftFetcher";
    private static final String API_URL = "https://web.socem.plymouth.ac.uk/COMP2003/COMP2003_26/User/";
    private static final String API_URL2 = "https://web.socem.plymouth.ac.uk/COMP2003/COMP2003_26/User/ChangeNeg";
    private static final String PREF_NAME = "YourPrefs";
    //private static final String API_RESPONSE_KEY = "API_RESPONSE_KEY";

    private Context context;

    // Constructor to receive the context
    public Overdraft_Fetcher(Context context) {
        this.context = context;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground: Starting background task to fetch overdraft...");

        Boolean overdraft = false; // Default value in case of failure or no data

        String token = getTokenFromSharedPreferences();

        try {
            // Construct the URL with the token query parameter
            Uri.Builder builder = Uri.parse(API_URL).buildUpon();
            builder.appendQueryParameter("token", token);
            String urlString = builder.build().toString();
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStream inputStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Log the raw JSON response before parsing
            Log.d(TAG, "Raw JSON response: " + response.toString());

            // Parse the JSON response into a JSONArray
            JSONArray jsonArray = new JSONArray(response.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String username = jsonObject.getString("username");
                boolean lip = jsonObject.getBoolean("lip");
                boolean negAllowed = jsonObject.getBoolean("negAllowed");
                Log.d(TAG, "doInBackground: Username: " + username + ", lip: " + lip + ", negAllowed: " + negAllowed);
                overdraft = negAllowed; // Assigning the value of negAllowed to overdraft
            }

            conn.disconnect();
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error fetching user: " + e.getMessage());
        }

        return overdraft; // Return the value of negAllowed
    }




    public void changeOverdraft(boolean overdraft) {
        String token = getTokenFromSharedPreferences();

        try {
            // Construct the URL for the API endpoint
            Uri.Builder builder = Uri.parse(API_URL2).buildUpon();
            builder.appendQueryParameter("token", token);
            builder.appendQueryParameter("neg", String.valueOf(overdraft));
            String urlString = builder.build().toString();
            Log.d(TAG, "Constructed URL: " + urlString);
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");

            // Set Content-Type header
            conn.setRequestProperty("Content-Type", "application/json");


            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                Log.d(TAG, "Overdraft status updated successfully");
            } else {
                Log.e(TAG, "Failed to update overdraft status, HTTP response code: " + responseCode);
                readResponseContent(conn);
            }

            // Read the header field for 'negAllowed'
            boolean newOverdraft = Boolean.parseBoolean(conn.getHeaderField("negAllowed"));
            Log.d(TAG, "New overdraft status: " + newOverdraft);

            conn.disconnect();
        } catch (IOException e) {
            Log.e(TAG, "Error updating overdraft status: " + e.getMessage());
        }
    }
    private String readResponseContent(HttpURLConnection connection) {
        try {
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error reading response content: " + e.getMessage());
            return null;
        }
    }






    private String getTokenFromSharedPreferences() {
        Log.d(TAG, "getTokenFromSharedPreferences: Retrieving token from SharedPreferences...");
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Log.d(TAG, "getTokenFromSharedPreferences:"+ sharedPreferences.getString(API_RESPONSE_KEY, ""));
        String storedResponse = sharedPreferences.getString(API_RESPONSE_KEY, "");
        Log.d("YourActivity", "Stored API Response: " + storedResponse);
        return sharedPreferences.getString(API_RESPONSE_KEY, "");
    }

}

