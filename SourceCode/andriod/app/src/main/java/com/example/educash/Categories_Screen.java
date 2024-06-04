package com.example.educash;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class Categories_Screen extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerViewCategories;
    private LinearLayout linearLayoutContainer;

    private static final String TAG = "Category_Screen";

    private  float textSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_screen);

        // Retrieve text size preference and set text size
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String textSizePreference = prefs.getString("textSizePreference", "default");
        Log.d(TAG, "onCreate: textSizePreference: " + textSizePreference);


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



        Button menuButton = findViewById(R.id.menu_button);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer();
            }
        });

        Button logoButton = findViewById(R.id.logo_btn);
        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Categories_Screen.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        NavigationView navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationUtils.setupNavigationMenu(navigationView, drawerLayout, this);

        recyclerViewCategories = findViewById(R.id.recycler_view_categories);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));

        linearLayoutContainer = findViewById(R.id.linear_layout_container);

        fetchCategories();

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

    private void fetchCategories() {
        CategoryFetcher categoryFetcher = new CategoryFetcher(this, new CategoryFetcher.CategoryListener() {
            @Override
            public void onCategoriesReceived(List<Category> categories) {
                Categories_Screen.this.onCategoriesReceived(categories);
            }
        });
        categoryFetcher.execute();
    }

    public void onCategoriesReceived(List<Category> categories) {
        linearLayoutContainer.removeAllViews();

        for (Category category : categories) {
            LinearLayout categoryLayout = new LinearLayout(this);
            categoryLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            categoryLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            textView.setText(category.getCategorieName());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            textView.setAllCaps(true);
            int textColor = ContextCompat.getColor(Categories_Screen.this, R.color.blue);
            textView.setTextColor(textColor);
            Typeface typeface = ResourcesCompat.getFont(Categories_Screen.this, R.font.mplus);
            textView.setTypeface(typeface);

            Button deleteButton = new Button(this);
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonLayoutParams.setMargins(10, 0, 0, 0);
            deleteButton.setLayoutParams(buttonLayoutParams);
            deleteButton.setText("X");
            deleteButton.setTextSize(36);
            deleteButton.setTextColor(ContextCompat.getColor(Categories_Screen.this, R.color.red));
            deleteButton.setBackgroundColor(ContextCompat.getColor(Categories_Screen.this, android.R.color.transparent));
            deleteButton.setPadding(10, 5, 10, 5);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int categoryId = category.getCategorieId();
                    linearLayoutContainer.removeView(categoryLayout);
                    deleteCategory(categoryId);
                }
            });

            categoryLayout.addView(textView);
            categoryLayout.addView(deleteButton);
            linearLayoutContainer.addView(categoryLayout);
        }
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Category");

        // Set up the input
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String categoryName = input.getText().toString().trim();
                if (!categoryName.isEmpty()) {
                    // Here you can handle adding the category with the entered name
                    new AsyncTask<String, Void, Void>() {
                        @Override
                        protected Void doInBackground(String... params) {
                            String categoryName = params[0];
                            CategoryFetcher categoryFetcher = new CategoryFetcher(Categories_Screen.this, new CategoryFetcher.CategoryListener(){
                                @Override
                                public void onCategoriesReceived(List<Category> categories) {
                                    // Optionally handle any UI updates after adding the category
                                }
                            });
                            categoryFetcher.addCategory(categoryName);
                            return null;
                        }
                    }.execute(categoryName);
                    fetchCategories();
                } else {
                    Toast.makeText(Categories_Screen.this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
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



    private void deleteCategory(int categoryId) {
        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                int categoryIdToDelete = params[0];
                CategoryFetcher categoryFetcher = new CategoryFetcher(Categories_Screen.this, new CategoryFetcher.CategoryListener() {
                    @Override
                    public void onCategoriesReceived(List<Category> categories) {

                    }
                });
                categoryFetcher.deleteCategory(categoryIdToDelete);
                return null;
            }
        }.execute(categoryId);
    }
}