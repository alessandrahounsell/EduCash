package com.example.educash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Output_Screen extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    private TextView incomingsTextView;

    private Spinner categorySpinner;

    private Spinner presetSpinner;

    private int CategoryId;

    private double submittedAmount = 0.0;

    private int PresetId;

    private static final String TAG = "Output_Screen";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_screen);
        //Log.d("CurrentUser",AppConstants.CurrentUserPin); //THIS LINE IS WHERE THE ERROR IS
        // Initialize incomingsTextView

        categorySpinner = findViewById(R.id.category_spinner);

        presetSpinner = findViewById(R.id.preset_spinner);

        Button catButton = findViewById(R.id.categories_btn);

        Button presetButton = findViewById(R.id.presets_btn);

       // categorySpinner.setLayoutResource(R.layout.spinner_layout);

        // Apply the custom layout to the spinner
        categorySpinner.setAdapter(null);
        catButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch categories when the button is clicked
                fetchCategories();

                // Toggle visibility of the Spinner and the category text
                if (categorySpinner.getVisibility() == View.VISIBLE) {
                    categorySpinner.setVisibility(View.GONE);
                    findViewById(R.id.category_text).setVisibility(View.VISIBLE);
                } else {
                    categorySpinner.setVisibility(View.VISIBLE);
                    findViewById(R.id.category_text).setVisibility(View.GONE);
                }
            }
        });

        presetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch presets when the button is clicked
                fetchPresets();

                // Toggle visibility of the Spinner and the category text
                if (presetSpinner.getVisibility() == View.VISIBLE) {
                    presetSpinner.setVisibility(View.GONE);
                    findViewById(R.id.preset_txt).setVisibility(View.VISIBLE);
                } else {
                    presetSpinner.setVisibility(View.VISIBLE);
                    findViewById(R.id.preset_txt).setVisibility(View.GONE);
                }
            }
        });


        incomingsTextView = findViewById(R.id.IncomingsTextView);



        Button submitButton = findViewById(R.id.SumbitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Submit button clicked!" + "Amount: " + submittedAmount);
                clearIncomingsTextView();
                submitData();
            }
        });

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
                Intent intent = new Intent(Output_Screen.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        // Call the setupNavigationMenu method from NavigationUtils
        NavigationUtils.setupNavigationMenu(navigationView, drawerLayout, this);

        setNumericButtonClickListeners();



    }

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    private void fetchCategories() {
        CategoryFetcher categoryFetcher = new CategoryFetcher(this, new CategoryFetcher.CategoryListener() {
            @Override
            public void onCategoriesReceived(List<Category> categories) {
                // Update the spinner with received categories
                populateCatSpinner(categories);
            }
        });
        categoryFetcher.execute();
    }

    private void populateCatSpinner(List<Category> categories) {
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getCategorieName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_custom, categoryNames);
        adapter.setDropDownViewResource(R.layout.spinner_custom);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected category object
                Category selectedCategory = categories.get(position);

                // Set the CategoryId to the categoryId of the selected category
                CategoryId = selectedCategory.getCategorieId();

                Log.d("Spinner", "Selected category: " + CategoryId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "Please Specify a category", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchPresets() {
        PresetFetcher presetFetcher = new PresetFetcher(this, new PresetFetcher.PresetListener() {
            @Override
            public void onPresetsReceived(List<Preset> presets) {
                // Update the spinner with received presets
                populatePresetSpinner(presets);
            }
        });
        presetFetcher.execute();
    }

    private void populatePresetSpinner(List<Preset> presets) {
        List<String> presetNames = new ArrayList<>();
        for (Preset preset : presets) {
            presetNames.add(preset.getPresetName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_custom, presetNames);
        adapter.setDropDownViewResource(R.layout.spinner_custom);
        presetSpinner.setAdapter(adapter);

        presetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected preset object
                Preset selectedPreset = presets.get(position);

                // Set the PrsetId to the presetId of the selected preset
                PresetId = selectedPreset.getPresetId();
                Log.d("Spinner", "Selected preset: " + PresetId);

                // Update incomingsTextView with the price of the selected preset
                incomingsTextView.setText("-£" + selectedPreset.getPrice());
                submittedAmount = selectedPreset.getPrice(); // Update submitted amount
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(), "Please Specify a preset", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void submitData() {
        // Create your JSON data object
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("categorieId", CategoryId);
            jsonData.put("take", true);
            if (PresetId != 0) {
                // If a preset is selected, set the preset name as "name"
                jsonData.put("name", presetSpinner.getSelectedItem().toString());
            } else {
                // If no preset is selected, set an empty string as "name"
                jsonData.put("name", "");
            }
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
                    Log.d(TAG, "onClick: Submit button clicked!" + "Amount: " + submittedAmount);
                    Toast.makeText(Output_Screen.this, "Outgoings successfully submitted", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "onDataSubmitted: Error submitting data");
                    Toast.makeText(Output_Screen.this, "Please select a category and enter an amount", Toast.LENGTH_LONG).show();
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
            incomingsTextView.setText("-£");
            // Clear the submitted amount
            submittedAmount = 0.0;
        } else if (currentText.length() < 8) {
            String newText = currentText + digit;
            incomingsTextView.setText(newText);
            submittedAmount = Double.parseDouble(newText.replace("-£", ""));
        } else {
            Toast.makeText(this, "Maximum 6 digits allowed", Toast.LENGTH_SHORT).show();
        }
    }


    private void clearIncomingsTextView() {
        if (incomingsTextView.length() > 2){
            double currentOutgoing = Double.parseDouble(incomingsTextView.getText().toString().replace("-£", ""));
            AppConstants.UserOutgoings += currentOutgoing;
            Log.d("Outgoing", "Outgoing total = " + AppConstants.UserOutgoings);
            incomingsTextView.setText("-£");
        }
        else{
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
        }
    }




}

