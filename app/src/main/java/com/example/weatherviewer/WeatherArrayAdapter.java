package com.example.weatherviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class WeatherArrayAdapter extends ArrayAdapter<Weather> {

    private HashMap<String, Bitmap> bitmaps = new HashMap<>();

    public WeatherArrayAdapter(@NonNull Context context, List<Weather> forecast) {
        super(context, -1, forecast);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Weather weather = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            // Seaches views in the view hierarchy rooted at convertView
            viewHolder.conditionImageView = convertView.findViewById(R.id.conditionImageView);
            viewHolder.dayTextView = convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView = convertView.findViewById(R.id.lowTextView);
            viewHolder.hiTextView = convertView.findViewById(R.id.highTextView);
            viewHolder.humidityTextView = convertView.findViewById(R.id.humidityTextView);
            // Attaches the view holder to the view
            convertView.setTag(viewHolder);
        } else {
            // Gets the holder which helps to bind data to views
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (bitmaps.containsKey(weather.iconURL)) {
            viewHolder.conditionImageView.setImageBitmap(bitmaps.get(weather.iconURL));
        } else {
            new ImageDownloadTask(viewHolder.conditionImageView).execute(weather.iconURL);
        }

        viewHolder.bind(weather, getContext());
        return convertView;
    }

    private static class ViewHolder {

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
        }
    }

    private class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImageView;

        public ImageDownloadTask(ImageView mImageView) {
            this.mImageView = mImageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {

            HttpURLConnection connection= null;
            InputStream inputStream = null;
            Bitmap bitmap;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                inputStream = connection.getInputStream();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
            }
            bitmap = BitmapFactory.decodeStream(inputStream);
            bitmaps.put(urls[0], bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
        }
    }

}
