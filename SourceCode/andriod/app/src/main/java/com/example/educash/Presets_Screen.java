package com.example.educash;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.example.educash.PresetFetcher;


import java.util.List;

import android.graphics.Color;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class Presets_Screen extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private LinearLayout linearLayoutContainer;
    private RecyclerView recyclerViewPresets;

    private float textSize;

    private PresetFetcher presetsFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presets_screen);

        // Retrieve text size preference and set text size
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String textSizePreference = prefs.getString("textSizePreference", "default");


        switch (textSizePreference) {
            case "Small":
                textSize = 18;
                break;
            case "Large":
                textSize = 38;
                break;
            default: // Default text size
                textSize = 28;
                break;
        }

        TextView add = findViewById(R.id.addButton);
        add.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        // Set up the menu button click listener
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
            public void onClick(View view) {
                Intent intent = new Intent(Presets_Screen.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        // Call the setupNavigationMenu method from NavigationUtils
        NavigationUtils.setupNavigationMenu(navigationView, drawerLayout, this);

        recyclerViewPresets = findViewById(R.id.recycler_view_presets);
        recyclerViewPresets.setLayoutManager(new LinearLayoutManager(this));

        linearLayoutContainer = findViewById(R.id.linear_layout_container);

        fetchPresets();

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });
    }


    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    private void fetchPresets() {
        PresetFetcher presetFetcher = new PresetFetcher(this, new PresetFetcher.PresetListener() {
            @Override
            public void onPresetsReceived(List<Preset> presets) {
                // Clear existing views
                linearLayoutContainer.removeAllViews();

                // Populate presets dynamically
                for (Preset preset : presets) {
                    LinearLayout presetLayout = new LinearLayout(Presets_Screen.this);
                    presetLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    presetLayout.setOrientation(LinearLayout.HORIZONTAL);

                    TextView textView = new TextView(Presets_Screen.this);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                    textView.setText(preset.getPresetName());
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                    textView.setAllCaps(true);
                    int textColor = ContextCompat.getColor(Presets_Screen.this, R.color.blue);
                    textView.setTextColor(textColor);
                    Typeface typeface = ResourcesCompat.getFont(Presets_Screen.this, R.font.mplus);
                    textView.setTypeface(typeface);

                    Button deleteButton = new Button(Presets_Screen.this);
                    LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    buttonLayoutParams.setMargins(10, 0, 0, 0);
                    deleteButton.setLayoutParams(buttonLayoutParams);
                    deleteButton.setText("X");
                    deleteButton.setTextSize(36);
                    deleteButton.setTextColor(ContextCompat.getColor(Presets_Screen.this, R.color.red));
                    deleteButton.setBackgroundColor(ContextCompat.getColor(Presets_Screen.this, android.R.color.transparent));
                    deleteButton.setPadding(10, 5, 10, 5);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int presetId = preset.getPresetId();
                            linearLayoutContainer.removeView(presetLayout);
                            deletePreset(presetId);
                        }
                    });

                    presetLayout.addView(textView);
                    presetLayout.addView(deleteButton);
                    linearLayoutContainer.addView(presetLayout);
                }
            }
        });
        // Execute the PresetFetcher task
        presetFetcher.execute();
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Preset");

        // Set up the input fields
        final EditText presetNameInput = new EditText(this);
        presetNameInput.setHint("Preset Name");
        final EditText presetPriceInput = new EditText(this);
        presetPriceInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        presetPriceInput.setHint("Preset Price");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(presetNameInput);
        layout.addView(presetPriceInput);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String presetName = presetNameInput.getText().toString().trim();
                String presetPriceText = presetPriceInput.getText().toString().trim();
                if (!presetName.isEmpty() && !presetPriceText.isEmpty()) {
                    double presetPrice = Double.parseDouble(presetPriceText);
                    // Here you can handle adding the preset with the entered name and price
                    new AsyncTask<String, Void, Void>() {
                        @Override
                        protected Void doInBackground(String... params) {
                            String presetName = params[0];
                            double presetPrice = Double.parseDouble(params[1]);
                            PresetFetcher presetFetcher = new PresetFetcher(Presets_Screen.this, new PresetFetcher.PresetListener(){
                                @Override
                                public void onPresetsReceived(List<Preset> presets) {
                                    // Optionally handle any UI updates after adding the preset
                                }
                            });
                            presetFetcher.addPreset(presetName, presetPrice);
                            Log.d("Preset", "Preset added: " + presetName + " - " + presetPrice);
                            return null;
                        }
                    }.execute(presetName, String.valueOf(presetPrice));
                    fetchPresets();
                } else {
                    Toast.makeText(Presets_Screen.this, "Preset name and price cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }






    private void deletePreset(int presetId) {
        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                int presetIdToDelete = params[0];
                PresetFetcher presetFetcher = new PresetFetcher(Presets_Screen.this, new PresetFetcher.PresetListener() {
                    @Override
                    public void onPresetsReceived(List<Preset> presets) {
                        // You can choose to implement this if necessary.
                    }
                });
                presetFetcher.deletePreset(presetIdToDelete);
                return null;
            }
        }.execute(presetId);
    }
}
