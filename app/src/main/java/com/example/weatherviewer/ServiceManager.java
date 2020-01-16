package com.example.weatherviewer;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceManager {

    private static OpenWeatherMapApi service;
    private static Object lock = new Object();

    private ServiceManager() {
    }

    public static OpenWeatherMapApi getOpenWeatherService() {
        if (service == null) {
            synchronized (lock) {
                service = new Retrofit.Builder()
                        .baseUrl(OpenWeatherMapApi.OPEN_WEATHER_BASE)
                        .client(getClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(OpenWeatherMapApi.class);
            }
        }
        return service;
    }

    private static OkHttpClient getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(new StethoInterceptor());
        builder.addInterceptor(new MyInterceptor());
        return builder.build();
    }
}
