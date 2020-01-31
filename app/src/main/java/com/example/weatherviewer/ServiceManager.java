package com.example.weatherviewer;

import com.example.weatherviewer.Utils.MyInterceptor;
import com.example.weatherviewer.open_weather_map.OpenWeatherMapApi;
import com.example.weatherviewer.json_placeholder.JsonPlaceholderAPI;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceManager {

    private static OpenWeatherMapApi openWeatherMapApi;
    private static JsonPlaceholderAPI jsonPlaceholderAPI;
    private static Object lock = new Object();

    private ServiceManager() {
    }

    public static OpenWeatherMapApi getOpenWeatherService() {
        if (openWeatherMapApi == null) {
            synchronized (lock) {
                openWeatherMapApi = new Retrofit.Builder()
                        .baseUrl(OpenWeatherMapApi.OPEN_WEATHER_BASE)
                        .client(getClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(OpenWeatherMapApi.class);
            }
        }
        return openWeatherMapApi;
    }

    public static JsonPlaceholderAPI getJsonPlaceholderClient() {
        if (jsonPlaceholderAPI == null) {
            synchronized (lock) {
                jsonPlaceholderAPI = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(getClient())
                        .baseUrl(JsonPlaceholderAPI.BASE_URL)
                        .build().create(JsonPlaceholderAPI.class);
            }
        }
        return jsonPlaceholderAPI;
    }

    private static OkHttpClient getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(new StethoInterceptor());
        builder.addInterceptor(new MyInterceptor());
        return builder.build();
    }
}
