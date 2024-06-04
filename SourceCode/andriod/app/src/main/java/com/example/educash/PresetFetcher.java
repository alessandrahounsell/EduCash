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

public class PresetFetcher extends AsyncTask<Void, Void, List<Preset>> {
    private static final String TAG = "PresetFetcher";
    private static final String API_URL = "https://web.socem.plymouth.ac.uk/COMP2003/COMP2003_26/Preset/GetPreset";
    private static final String API_URL2 = "https://web.socem.plymouth.ac.uk/COMP2003/COMP2003_26/Preset";
    private static final String API_URL3 = "https://web.socem.plymouth.ac.uk/COMP2003/COMP2003_26/Preset/addPreset";
    private static final String PREF_NAME = "YourPrefs";
    //private static final String API_RESPONSE_KEY = "API_RESPONSE_KEY";

    private Context context;
    private PresetListener listener;

    public PresetFetcher(Context context, PresetListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected List<Preset> doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground: Starting background task to fetch presets...");

        String token = getTokenFromSharedPreferences();
        List<Preset> presets = new ArrayList<>();

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
                int presetId = jsonObject.getInt("presetId");
                String presetName = jsonObject.getString("presetName");
                double price = jsonObject.getDouble("price");
                presets.add(new Preset(presetId, presetName, price));
            }

            conn.disconnect();
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error fetching presets: " + e.getMessage());
        }

        return presets;
    }

    // Method to delete a preset by its ID
    public void deletePreset(int presetId) {
        String token = getTokenFromSharedPreferences();

        try {
            // Construct the URL with the token and preset ID
            Uri.Builder builder = Uri.parse(API_URL2).buildUpon();
            builder.appendQueryParameter("id", String.valueOf(presetId));
            builder.appendQueryParameter("token", token);
            String urlString = builder.build().toString();
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "Preset with ID " + presetId + " deleted successfully");
            } else {
                Log.e(TAG, "Failed to delete preset with ID " + presetId + ", HTTP response code: " + responseCode);
            }

            conn.disconnect();
        } catch (IOException e) {
            Log.e(TAG, "Error deleting preset: " + e.getMessage());
        }
    }

    public void addPreset(String presetName, double presetPrice) {
        String token = getTokenFromSharedPreferences();

        try {
            // Construct the URL for adding a preset
            Uri.Builder builder = Uri.parse(API_URL3).buildUpon();
            builder.appendQueryParameter("token", token);
            String urlString = builder.build().toString();
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            // Set Content-Type header
            conn.setRequestProperty("Content-Type", "application/json");

            // Create JSON object for the preset
            JSONObject requestBody = new JSONObject();
            requestBody.put("name", presetName);
            requestBody.put("price", presetPrice);

            // Write JSON data to the connection output stream
            OutputStream os = conn.getOutputStream();
            os.write(requestBody.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                Log.d(TAG, "Preset added successfully");

            } else {
                Log.e(TAG, "Failed to add preset, HTTP response code: " + responseCode);
            }

            conn.disconnect();
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error adding preset: " + e.getMessage());
        }
    }




    @Override
    protected void onPostExecute(List<Preset> presets) {
        Log.d(TAG, "onPostExecute: Received presets - " + presets.size());
        super.onPostExecute(presets);
        if (listener != null) {
            listener.onPresetsReceived(presets);
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

    public interface PresetListener {
        void onPresetsReceived(List<Preset> presets);
    }
}

