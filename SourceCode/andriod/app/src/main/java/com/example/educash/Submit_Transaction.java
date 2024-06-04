package com.example.educash;

import static com.example.educash.Login_Page.API_RESPONSE_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Submit_Transaction extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "DataSubmitter";
    private static final String API_URL = "https://web.socem.plymouth.ac.uk/COMP2003/COMP2003_26/Transaction";
    private static final String PREF_NAME = "YourPrefs";

    private Context context;
    private DataSubmissionListener listener;

    // Data to be submitted
    private JSONObject data;

    public Submit_Transaction(Context context, DataSubmissionListener listener, JSONObject data) {
        this.context = context;
        this.listener = listener;
        this.data = data;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String token = getTokenFromSharedPreferences(); // Implement this method to get the token

        try {
            // Construct the URL with the token query parameter
            Uri.Builder builder = Uri.parse(API_URL).buildUpon();
            builder.appendQueryParameter("token", token);
            String urlString = builder.build().toString();
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setDoOutput(true);

            // Write data to the connection
            OutputStream os = conn.getOutputStream();
            os.write(data.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true; // Submission successful
            } else {
                Log.e(TAG, "Error response code: " + responseCode);
                return false; // Error in submission
            }
        } catch (IOException e) {
            Log.e(TAG, "Error submitting data: " + e.getMessage());
            return false; // Error in submission
        }
    }

    private String getTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Log.d(TAG, "getTokenFromSharedPreferences:"+ sharedPreferences.getString(API_RESPONSE_KEY, ""));
        String storedResponse = sharedPreferences.getString(API_RESPONSE_KEY, "");
        Log.d("YourActivity", "Stored API Response: " + storedResponse);
        return sharedPreferences.getString(API_RESPONSE_KEY, "");
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (listener != null) {
            listener.onDataSubmitted(success);
        }
    }

    // Define an interface to listen for submission result
    public interface DataSubmissionListener {
        void onDataSubmitted(boolean success);
    }
}

