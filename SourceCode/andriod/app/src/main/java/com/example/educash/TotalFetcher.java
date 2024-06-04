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
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TotalFetcher extends AsyncTask<Void, Void, Double> {
    private static final String TAG = "TotalFetcher";
    private static final String API_URL = "https://web.socem.plymouth.ac.uk/COMP2003/COMP2003_26/AccountTotals"; //
    private static final String PREF_NAME = "YourPrefs";

    private Context context;
    private TotalListener listener;

    public TotalFetcher(Context context, TotalListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Double doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground: testing");
        String token = getTokenFromSharedPreferences();
        double total = 0.0;

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

            // Assuming you only expect one object in the array
            if (jsonArray.length() > 0) {
                // Extract the first object from the array
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                // Extract the currentAmount from the JSON object
                double currentAmount = jsonObject.getDouble("currentAmount");
                total = currentAmount;
            }

            conn.disconnect();
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error fetching total: " + e.getMessage());
        }

        return total;
    }


    @Override
    protected void onPostExecute(Double total) {
        super.onPostExecute(total);
        if (listener != null) {
            listener.onTotalReceived(total);
        }
    }

    private String getTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Log.d(TAG, "getTokenFromSharedPreferences:"+ sharedPreferences.getString(API_RESPONSE_KEY, ""));
        String storedResponse = sharedPreferences.getString(API_RESPONSE_KEY, "");
        Log.d("YourActivity", "Stored API Response: " + storedResponse);
        return sharedPreferences.getString(API_RESPONSE_KEY, "");
    }

    public interface TotalListener {
        void onTotalReceived(Double total);
    }
}
