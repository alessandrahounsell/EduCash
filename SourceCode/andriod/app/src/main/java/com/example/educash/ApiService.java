package com.example.educash;

import java.time.LocalDateTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;

import retrofit2.http.Headers;

import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {
    @POST("User")
    Call<String> registerUser(@Body UserRegistrationRequest userRegistrationRequest);

    @GET("User")
    Call<List<LIPInfo>> getLIPInfo(@Query("token") String token);

    @POST("Auth/Login")
    Call<String> login(@Body LoginRequest loginRequest);


    @POST("Auth/LoginWithPin")
    Call<Boolean> loginWithPin(@Query("pin") String pin, @Query("token") String token);

    @GET("Categorie/GetCategories")

    /*@GET("Categorie/GetCategories")


    Call<String> getCategories(
            @Query("categorieId") Integer categorieId,
            @Query("categorieName") String categorieName
    );*/

    Call<String> getCategories(@Header("Authorization") String authToken);

    @PUT("User/ChangeLIP")
    Call<List<LipChange>> changeLIP(@Query("token") String token, @Query("LIP") boolean lip);

    @GET("Stats/Weekly")
    Call<List<StatisticsItem>> getWeeklyStatistics(@Query("token") String token, @Query("startDate") LocalDateTime startDate);

    @GET("Stats/Monthly")
    Call<List<StatisticsItem>> getMonthlyStatistics(@Query("token")String token, @Query("month") int month, @Query("year") int year);

    @GET("Stats/Termly")
    Call<List<StatisticsItem>> getTermlyStatistics(@Query("token") String token, @Query ("termSelect")int termSelect);
}

