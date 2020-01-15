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

import com.example.weatherviewer.Utils.Clock;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class WeatherArrayAdapter extends ArrayAdapter<Weather> {

    /**
     * This a memory cache holding images already decoded from internet
     * Is a resource shared by the Main Thread and the worker thread that posts here
     */
    private HashMap<String, Bitmap> bitmaps = new HashMap<>();
    private static final String TAG = WeatherArrayAdapter.class.getSimpleName();

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

        ImageDownloadTask(ImageView mImageView) {
            this.mImageView = mImageView;
        }

        /**
         * All processing happens in a worker thread
         * Opens a connection to a web service
         * Reads al bytes and produces a Bitmap out of them
         * Saves the bitmap in a HashMap for caching
         *
         * @param urls contains the url to connect to at position 0
         * @return the bitmap decoded from the input stream
         */
        @Override
        protected Bitmap doInBackground(String... urls) {
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            Bitmap bitmap;
            Clock clock = new Clock();
            try {
                clock.init();
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                inputStream = connection.getInputStream();
                Log.d(TAG, "doInBackground: open connection an getting input stream  " + clock.getElapsedTimeMillis());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
            }
            clock.init();
            bitmap = BitmapFactory.decodeStream(inputStream);
            Log.d(TAG, "doInBackground:decoding to Bitmap " + clock.getElapsedTimeMillis());
            bitmaps.put(urls[0], bitmap);
            return bitmap;
        }

        /**
         * Method executed in Main thread to update image
         *
         * @param bitmap to be set it into ImageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
        }
    }

}
