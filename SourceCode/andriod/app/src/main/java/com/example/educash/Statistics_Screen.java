package com.example.educash;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Statistics_Screen extends AppCompatActivity {

    private DrawerLayout drawerLayout; // Declare drawerLayout as a class-level variable
    private PieChart pieChart;
    private ApiManager apiManager;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "YourPrefs";
    private static final String API_RESPONSE_KEY = "apiResponse";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_screen);

        // Initialize drawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);

        NavigationUtils.setupNavigationMenu(navigationView, drawerLayout, this);
        Button menuButton = findViewById(R.id.menu_button);
        Button weeklyStats = findViewById(R.id.weekly_btn);
        Button monthlyStats = findViewById(R.id.monthly_btn);
        Button termlyStats = findViewById(R.id.termly_btn);

        apiManager = new ApiManager(this);

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
                Intent intent = new Intent(Statistics_Screen.this, HomeScreen.class);
                startActivity(intent);
            }
        });
        weeklyStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchWeeklyStatisticsData();
                fetchWeeklyStatisticsData();
            }
        });

        monthlyStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMonthlyStatistcs();
                fetchMonthlyStatistcs();
            }
        });

        termlyStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchTermlyStatistics();
                fetchTermlyStatistics();
            }
        });

        // Call the method to fetch weekly statistics data for a base chart on the screen when the user loads the screen up
        fetchWeeklyStatisticsData();
        //Call twice to fix a layout bug
        fetchWeeklyStatisticsData();
    }

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.END);
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
        legend.setTextColor(Color.WHITE);
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
        pieChart.setHoleColor(android.R.color.transparent);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setCenterText("Weeks Stats");
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setCenterTextSize(16f);
        pieChart.animateXY(1000, 1000);
        pieChart.invalidate();
    }

    // Monthly stats fetch code
    private void fetchMonthlyStatistcs(){
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String token = sharedPreferences.getString(API_RESPONSE_KEY, null);
        Log.d("API RESPONSE",  token);
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();
        LocalDate previousMonthDate = currentDate.minusMonths(1);
        int previousMonth = 6;
        int correctYear = previousMonthDate.getYear();
        Log.d("PreviousMonth","Previous Month is "+previousMonth);
        Log.d("Correct Year", "Correct Year is" + correctYear);
        apiManager.getMonthlyStatistics(token, previousMonth, correctYear, new Callback<List<StatisticsItem>>() {
            @Override
            public void onResponse(Call<List<StatisticsItem>> call, Response<List<StatisticsItem>> response) {
                if (response.isSuccessful()) {
                    List<StatisticsItem> statisticsItems = response.body();
                    populatePieChartMonthly(statisticsItems);
                } else {
                    Log.e("API Manager", "Failed to retrieve monthly statistics data");
                }
            }

            @Override
            public void onFailure(Call<List<StatisticsItem>> call, Throwable t) {
                Log.e("API Manager", "Failed to fetch monthly statistics data: " + t.getMessage());
            }
        });
    }

    //Monthly Stats pie chart
    private void populatePieChartMonthly(List<StatisticsItem> statisticsItems){
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

        // Create a pie data object from dataset
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart)); // Set percentage formatting

        // Set data to chart
        pieChart.setData(data);

        // Display the pie chart
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.R.color.transparent);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setCenterText("Monthly Stats");
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setCenterTextSize(16f);
        pieChart.animateXY(1000, 1000);
        Legend legend = pieChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(10f); // Set the offset as needed

        // Set legend entries manually
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

        pieChart.invalidate();
    }

    public static LocalDateTime getOneWeekAgo(LocalDateTime currentDate) {
        return currentDate.minusWeeks(1);
    }

    public static int getTermFromMonth(int month) {
        if (month >= 9 && month <= 12) { // September-December
            return 1;
        } else if (month >= 1 && month <= 3) { // January-March
            return 2;
        } else { // April-August
            return 3;
        }
    }

    public static int getCurrentTerm() {
            LocalDate currentDate = LocalDate.now();
            int currentMonth = currentDate.getMonthValue();
            return getTermFromMonth(currentMonth);
    }


    public void fetchTermlyStatistics() {
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String token = sharedPreferences.getString(API_RESPONSE_KEY, null);
        Log.d("API RESPONSE",  token);
        LocalDate currentDate = LocalDate.now();
        int currentMonth = 6;
        int term = getTermFromMonth(currentMonth);
        apiManager.getTermlyStatistics(token, term, new Callback<List<StatisticsItem>>() {
            @Override
            public void onResponse(Call<List<StatisticsItem>> call, Response<List<StatisticsItem>> response) {
                if (response.isSuccessful()) {
                    List<StatisticsItem> statisticsItems = response.body();
                    populatePieChartTermly(statisticsItems);
                } else {
                    Log.e("API Manager", "Failed to retrieve Termly statistics data");
                }

            }

            @Override
            public void onFailure(Call<List<StatisticsItem>> call, Throwable t) {
                Log.e("API Manager", "Failed to fetch Termly statistics data: " + t.getMessage());
            }
        });
    }




    public void populatePieChartTermly(List<StatisticsItem> statisticsItems){pieChart = findViewById(R.id.pieChart);
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

        // Create a pie data object from dataset
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart)); // Set percentage formatting

        // Set data to chart
        pieChart.setData(data);

        // Display the pie chart
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.R.color.transparent);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setCenterText("Termly Stats");
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setCenterTextSize(16f);
        pieChart.animateXY(1000, 1000);
        Legend legend = pieChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(10f); // Set the offset as needed

        // Set legend entries manually
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

        pieChart.invalidate();
    }

}
