package com.example.educash;

import static android.content.ContentValues.TAG;
import static com.example.educash.Login_Page.API_RESPONSE_KEY;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pin_Login extends AppCompatActivity {
    private TextView pinTextView;
    public SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "YourPrefs";
    private ApiManager apiManager;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pinTextView = findViewById(R.id.pinTextView);
        setNumericButtonClickListeners();

        apiManager = new ApiManager(this);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Check if the user has a token
        String token = sharedPreferences.getString(API_RESPONSE_KEY, "");
        Log.d("YourActivity", "Stored API Response: " + token);

        if(token == null ){
            Toast.makeText(this, "An Error has occured, taking you back to the login screen.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Pin_Login.this, Login_Page.class));
        }

        findViewById(R.id.Login_Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

    }

    private void Login() {
        // Get the entered PIN from the TextView
        String enteredPin = pinTextView.getText().toString().trim();
        Log.d("Pin entered Login", enteredPin);

        // Check if the entered PIN has a length of 6 characters
        if (enteredPin.length() != 6) {
            // Show an error toast and return without attempting login
            Toast.makeText(this, "PIN must be exactly 6 characters long.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the token from SharedPreferences
        String token = sharedPreferences.getString(API_RESPONSE_KEY, "");

        // Call the API manager to perform PIN login
        apiManager.loginWithPin(enteredPin, token, new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    // Handle successful login
                    boolean loginSuccess = response.body();
                    if (loginSuccess) {
                        // PIN login successful, navigate to the next screen
                        Intent intent = new Intent(Pin_Login.this, HomeScreen.class);
                        startActivity(intent);
                    } else {
                        // PIN login failed, show an error message
                        Toast.makeText(Pin_Login.this, "Incorrect PIN please try again", Toast.LENGTH_SHORT).show();
                        pinTextView.setText("");
                    }
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(Pin_Login.this, "PIN login failed. Please try again.", Toast.LENGTH_SHORT).show();
                    pinTextView.setText("");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                // Handle login failure
                Log.e(TAG, "PIN login failed: " + t.getMessage());
                Toast.makeText(Pin_Login.this, "PIN login failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
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
    }


    private void setNumericButtonClickListener(Button button, final String digit) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePinTextView(digit);
            }
        });
    }

    private void updatePinTextView(String digit) {
        String currentText = pinTextView.getText().toString();
        String newText = currentText + digit;
        pinTextView.setText(newText);
    }
    }
