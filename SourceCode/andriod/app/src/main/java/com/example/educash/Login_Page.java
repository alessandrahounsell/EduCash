package com.example.educash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class Login_Page extends AppCompatActivity {

    private EditText usernameEdt, passwordEdt;
    private Button  SubmitButton;
    private Button RegisterButton;
    private String strUsername, strPassword, apiResponse;

    private static final String PREF_NAME = "YourPrefs";
    public static final String API_RESPONSE_KEY = "apiResponse";

    public SharedPreferences sharedPreferences;
    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Retrieve the API response from SharedPreferences
        apiResponse = sharedPreferences.getString(API_RESPONSE_KEY, null);

        // Initialize API manager
        apiManager = new ApiManager(getApplicationContext());

        // Find views
        usernameEdt = findViewById(R.id.usernameEdt);
        passwordEdt = findViewById(R.id.passwordEdt);
        SubmitButton = findViewById(R.id.SumbitButton);
        RegisterButton = findViewById(R.id.RegisterButton);

        // Submit button click listener
        SubmitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                strUsername = usernameEdt.getText().toString().trim();
                strPassword = passwordEdt.getText().toString().trim();

                // Check if username and password are not empty
                if (TextUtils.isEmpty(strUsername)) {
                    Toast.makeText(Login_Page.this, "Please enter a Username", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(strPassword)) {
                    Toast.makeText(Login_Page.this, "Please enter a Password", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform login
                    login();
                }
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login_Page.this, Register_Login_Page.class));
            }
        });
    }

    private void login() {
        // Carry out API call for login
        apiManager.login(strUsername, strPassword, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    // Handle successful response
                    apiResponse = response.body();
                    if (apiResponse != null && !apiResponse.isEmpty()) {
                        Log.d("YourActivity", "API Response Retrieved: " + apiResponse);
                        // Save API response to SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(API_RESPONSE_KEY, apiResponse);
                        editor.apply();
                        // Redirect to HomeScreen
                        Intent intent = new Intent(Login_Page.this, HomeScreen.class);
                        startActivity(intent);
                    } else {
                        // Handle empty response
                        Log.e("YourActivity", "Empty API response");
                        Toast.makeText(Login_Page.this, "Empty response from server", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle unsuccessful response
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Handle failure
                handleFailure(t);
            }
        });
    }

    private void handleErrorResponse(Response<?> response) {
        Log.e("YourActivity", "API Call Failed: " + response.message());
        if (response.errorBody() != null) {
            try {
                Log.e("YourActivity", "Error Response Body: " + response.errorBody().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void handleFailure(Throwable t) {
        Log.e("YourActivity", "API Call Failed: " + t.getMessage(), t);
        if (t instanceof HttpException) {
            Log.e("YourActivity", "HTTP Status Code: " + ((HttpException) t).code());
        } else {
            Log.e("YourActivity", "Non-HTTP Exception: " + t.getClass().getSimpleName());
        }
        Toast.makeText(Login_Page.this, "You have entered incorrect login details", Toast.LENGTH_LONG).show();
    }
}
