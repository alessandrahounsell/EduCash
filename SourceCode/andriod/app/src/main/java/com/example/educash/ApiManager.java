package com.example.educash;

import static android.content.Context.MODE_PRIVATE;
import static com.example.educash.Login_Page.API_RESPONSE_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.MalformedJsonException;

import java.time.LocalDateTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ApiManager {
    private ApiService apiService;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "APItoken";



    public ApiManager(Context context) {
        // Initialize the ApiService using RetrofitClient
        apiService = RetrofitClient.getApiService();
        // Initialize SharedPreferences with the provided context
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }



    private static final String TAG = "ApiManager";

    public ApiManager() {
        // Initialize the ApiService using RetrofitClient
        apiService = RetrofitClient.getApiService();
    }


    public void registerUser(String username, String password, String pin, boolean lip, Callback<String> callback) {
        // Create a UserRegistrationRequest instance with the provided parameters
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(username, password, pin, lip);

        // Make the user registration API request
        Call<String> call = apiService.registerUser(userRegistrationRequest);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response
                    String result = response.body();
                    Log.d(TAG, "onResponse: " + result);  // Log the successful response
                    callback.onResponse(call, Response.success(result));
                } else {
                    // Handle unsuccessful response
                    Log.e("ApiManager", "Unsuccessful Response - HTTP Code: " + response.code());
                    Log.e(TAG, "onFailure: Unsuccessful response");  // Log the unsuccessful response
                    callback.onFailure(call, new Throwable("Unsuccessful response"));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Handle failure
                Log.e(TAG, "onFailure: " + t.getMessage());  // Log the failure message
                callback.onFailure(call, t);
            }
        });


    }

    public void login(String username, String password, Callback<String> callback) {
        // Create a LoginRequest instance with the provided parameters
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Make the login API request
        Call<String> call = apiService.login(loginRequest);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response
                    String result = response.body();
                    Log.d(TAG, "onResponse: " + result);  // Log the successful response
                    if (callback != null) {
                        callback.onResponse(call, Response.success(result));
                    }
                } else {
                    // Handle unsuccessful response
                    Log.e(TAG, "Unsuccessful Response - HTTP Code: " + response.code());
                    if (callback != null) {
                        callback.onFailure(call, new Throwable("Unsuccessful response"));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Handle failure
                if (t instanceof MalformedJsonException) {
                    Log.e(TAG, "Malformed JSON Exception: " + t.getMessage());
                    // Additional handling for malformed JSON, if needed
                } else {
                    Log.e(TAG, "API Call Failed: " + t.getMessage());
                }
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }



   // public void getCategories(Callback<String> callback) {
       // String authToken = sharedPreferences.getString(API_RESPONSE_KEY, "");
    //    Call<String> call = apiService.getCategories("Bearer " + authToken); // Adjust the header as neede
        
  
  


    public void getUserLipInfo(String token, Callback<List<LIPInfo>> callback) {
        Call<List<LIPInfo>> call = apiService.getLIPInfo(token);
        call.enqueue(new Callback<List<LIPInfo>>() {
            @Override
            public void onResponse(Call<List<LIPInfo>> call, Response<List<LIPInfo>> response) {
                if (response.isSuccessful()) {
                    List<LIPInfo> lipInfoList = response.body();
                    // Process lipInfoList as needed
                    callback.onResponse(call, Response.success(lipInfoList));
                } else {
                    // Handle unsuccessful response
                    callback.onFailure(call, new Throwable("Unsuccessful response"));
                }
            }

            @Override
            public void onFailure(Call<List<LIPInfo>> call, Throwable t) {
                // Handle failure
                callback.onFailure(call, t);
            }
        });
    }


    public void loginWithPin(String pin, String token, Callback<Boolean> callback) {
        // Create a LoginWithPinRequest instance with the provided parameters
        LoginWithPinRequest loginWithPinRequest = new LoginWithPinRequest(pin, token);

        // Make the login with PIN API request
        Call<Boolean> call = apiService.loginWithPin(loginWithPinRequest.getPin(), loginWithPinRequest.getToken());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response
                    Boolean result = response.body();
                    callback.onResponse(call, Response.success(result));
                } else {
                    // Handle unsuccessful response
                    Log.e(TAG, "Unsuccessful Response - HTTP Code: " + response.code());
                    Log.e(TAG, "onFailure: Unsuccessful response");
                    callback.onFailure(call, new Throwable("Unsuccessful response"));
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                // Handle failure
                Log.e(TAG, "onFailure: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }

    public void getWeeklyStatistics(String token, LocalDateTime date, Callback<List<StatisticsItem>> callback) {
        Call<List<StatisticsItem>> call = apiService.getWeeklyStatistics(token, date);
        call.enqueue(new Callback<List<StatisticsItem>>() {
            @Override
            public void onResponse(Call<List<StatisticsItem>> call, Response<List<StatisticsItem>> response) {
                if (response.isSuccessful()) {
                    List<StatisticsItem> statisticsItems = response.body();
                    callback.onResponse(call, Response.success(statisticsItems));
                } else {
                    callback.onFailure(call, new Throwable("Failed to retrieve weekly statistics data"));
                }
            }
            @Override
            public void onFailure(Call<List<StatisticsItem>> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void getMonthlyStatistics(String token, int month, int year, Callback<List<StatisticsItem>>callback){
        Call<List<StatisticsItem>> call = apiService.getMonthlyStatistics(token, month, year);
        call.enqueue(new Callback<List<StatisticsItem>>() {
            @Override
            public void onResponse(Call<List<StatisticsItem>> call, Response<List<StatisticsItem>> response) {
                if (response.isSuccessful()) {
                    List<StatisticsItem> statisticsItems = response.body();
                    callback.onResponse(call, Response.success(statisticsItems));
                } else {
                    callback.onFailure(call, new Throwable("Failed to retrieve monthly statistics data"));
                }
            }

            @Override
            public void onFailure(Call<List<StatisticsItem>> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void getTermlyStatistics(String token, int termSelect, Callback<List<StatisticsItem>>callback){
        Call<List<StatisticsItem>> call = apiService.getTermlyStatistics(token,termSelect);
        call.enqueue(new Callback<List<StatisticsItem>>() {
            @Override
            public void onResponse(Call<List<StatisticsItem>> call, Response<List<StatisticsItem>> response) {
                if (response.isSuccessful()){
                    List<StatisticsItem> statisticsItems = response.body();
                    callback.onResponse(call, Response.success(statisticsItems));
                } else {
                    callback.onFailure(call, new Throwable("Failed to retrieve termly statistics data"));
                }
            }

            @Override
            public void onFailure(Call<List<StatisticsItem>> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });


    }




}

