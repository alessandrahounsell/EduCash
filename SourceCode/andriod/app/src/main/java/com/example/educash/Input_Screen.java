package com.example.educash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class Input_Screen extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView incomingsTextView;

    private double submittedAmount = 0.0;

    private static final String TAG = "Input_Screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_screen);

        Button menuButton = findViewById(R.id.menu_button);
        incomingsTextView = findViewById(R.id.IncomingsTextView);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer();
            }
        });


        NavigationView navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        // Set up navigation menu
        NavigationUtils.setupNavigationMenu(navigationView, drawerLayout, this);


        setNumericButtonClickListeners();
        Button submitButton = findViewById(R.id.SumbitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearIncomingsTextView();
                submitData();
            }
        });

        // Logo button to home screen
        Button logoButton = findViewById(R.id.logo_btn);
        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Input_Screen.this, HomeScreen.class);
                startActivity(intent);
            }
        });
    }

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    public void submitData() {
        // Create your JSON data object
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("categorieId", 8);
            jsonData.put("take", false);
            jsonData.put("name", "");
            jsonData.put("transactionAmmount", submittedAmount);
            jsonData.put("transactionDate", "2024-04-22T11:34:28.894Z"); // Generic date time as it gets overridden by the API
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create an instance of DataSubmitter with your data
        Submit_Transaction submitTransaction = new Submit_Transaction(this, new Submit_Transaction.DataSubmissionListener() {
            @Override
            public void onDataSubmitted(boolean success) {
                if (success) {
                    Log.d(TAG, "onDataSubmitted: Data submitted successfully!");
                } else {
                    Log.e(TAG, "onDataSubmitted: Error submitting data");


                }
            }
        }, jsonData);

        // Execute the task
        submitTransaction.execute();
    }

    private void setNumericButtonClickListeners() {
        // Get references to numeric buttons (0-9)
        Button button0 = findViewById(R.id.zero);
        Button button1 = findViewById(R.id.one);
        Button button2 = findViewById(R.id.two);
        Button button3 = findViewById(R.id.three);
        Button button4 = findViewById(R.id.four);
        Button button5 = findViewById(R.id.five);
        Button button6 = findViewById(R.id.six);
        Button button7 = findViewById(R.id.seven);
        Button button8 = findViewById(R.id.eight);
        Button button9 = findViewById(R.id.nine);
        Button button00 = findViewById(R.id.dot);
        Button buttonCE = findViewById(R.id.CE);

        // Set click listeners for numeric buttons
        setNumericButtonClickListener(button0, "0");
        setNumericButtonClickListener(button1, "1");
        setNumericButtonClickListener(button2, "2");
        setNumericButtonClickListener(button3, "3");
        setNumericButtonClickListener(button4, "4");
        setNumericButtonClickListener(button5, "5");
        setNumericButtonClickListener(button6, "6");
        setNumericButtonClickListener(button7, "7");
        setNumericButtonClickListener(button8, "8");
        setNumericButtonClickListener(button9, "9");
        setNumericButtonClickListener(button00, ".");
        setNumericButtonClickListener(buttonCE, "CE");
    }


    private void setNumericButtonClickListener(Button button, final String digit) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateIncomingsTextView(digit);
            }
        });
    }

    private void updateIncomingsTextView(String digit) {
        String currentText = incomingsTextView.getText().toString();
        if (currentText.length() == 6 && digit.equals("00")) {
            digit = "0";
            String newText = currentText + digit;
            incomingsTextView.setText(newText);
            submittedAmount *= 10; // Shift the decimal point one place to the left
        } else if (digit.equals("CE")) {
            incomingsTextView.setText("+£");
            // Clear the submitted amount
            submittedAmount = 0.0;
        } else if (currentText.length() < 8) {
            String newText = currentText + digit;
            incomingsTextView.setText(newText);
            submittedAmount = Double.parseDouble(newText.replace("+£", ""));
        } else {
            Toast.makeText(this, "Maximum 6 digits allowed", Toast.LENGTH_SHORT).show();
        }
    }



    private void clearIncomingsTextView() {
       if (incomingsTextView.length() > 2) {
           double currentOutgoing = Double.parseDouble(incomingsTextView.getText().toString().replace("+£", ""));
           AppConstants.UserIncomings += currentOutgoing;
           Log.d("Incoming", "Incoming total = " + AppConstants.UserIncomings);
           incomingsTextView.setText("+£");
           Toast.makeText(this, "Incomings successfully submitted", Toast.LENGTH_SHORT).show();
       }
       else{
           Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
       }
    }


}








