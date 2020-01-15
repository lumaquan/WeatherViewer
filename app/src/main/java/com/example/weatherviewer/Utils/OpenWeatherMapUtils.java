package com.example.weatherviewer.Utils;

import android.net.Uri;
import android.util.Log;
import android.content.Context;

import com.example.weatherviewer.R;
import com.example.weatherviewer.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class OpenWeatherMapUtils {

    private static final String TAG = OpenWeatherMapUtils.class.getSimpleName();

    // Tags to recognize different elements in the json response
    private static final String LIST = "list";
    private static final String TEMP = "temp";
    private static final String WEATHER = "weather";
    private static final String DT = "dt";
    private static final String MIN = "min";
    private static final String MAX = "max";
    private static final String HUMIDITY = "humidity";
    private static final String DESCRIPTION = "description";
    private static final String ICON = "icon";

    // Query parameters
    private static final String Q = "q";
    private static final String UNITS = "units";
    private static final String CNT = "cnt";
    private static final String APPID = "appid";

    public static List<Weather> extractForecast(JSONObject forecast) throws JSONException {
        JSONArray weatherDays = forecast.getJSONArray(LIST);
        List<Weather> weatherList = new ArrayList<>();
        for (int i = 0; i < weatherDays.length(); ++i) {
            JSONObject day = weatherDays.getJSONObject(i);
            JSONObject temperatures = day.getJSONObject(TEMP);
            JSONObject weather = day.getJSONArray(WEATHER).getJSONObject(0);
            weatherList.add(new Weather(
                    day.getLong(DT),
                    temperatures.getDouble(MIN),
                    temperatures.getDouble(MAX),
                    day.getDouble(HUMIDITY),
                    weather.getString(DESCRIPTION),
                    weather.getString(ICON)));
        }
        return weatherList;
    }

    public static URL createURL(Context context, String city) {
        String apiKey = context.getString(R.string.api_key);
        String baseUrl = context.getString(R.string.web_service_url);
        Uri uri = Uri.parse(baseUrl);
        try {
            uri = uri.buildUpon().appendQueryParameter(Q, city)
                    .appendQueryParameter(UNITS, "imperial")
                    .appendQueryParameter(CNT, "16")
                    .appendQueryParameter(APPID, apiKey)
                    .build();
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "createURL: " + e.getLocalizedMessage());
        }
        return null;
    }
}
