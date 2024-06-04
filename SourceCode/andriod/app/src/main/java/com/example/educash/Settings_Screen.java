package com.example.educash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.AsyncTask;
import android.util.Log;
import android.util.TypedValue;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Settings_Screen extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private static final String PREF_NAME = "YourPrefs";
    private static final String API_RESPONSE_KEY = "apiResponse";
    private static final String TOGGLE_STATE_KEY = "toggleState";
    private SharedPreferences colourPreferences;
    private SharedPreferences.Editor editor;
    private ToggleButton colourButton;

    Switch switcher;
    boolean nightMode;
    SharedPreferences sharedPreferences;


    private float textSize;
    private ToggleButton toggleButton1;
    private Overdraft_Fetcher overdraftFetcher;

    private static final String TAG = "Settings_Screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);
        // Initialize Overdraft_Fetcher
        overdraftFetcher = new Overdraft_Fetcher(this);

        // Initialize toggleButton1
        toggleButton1 = findViewById(R.id.toggleButton1);

        // Fetch overdraft status
        new FetchOverdraftTask().execute();

        // Set toggleButton1 click listener
        toggleButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Change overdraft status
                new ChangeOverdraftTask().execute(isChecked);
                Log.d(TAG, "onCheckedChanged: Overdraft status changed to " + isChecked);

                // Check if overdraft is true
                if (isChecked) {
                    // Set background resource when overdraft is true
                    toggleButton1.setBackgroundResource(R.drawable.onbutton);
                } else {
                    toggleButton1.setBackgroundResource(R.drawable.offbutton);
                }
            }
        });


        // Set up the menu button click listener
        Button menuButton = findViewById(R.id.menu_button);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        //Logout Button
        Button logOutButton = findViewById(R.id.logout_button);

        // Call the setupNavigationMenu method from NavigationUtils
        NavigationUtils.setupNavigationMenu(navigationView, drawerLayout, this);

        Spinner textSizeSpinner = findViewById(R.id.textSizeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.text_size_options, R.layout.spinner_custom);
        adapter.setDropDownViewResource(R.layout.spinner_custom);
        textSizeSpinner.setAdapter(adapter);

        textSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected text size from the spinner
                String selectedTextSize = (String) parentView.getItemAtPosition(position);

                // Retrieve the current text size preference from SharedPreferences
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String currentTextSizePreference = prefs.getString("textSizePreference", "default");

                // Check if the selected text size is different from the current text size preference
                if (!selectedTextSize.equals(currentTextSizePreference)) {
                    // Save the selected text size preference in SharedPreferences
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("textSizePreference", selectedTextSize);
                    editor.apply();

                    // Reload the activity to apply the new text size
                    recreate();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if nothing is selected
            }
        });

        // Retrieve text size preference and set text size
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String textSizePreference = prefs.getString("textSizePreference", "default");

        // Set spinner selection based on the retrieved preference
        int spinnerPosition = adapter.getPosition(textSizePreference);
        textSizeSpinner.setSelection(spinnerPosition);



        switch (textSizePreference) {
            case "Small":
                textSize = 30;
                break;
            case "Large":
                textSize = 50;
                break;
            default: // Default text size
                textSize = 35;
                break;
        }

        TextView pinText = findViewById(R.id.pinLoginText);
        pinText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        TextView logoutSize = findViewById(R.id.logout_button);
        logoutSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        TextView textSizeText = findViewById(R.id.textSizeText);
        textSizeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        TextView helpSize = findViewById(R.id.help_button);
        helpSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        TextView backgroundSize = findViewById(R.id.greenBackgroundText);
        backgroundSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        TextView overdraftSize = findViewById(R.id.overdraftText);
        overdraftSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);



        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer();
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        // Logo button to home screen
        Button logoButton = findViewById(R.id.logo_btn);
        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings_Screen.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        ToggleButton toggleButton = findViewById(R.id.toggleButton);

        // Retrieve the user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String userId = sharedPreferences.getString(API_RESPONSE_KEY, "");

        // Make API call to get user's LIP info
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<LIPInfo>> call = apiService.getLIPInfo(userId);

