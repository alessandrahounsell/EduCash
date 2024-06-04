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

import android.graphics.Color;
import androidx.core.content.ContextCompat;

public class CategoryFetcher extends AsyncTask<Void, Void, List<Category>> {
    private static final String TAG = "CategoryFetcher";
    private static final String API_URL = "https://web.socem.plymouth.ac.uk/COMP2003/COMP2003_26/Categorie/GetCategories";
    private static final String API_URL2 = "https://web.socem.plymouth.ac.uk/COMP2003/COMP2003_26/Categorie/catdelete";
    private static final String API_URL3 = "https://web.socem.plymouth.ac.uk/COMP2003/COMP2003_26/Categorie/NewCategorie";
    private static final String PREF_NAME = "YourPrefs";
    //private static final String API_RESPONSE_KEY = "API_RESPONSE_KEY";

    private Context context;
    private CategoryListener listener;

    public CategoryFetcher(Context context, CategoryListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected List<Category> doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground: testing");
        String token = getTokenFromSharedPreferences();
        List<Category> categories = new ArrayList<>();

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
                int categoryId = jsonObject.getInt("categorieId");
                String categoryName = jsonObject.getString("categorieName");
                categories.add(new Category(categoryId, categoryName));
            }

            conn.disconnect();
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error fetching categories: " + e.getMessage());
        }

        return categories;
    }

    // Method to delete a category by its ID
    public void deleteCategory(int categoryId) {
        String token = getTokenFromSharedPreferences();

        try {
            // Construct the URL with the token and category ID
            Uri.Builder builder = Uri.parse(API_URL2).buildUpon();
            builder.appendQueryParameter("id", String.valueOf(categoryId));
            builder.appendQueryParameter("token", token);
            String urlString = builder.build().toString();
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "Category with ID " + categoryId + " deleted successfully");
            } else {
                Log.e(TAG, "Failed to delete category with ID " + categoryId + ", HTTP response code: " + responseCode);
            }

            conn.disconnect();
        } catch (IOException e) {
            Log.e(TAG, "Error deleting category: " + e.getMessage());
        }
    }

    // Method to add a new category to the API
    public void addCategory(String categoryName) {
        String token = getTokenFromSharedPreferences();

        try {
            // Construct the URL for adding a category
            Uri.Builder builder = Uri.parse(API_URL3).buildUpon();
            builder.appendQueryParameter("token", token);
            String urlString = builder.build().toString();
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            // Set Content-Type header
            conn.setRequestProperty("Content-Type", "application/json");

            // Create JSON object for the category
            JSONObject requestBody = new JSONObject();
            requestBody.put("categorieName", categoryName);

            // Write JSON data to the connection output stream
            OutputStream os = conn.getOutputStream();
            os.write(requestBody.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                Log.d(TAG, "Category added successfully");

            } else {
                Log.e(TAG, "Failed to add category, HTTP response code: " + responseCode);
            }

            conn.disconnect();
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error adding category: " + e.getMessage());
        }
    }




    @Override
    protected void onPostExecute(List<Category> categories) {
        super.onPostExecute(categories);
        if (listener != null) {
            listener.onCategoriesReceived(categories);
        }
    }


    private String getTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Log.d(TAG, "getTokenFromSharedPreferences:"+ sharedPreferences.getString(API_RESPONSE_KEY, ""));
        String storedResponse = sharedPreferences.getString(API_RESPONSE_KEY, "");
        Log.d("YourActivity", "Stored API Response: " + storedResponse);
        return sharedPreferences.getString(API_RESPONSE_KEY, "");
    }


    public interface CategoryListener {
        void onCategoriesReceived(List<Category> categories);
    }
}
