package com.example.weatherviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.rtt.CivicLocationKeys;
import android.os.AsyncTask;
import android.os.SystemClock;
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

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class WeatherArrayAdapter extends ArrayAdapter<Weather> {

    /**
     * This a memory cache holding images already decoded from internet
     * Is a resource shared by the Main Thread and the worker thread that posts here
     */
    private HashMap<String, Bitmap> bitmaps = new HashMap<>();
    private static final String TAG = WeatherArrayAdapter.class.getSimpleName();
    private Clock clock = new Clock();

    public WeatherArrayAdapter(@NonNull Context context, List<Weather> forecast) {
        super(context, -1, forecast);
        clock.init();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d(TAG, clock.messageElapsedTimeMillis("getView:called: "));
        Log.d(TAG, "getView:current thread " + Thread.currentThread().getName());
        Weather weather = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            long startToBuildViewHolder = clock.getElapsedTimeMillis();
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
            Log.d(TAG, clock.messageElapsedTimeMillis("getView: create holder, create view, bind views and set tag-holder to view: ", startToBuildViewHolder));
        } else {
            // Gets the holder which helps to bind data to views
            viewHolder = (ViewHolder) convertView.getTag();
        }
        long startBinding = clock.getElapsedTimeMillis();
        viewHolder.bind(weather, getContext());
        Log.d(TAG, clock.messageElapsedTimeMillis("getView: time to make binding: ", startBinding));
        return convertView;
    }

    public void restartClock() {
        clock.init();
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
            if (bitmaps.containsKey(weather.iconURL)) {
                conditionImageView.setImageBitmap(bitmaps.get(weather.iconURL));
            } else {
                Log.d(TAG, clock.messageElapsedTimeMillis("bind: abut to download image "));
                new ImageDownloadTask(conditionImageView).executeOnExecutor(THREAD_POOL_EXECUTOR, weather.iconURL);
            }
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
        protected synchronized Bitmap doInBackground(String... urls) {
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            Bitmap bitmap;
            Log.d(TAG, "doInBackground: thread: " + Thread.currentThread().getName());
            try {
                long startGettingInputstream = clock.getElapsedTimeMillis();
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                inputStream = connection.getInputStream();
                Log.d(TAG, clock.messageElapsedTimeMillis("doInBackground: time to get input stream: ", startGettingInputstream));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
            }
            bitmap = BitmapFactory.decodeStream(inputStream);
            Log.d(TAG, clock.messageElapsedTimeMillis("doInBackground:put in map "));
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
