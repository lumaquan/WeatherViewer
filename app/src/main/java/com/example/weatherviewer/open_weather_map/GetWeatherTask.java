package com.example.weatherviewer.open_weather_map;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.example.weatherviewer.Utils.Clock;
import com.example.weatherviewer.Utils.OpenWeatherMapUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class GetWeatherTask extends AsyncTask<URL, Void, List<Weather>> {

    private static final String TAG = GetWeatherTask.class.getSimpleName();
    private Clock clock = new Clock();
    private OpenWeatherMapApi.Weather16DaysCallback mWeather16DaysCallback;
    private int delayMillis = 0;


    public GetWeatherTask(OpenWeatherMapApi.Weather16DaysCallback callback) {
        super();
        this.mWeather16DaysCallback = callback;
    }

    @Override
    protected List<Weather> doInBackground(URL... urls) {
        clock.init();
        HttpURLConnection connection = null;
        String json = null;
        try {
            connection = (HttpURLConnection) urls[0].openConnection();
            int code = connection.getResponseCode();
            showDetailsConnection(connection);
            if (code == HttpURLConnection.HTTP_OK) {
                json = getJsonStringFromInputStream(connection.getInputStream());
                clock.logMessageElapsedTimeMillis(TAG, "doInBackground: time to read json: ");
            } else {
                clock.messageElapsedTimeMillis("doInBackground: time to fetch: ");
            }
        } catch (IOException e) {
            if (mWeather16DaysCallback != null) {
                mWeather16DaysCallback.error("Unable to connect to server");
            }
            clock.messageElapsedTimeMillis("doInBackground: exception getting json: ");
        } finally {
            connection.disconnect();
        }

        long startToExtractWeather = clock.getElapsedTimeMillis();
        if (json != null) {
            try {
                List<Weather> forecast = OpenWeatherMapUtils.extractForecast(new JSONObject(json));
                clock.logMessageElapsedTimeMillis(TAG, "doInBackground:time to extract forecast: ", startToExtractWeather);
                Thread.sleep(delayMillis);
                return forecast;
            } catch (JSONException | InterruptedException e) {
                if (mWeather16DaysCallback != null) {
                    mWeather16DaysCallback.error("Unable to connect to server");
                }
                clock.logMessageElapsedTimeMillis(TAG, "doInBackground:time to extract forecast: ", startToExtractWeather);
            }
        }
        return null;
    }


    @Override
    protected void onPostExecute(List<Weather> weather) {
        clock.logMessageElapsedTimeMillis(TAG, "onPostExecute: time to get response parsed an ready to use: ");
        if (weather != null && weather.size() > 0) {
            if (mWeather16DaysCallback != null) {
                mWeather16DaysCallback.getWeather(weather);
            }
        } else {
            mWeather16DaysCallback.error("Unable to connect to server");
        }
    }

    private void showDetailsConnection(HttpURLConnection connection) throws IOException {
        Log.d(TAG, "doInBackground:response message: " + connection.getResponseMessage());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "doInBackground:content length: " + connection.getContentLengthLong());
        }
        Log.d(TAG, "doInBackground:last modified: " + connection.getLastModified());
    }

    private String getJsonStringFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }
    }

    private String getJsonStringFromInputStream2(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }
    }


}



