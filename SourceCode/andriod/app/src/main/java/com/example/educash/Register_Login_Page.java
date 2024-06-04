package com.example.educash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register_Login_Page extends AppCompatActivity {

    EditText edtUsername, edtPassword, edtConfirmPassword;
    Button btnSignUp;
    String txtUsername, txtPassword, txtConfirmPassword, txtPin;
    private AuthenticationService authService;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "YourPrefs";
    private static final String API_RESPONSE_KEY = "apiResponse";

    private ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login_page);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        authService = new AuthenticationService();
        apiManager = new ApiManager();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtUsername = edtUsername.getText().toString().trim();
                txtPassword = edtPassword.getText().toString().trim();
                txtConfirmPassword = edtConfirmPassword.getText().toString().trim();
                EditText edtPin = findViewById(R.id.edtPin);
                txtPin = edtPin.getText().toString().trim();

                if (!TextUtils.isEmpty(txtUsername)
                        && !TextUtils.isEmpty(txtPassword)
                        && !TextUtils.isEmpty(txtConfirmPassword) && txtConfirmPassword.equals(txtPassword)
                        && !TextUtils.isEmpty(txtPin))
                {
                    SignUpUser();
                } else if (TextUtils.isEmpty(txtUsername)) {
                    Toast.makeText(Register_Login_Page.this, "Please enter a Username", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(txtPassword)) {
                    Toast.makeText(Register_Login_Page.this, "Please enter a Password", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(txtConfirmPassword)) {
                    Toast.makeText(Register_Login_Page.this, "Please Confirm your Password", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(txtPin)) {
                    Toast.makeText(Register_Login_Page.this, "Please enter a Pin which you will use to enter the app", Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(Register_Login_Page.this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button RegisterButton = findViewById(R.id.RegisterButton); // Login button - Actually the button to switch to login its just being wierd so i had to keep the name as register
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Register_Login_Page.this, Login_Page.class);
                startActivity(intent);
            }
        });

        Button openLinkButton = findViewById(R.id.tandc_button);

        openLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://educterms-and-conditions.tiiny.site/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    private void SignUpUser() {
        btnSignUp.setVisibility(View.INVISIBLE);
        authService.createUserWithEmailAndPassword(txtUsername, txtPassword, new AuthenticationService.SignUpCallback(){

            // Sign Up logic should go here


            @Override
            public void onSuccess() {


                // Save user information to DB send to API here


                //parsing txtPin to int



                saveUserInfoToApi(txtUsername, txtPassword, txtPin);


            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(Register_Login_Page.this, errorMessage, Toast.LENGTH_LONG).show();
                btnSignUp.setVisibility(View.VISIBLE);
            }
        });

    }
    Hashing_Algorithm objA = new Hashing_Algorithm();


    private void saveUserInfoToApi(String username, String password, String pin) {
        // Use your ApiManager to save user information to the API
        Hashing_Algorithm.hashPassword(password);
        ApiManager apiManager = new ApiManager();

        boolean LIP = false;

        apiManager.registerUser(username, password,pin,LIP, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                // Handle the API response if needed
                if (response.isSuccessful()) {
                    String apiResponse = response.body();
                    if (apiResponse != null) {

                        Log.d("YourActivity", "API Response Retrieved: " + apiResponse);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(API_RESPONSE_KEY, apiResponse);
                        editor.apply();
                        String storedResponse = sharedPreferences.getString(API_RESPONSE_KEY, "");
                        Log.d("YourActivity", "Stored API Response: " + storedResponse);

                    }
                    Log.d("ApiManager", "Successful Response: " + apiResponse);
                    Toast.makeText(Register_Login_Page.this, "Sign Up Successful !", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register_Login_Page.this, HomeScreen.class);
                    startActivity(intent);
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(Register_Login_Page.this, "Failed to save user information to API", Toast.LENGTH_SHORT).show();
                    btnSignUp.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Handle failure if needed
                Toast.makeText(Register_Login_Page.this, "API call failed, It's Likely Someone else has this username", Toast.LENGTH_LONG).show();
                btnSignUp.setVisibility(View.VISIBLE);
            }
        });
    }
}
