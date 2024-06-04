package com.example.educash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class History_Screen extends AppCompatActivity implements TransactionFetcher.TransactionListener {
    private DrawerLayout drawerLayout;
    private LinearLayout transactionsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_screen);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        NavigationUtils.setupNavigationMenu(navigationView, drawerLayout, this);
        Button menuButton = findViewById(R.id.menu_button);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer();
            }
        });

        // Logo button to home screen
        Button logoButton = findViewById(R.id.logo_btn);
        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(History_Screen.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        // Initialize the LinearLayout for transactions
        transactionsLayout = findViewById(R.id.transactions_layout);

        // Fetch transactions
        fetchTransactions();
    }

    private void fetchTransactions() {
        TransactionFetcher transactionFetcher = new TransactionFetcher(this, this);
        transactionFetcher.execute();
    }

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    private void deleteTransaction(int transactionId) {
        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                int transactionIdToDelete = params[0];
                TransactionFetcher transactionFetcher = new TransactionFetcher(History_Screen.this, new TransactionFetcher.TransactionListener() {
                    @Override
                    public void onTransactionsReceived(List<Transaction> transactions) {

                    }
                });
                transactionFetcher.deleteTransaction(transactionIdToDelete);
                return null;
            }
        }.execute(transactionId);
    }



    @Override
    public void onTransactionsReceived(List<Transaction> transactions) {
        // Clear any existing views
        transactionsLayout.removeAllViews();

        // Format for displaying only date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Create a list to store transactions
        List<Transaction> reversedTransactions = new ArrayList<>();

        // Add transactions to the list in reverse order
        for (int i = transactions.size() - 1; i >= 0; i--) {
            reversedTransactions.add(transactions.get(i));
        }
        // Loop through each transaction and create a view for it
        for (Transaction transaction : reversedTransactions) {
            View transactionView = getLayoutInflater().inflate(R.layout.transaction_item, null);

            // Find views within transaction item layout
            TextView categoryNameTextView = transactionView.findViewById(R.id.category_name_text_view);
            TextView transactionAmountTextView = transactionView.findViewById(R.id.transaction_amount_text_view);
            TextView transactionDateTextView = transactionView.findViewById(R.id.transaction_date_text_view);

            // Set transaction details
            categoryNameTextView.setText(transaction.getCategoryName());
            transactionAmountTextView.setText(String.valueOf(transaction.getTransactionAmount()));
            // Parse the date string and format it
            try {
                Date transactionDate = dateFormat.parse(transaction.getTransactionDate());
                String formattedDate = dateFormat.format(transactionDate);
                transactionDateTextView.setText(formattedDate);
            } catch (ParseException e) {
                // Handle parsing exception
                e.printStackTrace();
                transactionDateTextView.setText("Invalid Date");
            }

            // Add transaction view to the transactions layout
            transactionsLayout.addView(transactionView);

            // Inside the loop where you create transaction views
            Button deleteButton = transactionView.findViewById(R.id.delete_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Delete the corresponding transaction
                    deleteTransaction(transaction.getTransactionId());
                    fetchTransactions();
                }
            });

        }
    }
}
