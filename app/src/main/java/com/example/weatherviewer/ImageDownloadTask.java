package com.example.weatherviewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.weatherviewer.Utils.Clock;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView mImageView;
    private Map<String, Bitmap> bitmaps;
    private final String TAG = ImageDownloadTask.class.getSimpleName();
    private Clock clock = new Clock(TAG);

    ImageDownloadTask(ImageView mImageView, Map<String, Bitmap> bitmapMap) {
        this.mImageView = mImageView;
        this.bitmaps = bitmapMap;
    }

    @Override
    protected synchronized Bitmap doInBackground(String... urls) {
        clock.init();
        clock.logMessageElapsedTimeMillis("doInBackground:thread" + Thread.currentThread().getName());
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        Bitmap bitmap;
        long startGettingInputstream = 0;
        try {
            URL url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
          //  clock.logMessageElapsedTimeMillis("doInBackground: times elapsed before get inputstream");
            startGettingInputstream = clock.getElapsedTimeMillis();
            inputStream = connection.getInputStream();
           // clock.logMessageElapsedTimeMillis("doInBackground: time to get input stream", startGettingInputstream);

        } catch (IOException e) {
          //  clock.logMessageElapsedTimeMillis("doInBackground: time to get input stream", startGettingInputstream);
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        long startToDecodeInputStream = clock.getElapsedTimeMillis();
        bitmap = BitmapFactory.decodeStream(inputStream);
       // clock.logMessageElapsedTimeMillis("doInBackground:dtime to decode bitmap", startToDecodeInputStream);
        if (bitmaps != null) {
            bitmaps.put(urls[0], bitmap);
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
       clock.logMessageElapsedTimeMillis("onPostExecute:time to deliver bitmap to UI");
        mImageView.setImageBitmap(bitmap);
    }
}
