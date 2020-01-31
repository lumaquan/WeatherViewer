package com.example.weatherviewer.open_weather_map;

import android.util.Log;

import com.example.weatherviewer.ServiceManager;
import com.example.weatherviewer.Utils.Clock;
import com.example.weatherviewer.Utils.OpenWeatherMapUtils;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapApi {

    String OPEN_WEATHER_BASE = "https://api.openweathermap.org/";
    String APPID = "56529b2d2e5475be0800bfcd97f3e1f0";
    String TAG = OpenWeatherMapApi.class.getSimpleName();
    String noSuccessful = "no successful";
    String networkError = "network error";
    String error = "error";
    Clock clock = new Clock();

    @GET("data/2.5/forecast/daily?cnt=16&units=imperial")
    Call<WeatherResponse> forecast16days(@Query("q") String city, @Query("appid") String appid);

    public interface Weather16DaysCallback {

        void getWeather(List<Weather> forecast);

        void error(String error);
    }

    static void useRetrofit(String cityNotEncoded, Weather16DaysCallback callback) {
        clock.init();
        Call<WeatherResponse> call = ServiceManager.getOpenWeatherService().forecast16days(cityNotEncoded, APPID);
        clock.logMessageElapsedTimeMillis(TAG, "useRetrofit: Time to create client : ");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                clock.logMessageElapsedTimeMillis(TAG, "onResponse:time to get response (not transformed): ");
                if (response.isSuccessful()) {
                    long startToParse = clock.getElapsedTimeMillis();
                    List<Weather> forecast = OpenWeatherMapUtils.extractWeatherFromRetrofitResponse(response.body());
                    clock.logMessageElapsedTimeMillis(TAG, "onResponse:time to transform response: ", startToParse);
                    clock.logMessageElapsedTimeMillis(TAG, "onResponse:time to get response (transformed) : ");
                    if (callback != null) {
                        callback.getWeather(forecast);
                    }
                } else {
                    clock.logMessageElapsedTimeMillis(TAG, "onResponse:" + noSuccessful);
                    if (callback != null) {
                        callback.error(noSuccessful);
                    }
                }

            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                clock.logMessageElapsedTimeMillis(TAG, "onFailure: time to get response (not transformed): ");
                if (t instanceof IOException) {
                    clock.logMessageElapsedTimeMillis(TAG, "onFailure:" + networkError);
                } else {
                    clock.logMessageElapsedTimeMillis(TAG, "onFailure:" + error);
                }
                if (callback != null) {
                    callback.error(error);
                }
            }
        });
    }

}
