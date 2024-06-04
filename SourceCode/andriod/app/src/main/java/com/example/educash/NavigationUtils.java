package com.example.educash;

import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class NavigationUtils {

    public static void setupNavigationMenu(NavigationView navigationView, DrawerLayout drawerLayout, AppCompatActivity activity) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                Log.d("NavItemClick", "Item clicked: " + itemId);

                Class<?> targetActivity = null;

                if (itemId == R.id.home) {
                    Log.d("NavigationUtils", "Clicked on Home");
                    targetActivity = HomeScreen.class;
                } else if (itemId == R.id.history) {
                    Log.d("NavigationUtils", "Clicked on History");
                    targetActivity = History_Screen.class;
                } else if (itemId == R.id.settings) {
                    Log.d("NavigationUtils", "Clicked on Settings");
                    targetActivity = Settings_Screen.class;
                } else if (itemId == R.id.presets) {
                    Log.d("NavigationUtils", "Clicked on Presets");
                    targetActivity = Presets_Screen.class;
                } else if (itemId == R.id.categories) {
                    Log.d("NavigationUtils", "Clicked on Categories");
                    targetActivity = Categories_Screen.class;
                } else if (itemId == R.id.statistics) {
                    Log.d("NavigationUtils", "Clicked on Statistics");
                    targetActivity = Statistics_Screen.class;

                }

                if (targetActivity != null) {
                    startNewActivity(activity, targetActivity);
                }

                // Close the drawer after handling the item click
                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            }
        });
    }

    private static void startNewActivity(AppCompatActivity currentActivity, Class<?> targetActivity) {
        Intent intent = new Intent(currentActivity, targetActivity);
        currentActivity.startActivity(intent);
    }
}
