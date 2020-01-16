package com.example.weatherviewer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapApi {

    String OPEN_WEATHER_BASE = "https://api.openweathermap.org/";

    @GET("data/2.5/forecast/daily")
    Call<WeatherResponse> forecast16days(@Query("q") String city, @Query("appid") String appid);

}
