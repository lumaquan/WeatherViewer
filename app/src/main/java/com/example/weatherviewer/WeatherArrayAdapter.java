package com.example.weatherviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.weatherviewer.Utils.Clock;
import com.example.weatherviewer.open_weather_map.Weather;

import java.util.HashMap;
import java.util.List;

public class WeatherArrayAdapter extends ArrayAdapter<Weather> {

    private HashMap<String, Bitmap> bitmaps = new HashMap<>();
    private static final String TAG = WeatherArrayAdapter.class.getSimpleName();
    private Clock clock = new Clock(TAG);
    private boolean useGlide = true;
    private boolean useCache = true;
    private boolean usePool = false;

    public WeatherArrayAdapter(@NonNull Context context, List<Weather> forecast) {
        super(context, -1, forecast);
        clock.init();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        clock.logMessageElapsedTimeMillis("getView:called");
        clock.logMessageElapsedTimeMillis("getView:thread: " + Thread.currentThread().getName());

        Weather weather = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            long startToBuildViewHolder = clock.getElapsedTimeMillis();
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            viewHolder.conditionImageView = convertView.findViewById(R.id.conditionImageView);
            viewHolder.dayTextView = convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView = convertView.findViewById(R.id.lowTextView);
            viewHolder.hiTextView = convertView.findViewById(R.id.highTextView);
            viewHolder.humidityTextView = convertView.findViewById(R.id.humidityTextView);
            convertView.setTag(viewHolder);
            clock.logMessageElapsedTimeMillis("getView: create holder, create view, bind views and set tag-holder to view", startToBuildViewHolder);
        } else {
            // Gets the holder which helps to bind data to views
            viewHolder = (ViewHolder) convertView.getTag();
        }
        long startBinding = clock.getElapsedTimeMillis();
        viewHolder.bind(weather, getContext());
        clock.logMessageElapsedTimeMillis("getView: time to make binding", startBinding);
        clock.logMessageElapsedTimeMillis("getView:finshed");
        return convertView;
    }

    public void restartClock() {
        clock.init();
    }

    public void setUseGlide(boolean useGlide) {
        this.useGlide = useGlide;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }
    public void cleanCache(){
        bitmaps.clear();
    }

    public void setUsePool(boolean usePool){
        this.usePool = usePool;
    }

    private class ViewHolder {

        private ImageView conditionImageView;
        private TextView dayTextView;
        private TextView lowTextView;
        private TextView hiTextView;
        private TextView humidityTextView;

        void bind(Weather weather, Context context) {
            dayTextView.setText(context.getString(R.string.day_description, weather.dayOfWeek, weather.description));
            lowTextView.setText(context.getString(R.string.low_temp, weather.minTemp));
            hiTextView.setText(context.getString(R.string.high_temp, weather.maxTemp));
            humidityTextView.setText(context.getString(R.string.humidity, weather.humidity));

            if (useGlide) {
                Glide.with(context).load(weather.iconURL).into(conditionImageView);
                return;
            }
            if (useCache && bitmaps.containsKey(weather.iconURL)) {
                conditionImageView.setImageBitmap(bitmaps.get(weather.iconURL));
            } else {
                if(usePool){
                    new ImageDownloadTask(conditionImageView, useCache ? bitmaps : null)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,weather.iconURL);
                }else{
                    new ImageDownloadTask(conditionImageView, useCache ? bitmaps : null)
                            .execute(weather.iconURL);
                }


            }
        }
    }

}
