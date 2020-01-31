package com.example.weatherviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherviewer.Utils.Clock;
import com.example.weatherviewer.open_weather_map.Weather;

import java.util.HashMap;
import java.util.List;

public class WeatherRecyclerViewAdapter extends RecyclerView.Adapter<WeatherRecyclerViewAdapter.ViewHolder> {

    private HashMap<String, Bitmap> bitmaps = new HashMap<>();
    private static final String TAG = WeatherRecyclerViewAdapter.class.getSimpleName();
    private Clock clock = new Clock(TAG);
    private List<Weather> forecast;
    private boolean useGlide = false;
    private boolean useCache = true;
    private boolean usePool = false;
    private int holderCount = 0;

    public WeatherRecyclerViewAdapter(List<Weather> forecast) {
        super();
        this.forecast = forecast;
        clock.init();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //clock.logMessageElapsedTimeMillis("onCreateViewHolder:called");
        clock.logMessageElapsedTimeMillis("onCreateViewHolder:thread: " + Thread.currentThread().getName());
        long startCreateHolder = clock.getElapsedTimeMillis();
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
        clock.logMessageElapsedTimeMillis("onCreateViewHolder: create holder " + ++holderCount, startCreateHolder);
        // clock.logMessageElapsedTimeMillis("onCreateViewHolder:finished");
        viewHolder.itemView.setTag(holderCount);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        long startBinding = clock.getElapsedTimeMillis();
        holder.bind(forecast.get(position), holder.itemView.getContext());
        clock.logMessageElapsedTimeMillis("onBindViewHolder:time to bind data " + holder.itemView.getTag(), startBinding);
    }

    @Override
    public int getItemCount() {
        return forecast.size();
    }

    public void restartClock() {
        clock.init();
    }

    public void setUseGlide(boolean useGlide) {
        this.useGlide = useGlide;
    }

    public void setUseCache(boolean useCache) {
        this.useGlide = useCache;
    }

    public void cleanCache() {
        bitmaps.clear();
    }

    public void setUsePool(boolean usePool) {
        this.usePool = usePool;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView conditionImageView;
        private TextView dayTextView;
        private TextView lowTextView;
        private TextView hiTextView;
        private TextView humidityTextView;
        private Weather weather;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            conditionImageView = itemView.findViewById(R.id.conditionImageView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            lowTextView = itemView.findViewById(R.id.lowTextView);
            hiTextView = itemView.findViewById(R.id.highTextView);
            humidityTextView = itemView.findViewById(R.id.humidityTextView);
        }

        void bind(Weather weather, Context context) {
            this.weather = weather;
            dayTextView.setText(context.getString(R.string.day_description, weather.dayOfWeek, weather.description));
            lowTextView.setText(context.getString(R.string.low_temp, weather.minTemp));
            hiTextView.setText(context.getString(R.string.high_temp, weather.maxTemp));
            humidityTextView.setText(context.getString(R.string.humidity, weather.humidity));
            bindImage();
        }

        private void bindImage() {
            if (useGlide) {
                Glide.with(itemView)
                        .load(weather.iconURL)
                        .into(conditionImageView);
                return;
            }
            if (useCache && bitmaps.containsKey(weather.iconURL)) {
                conditionImageView.setImageBitmap(bitmaps.get(weather.iconURL));
            } else {
                if (usePool) {
                    new ImageDownloadTask(conditionImageView, useCache ? bitmaps : null)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, weather.iconURL);
                } else {
                    new ImageDownloadTask(conditionImageView, useCache ? bitmaps : null)
                            .execute(weather.iconURL);
                }
            }


        }
    }
}