// Retrieve the user token from SharedPreferences
        final String token = sharedPreferences.getString(API_RESPONSE_KEY, ""); // Declare as final

        call.enqueue(new Callback<List<LIPInfo>>() {
            @Override
            public void onResponse(Call<List<LIPInfo>> call, Response<List<LIPInfo>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Extract LIP status from the API response
                    boolean isLIPEnabled = response.body().get(0).isLip();

                    // Set the initial state of the toggle button
                    toggleButton.setChecked(isLIPEnabled);
                } else {
                    // Handle unsuccessful response
                    // For now, let's set the default state of the toggle button to false
                    toggleButton.setChecked(false);
                    toggleButton.setBackgroundResource(R.drawable.offbutton);
                }
            }

            @Override
            public void onFailure(Call<List<LIPInfo>> call, Throwable t) {
                // Handle failure
                // For now, let's set the default state of the toggle button to false
                toggleButton.setChecked(false);
            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("YourActivity", "API Response Retrieved: " + token); // Use the final variable
                Log.d("Eepy", "LIP status is now:" + isChecked);

                // Make API call to change user's LIP status
                Call<List<LipChange>> updateCall = apiService.changeLIP(token, isChecked);
                if (isChecked) {
                    // Toggle button is checked (On state)
                    toggleButton.setBackgroundResource(R.drawable.onbutton);
                } else {
                    // Toggle button is not checked (Off state)
                    toggleButton.setBackgroundResource(R.drawable.offbutton);
                }

                updateCall.enqueue(new Callback<List<LipChange>>() {
                    @Override
                    public void onResponse(Call<List<LipChange>> call, Response<List<LipChange>> response) {
                        if (response.isSuccessful()) {
                            // Handle successful response
                            Log.d("API Response", "Response code: " + response.code());
                            List<LipChange> stringList = response.body();
                        } else {
                            // Handle error response
                            Log.d("API Response", "Error code: " + response.code());
                            Log.d("API Response", "Error message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<LipChange>> call, Throwable t) {
                        Log.e("API RESPONSE", "Error: " + t.getMessage(), t);
                    }
                });
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

        //Here////////////////////////////////////////////
        final ToggleButton colourButton = findViewById(R.id.switcher); // Declare toggleButton as final
// final SharedPreferences sharedPreferences = getSharedPreferences("Mode", Context.MODE_PRIVATE); // Declare sharedPreferences as final
        final SharedPreferences.Editor editor = sharedPreferences.edit(); // Declare editor as final
        boolean nightMode = sharedPreferences.getBoolean("night", false); // Declare nightMode as final

        colourButton.setChecked(nightMode);
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            colourButton.setBackgroundResource(R.drawable.onbutton);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        colourButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("night", true);
                    colourButton.setBackgroundResource(R.drawable.onbutton);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("night", false);
                }
                editor.apply();
            }
        });


    }
    public void openURL1(View view) {
        String url = "https://online.fliphtml5.com/vqthd/zoyy/";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    // AsyncTask to change overdraft status in background
    private class ChangeOverdraftTask extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean... params) {
            // Call changeOverdraft method with the first parameter (isChecked)
            overdraftFetcher.changeOverdraft(params[0]);
            return null;
        }
    }

    // AsyncTask to fetch overdraft status in background
    private class FetchOverdraftTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return overdraftFetcher.doInBackground();
        }

        @Override
        protected void onPostExecute(Boolean overdraft) {
            super.onPostExecute(overdraft);
            // Set toggleButton1 state based on overdraft status
            toggleButton1.setChecked(overdraft);
            Log.d(TAG, "onPostExecute: Overdraft status set to " + overdraft);
        }
    }

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    private void signOut() {
        // Get SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Edit the SharedPreferences to remove the API key
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(API_RESPONSE_KEY); // Remove the API key
        editor.apply(); // Apply changes

        // Log the value of API_RESPONSE_KEY for debugging
        Log.d("API_RESPONSE_KEY", "Value after sign out: " + sharedPreferences.getString(API_RESPONSE_KEY, null));

        // Navigate to the login screen or perform any other necessary actions
        Intent intent = new Intent(Settings_Screen.this, Login_Page.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}
