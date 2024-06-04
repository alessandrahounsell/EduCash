package com.example.educash;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.example.educash.Statistics_Screen.getOneWeekAgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.MotionEvent;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.educash.Input_Screen;
import com.example.educash.NavigationUtils;
import com.example.educash.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreen extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView currentAmountTextView;
    private ApiManager apiManager;
    private PieChart pieChart;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "YourPrefs";
    private static final String API_RESPONSE_KEY = "apiResponse";
    private float x1, y1, x2, y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Set up the menu button click listener
        Button menuButton = findViewById(R.id.menu_button);
        //Input listener
        Button inputButton = findViewById(R.id.plus_button);


        Button outputButton = findViewById(R.id.minus_button);
        System.out.println("I found the button" + outputButton);

        currentAmountTextView = findViewById(R.id.CurrentValue);
        apiManager = new ApiManager(this);

        fetchWeeklyStatisticsData(); //something about the layout is broken and so it needs to be called twice ¯\_(ツ)_/¯ not entirely sure why but this is the quickest fix
        fetchWeeklyStatisticsData();

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer();
            }
        });

        inputButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openInput();
            }
        });

        outputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOutgoings();
            }
        });

        NavigationView navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        // Call the setupNavigationMenu method from NavigationUtils
        NavigationUtils.setupNavigationMenu(navigationView, drawerLayout, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load and set the value to the TextView
        //loadAndSetCurrentAmount();
        TotalFetcher totalFetcher = new TotalFetcher(this, new TotalFetcher.TotalListener() {
            @Override
            public void onTotalReceived(Double total) {
                currentAmountTextView.setText(String.valueOf(total));
            }
        });
        totalFetcher.execute();
    }

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    private void openInput() {
        Intent intent = new Intent(this, Input_Screen.class);
        startActivity(intent);
    }

    private void openOutgoings() {
        System.out.println("Im in the out goings open function");
        Intent intent = new Intent(this, Output_Screen.class);
        startActivity(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                float deltaX = x2 - x1;
                float deltaY = y2 - y1;
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (Math.abs(deltaX) > 100) {
                        if (deltaX > 0) {
                            // Right swipe
                            Intent i = new Intent(HomeScreen.this, Input_Screen.class);
                            startActivity(i);
                        } else {
                            // Left swipe
                            Intent i = new Intent(HomeScreen.this, Output_Screen.class);
                            startActivity(i);
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(touchEvent);
    }

    private void fetchWeeklyStatisticsData() {
        // Call getWeeklyStatistics method with token and date
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime oneWeekAgo = getOneWeekAgo(currentDate);
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String token = sharedPreferences.getString(API_RESPONSE_KEY, null);
        Log.d("API RESPONSE",  token);
        Log.d("Today", "Today"+ currentDate);
        Log.d("WeekAgo","Week Ago" + oneWeekAgo);

        // Call API method to fetch weekly statistics
        apiManager.getWeeklyStatistics(token, oneWeekAgo, new Callback<List<StatisticsItem>>() {
            @Override
            public void onResponse(Call<List<StatisticsItem>> call, Response<List<StatisticsItem>> response) {
                if (response.isSuccessful()) {
                    List<StatisticsItem> statisticsItems = response.body();
                    // Populate pie chart with statisticsItems data
                    populatePieChartWeekly(statisticsItems);
                } else {
                    // Handle unsuccessful response
                    Log.e(TAG, "Failed to retrieve weekly statistics data");
                }
            }

            @Override
            public void onFailure(Call<List<StatisticsItem>> call, Throwable t) {
                // Handle failure
                Log.e(TAG, "Failed to retrieve weekly statistics data", t);
            }
        });
    }

    private void populatePieChartWeekly(List<StatisticsItem> statisticsItems) {
         pieChart = findViewById(R.id.pieChart);
        int[] colors = new int[]{Color.parseColor("#7DF9FF"), Color.parseColor("#FEB2FF"), Color.parseColor("#D9FF76"),
                Color.parseColor("#FF4D4D"), Color.parseColor("#A680FF"), Color.parseColor("#7A5DFF")};
        // Create a data set for the pie chart
        List<PieEntry> entries = new ArrayList<>();
        for (StatisticsItem item : statisticsItems) {
            entries.add(new PieEntry((float) item.getPercentage(), ""));
        }

        // Create a pie data set
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(14f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE); // Set the position of the value text
// Set the position of the value text based on the percentage

// Create a pie data object from dataset
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart)); // Set percentage formatting

// Set data to chart
        pieChart.setData(data);
// Set legend entries manually
        Legend legend = pieChart.getLegend();
        legend.setTextColor(Color.parseColor("#122E8A"));
        List<LegendEntry> legendEntries = new ArrayList<>();
        for (StatisticsItem item : statisticsItems) {
            legendEntries.add(new LegendEntry(item.getCatName(), Legend.LegendForm.SQUARE, Float.NaN, Float.NaN, null, colors[statisticsItems.indexOf(item)]));
        }
        legend.setCustom(legendEntries);
        legend.setWordWrapEnabled(true); // Enable word wrap for legend entries
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false); // Disable drawing legend inside the chart
        legend.setXEntrySpace(10f); // Adjust horizontal spacing between legend entries
        legend.setYEntrySpace(5f); // Adjust vertical spacing between legend entries
        legend.setWordWrapEnabled(true);
        legend.setTextSize(14f);
// Display the pie chart
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setCenterText("Weeks Stats");
        pieChart.setCenterTextColor(Color.parseColor("#122E8A"));
        pieChart.setCenterTextSize(16f);
        pieChart.animateXY(1000, 1000);

        pieChart.invalidate();
    }


    }


