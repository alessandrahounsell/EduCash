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
import java.util.ArrayList;
import java.util.List;

public class TransactionFetcher extends AsyncTask<Void, Void, List<Transaction>> {
    private static final String TAG = "TransactionFetcher";
    private static final String API_URL = "https://web.socem.plymouth.ac.uk/COMP2003/COMP2003_26/Transaction";

    private static final String PREF_NAME = "YourPrefs";
    //private static final String API_RESPONSE_KEY = "YOUR_API_RESPONSE_KEY";

    private Context context;
    private TransactionListener listener;

    public TransactionFetcher(Context context, TransactionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected List<Transaction> doInBackground(Void... voids) {
        Log.d(TAG, "doInBackground: Starting background task to fetch transactions...");

        String token = getTokenFromSharedPreferences();
        List<Transaction> transactions = new ArrayList<>();

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
                int transactionId = jsonObject.getInt("transactionId");
                JSONObject categoryObject = jsonObject.getJSONObject("categorie");
                int categoryId = categoryObject.getInt("categorieId");
                String categoryName = categoryObject.getString("categorieName");
                boolean take = jsonObject.getBoolean("take");
                double transactionAmount = jsonObject.getDouble("transactionAmount");
                String transactionDate = jsonObject.getString("transactionDate");

                Transaction transaction = new Transaction(transactionId, categoryId, categoryName, take, transactionAmount, transactionDate);
                transactions.add(transaction);
            }

            conn.disconnect();
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error fetching transactions: " + e.getMessage());
        }

        return transactions;
    }

    public void deleteTransaction(int transactionId) {
        String token = getTokenFromSharedPreferences();

        try {
            // Construct the URL with the token and transaction ID
            Uri.Builder builder = Uri.parse(API_URL).buildUpon();
            builder.appendQueryParameter("id", String.valueOf(transactionId));
            builder.appendQueryParameter("token", token);
            String urlString = builder.build().toString();
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "Transaction with ID " + transactionId + " deleted successfully");
            } else {
                Log.e(TAG, "Failed to delete transaction with ID " + transactionId + ", HTTP response code: " + responseCode);
            }

            conn.disconnect();
        } catch (IOException e) {
            Log.e(TAG, "Error deleting transaction: " + e.getMessage());
        }
    }


    @Override
    protected void onPostExecute(List<Transaction> transactions) {
        Log.d(TAG, "onPostExecute: Received transactions - " + transactions.size());
        super.onPostExecute(transactions);
        if (listener != null) {
            listener.onTransactionsReceived(transactions);
        }
    }

    private String getTokenFromSharedPreferences() {
        Log.d(TAG, "getTokenFromSharedPreferences: Retrieving token from SharedPreferences...");
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String storedResponse = sharedPreferences.getString(API_RESPONSE_KEY, "");
        Log.d(TAG, "Stored API Response: " + storedResponse);
        return storedResponse;
    }

    public interface TransactionListener {
        void onTransactionsReceived(List<Transaction> transactions);
    }
}
