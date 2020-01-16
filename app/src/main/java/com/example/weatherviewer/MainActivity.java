package com.example.weatherviewer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.weatherviewer.Utils.Clock;
import com.example.weatherviewer.Utils.OpenWeatherMapUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Watchable;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private List<Weather> weatherList = new ArrayList<>();
    private WeatherArrayAdapter weatherArrayAdapter;
    private ListView weatherForecast;
    private CoordinatorLayout coordinatorLayout;
    private CheckBox checkBox;
    private Clock clock = new Clock();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialSetup();
        // experimenting();
    }


    private void initialSetup() {
        weatherForecast = findViewById(R.id.weatherForecast);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        checkBox = findViewById(R.id.checkbox);
        weatherArrayAdapter = new WeatherArrayAdapter(this, weatherList);
        weatherForecast.setAdapter(weatherArrayAdapter);
        FloatingActionButton fectchWeatherForcaset = findViewById(R.id.fab);
        fectchWeatherForcaset.setOnClickListener(fetchWeatherListener);
    }


    private View.OnClickListener fetchWeatherListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clock.init();
            weatherArrayAdapter.restartClock();

            EditText locationEditText = findViewById(R.id.locationEditText);
            String cityNotEncoded = locationEditText.getText().toString();
            if (!TextUtils.isEmpty(cityNotEncoded)) {
                URL url = OpenWeatherMapUtils.createURL(MainActivity.this, cityNotEncoded);
                if (url != null) {
                    if (checkBox.isChecked()) {
                        useRetrofit(cityNotEncoded);
                    } else {
                        new GetWeatherTask().execute(url);
                    }
                }
            } else {
                Snackbar.make(coordinatorLayout, R.string.invalid_url, Snackbar.LENGTH_LONG).show();
            }
        }
    };

    private void useRetrofit(String cityNotEncoded) {
        long startToConnect = clock.getElapsedTimeMillis();
        Call<WeatherResponse> call = ServiceManager.getOpenWeatherService()
                .forecast16days(cityNotEncoded, getString(R.string.api_key));

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.d(TAG, clock.messageElapsedTimeMillis("onResponse: ", startToConnect));
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<Weather> forecast = OpenWeatherMapUtils.extractWeatherFromRetrofitResponse(response.body());
                        updateUI(forecast);
                    } else {
                        Snackbar.make(coordinatorLayout, "Unable to get body", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(coordinatorLayout, "Connection not successful", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Snackbar.make(coordinatorLayout, R.string.connect_error, Snackbar.LENGTH_LONG).show();
                Log.d(TAG, clock.messageElapsedTimeMillis("onResponse: ", startToConnect));
            }
        });
    }


    private class GetWeatherTask extends AsyncTask<URL, Void, List<Weather>> {

        @Override
        protected List<Weather> doInBackground(URL... urls) {
            HttpURLConnection connection = null;
            String json = null;
            try {
                long startToConnect = clock.getElapsedTimeMillis();
                connection = (HttpURLConnection) urls[0].openConnection();
                int code = connection.getResponseCode();
                showDetailsConnection(connection);
                if (code == HttpURLConnection.HTTP_OK) {
                    json = getJsonStringFromInputStream(connection.getInputStream());
                    Log.d(TAG, clock.messageElapsedTimeMillis("doInBackground: time to read json: ", startToConnect));
                } else {
                    Snackbar.make(coordinatorLayout, R.string.connect_error, Snackbar.LENGTH_LONG).show();
                    clock.messageElapsedTimeMillis("doInBackground: time to fetch: ", startToConnect);
                }
            } catch (IOException e) {
                Snackbar.make(coordinatorLayout, R.string.connect_error, Snackbar.LENGTH_LONG).show();
            } finally {
                connection.disconnect();
            }
            if (json != null) {
                try {
                    long startToExtractWeather = clock.getElapsedTimeMillis();
                    List<Weather> forecast = OpenWeatherMapUtils.extractForecast(new JSONObject(json));
                    Log.d(TAG, clock.messageElapsedTimeMillis("doInBackground:time to extract forecast: ", startToExtractWeather));
                    return forecast;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
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


        @Override
        protected void onPostExecute(List<Weather> weather) {
            updateUI(weather);
        }
    }

    private void updateUI(List<Weather> weather) {
        weatherList.clear();
        weatherList.addAll(weather);
        weatherArrayAdapter.notifyDataSetChanged();
        weatherForecast.smoothScrollToPosition(0);
    }


    private void experimenting() {
        //exceptionBasics();
        //experimentingPermissionsSDK();
        //experimentingPermissionCompat();
        testingWeatherClass();
    }

    private void testingWeatherClass() {
        Weather weather = new Weather(System.currentTimeMillis() / 1000, 23, 80, 50, "Cloudy", "c23");
        String s = weather.dayOfWeek;

        long timeSinceBoot = SystemClock.elapsedRealtime();
        long timeonThread = SystemClock.currentThreadTimeMillis();
        long timeSinceBootNotSleeping = SystemClock.uptimeMillis();
        long timeMillis = System.currentTimeMillis();
        long timeNanos = System.nanoTime();

        Log.d("timingA", "testingWeatherClass: " + timeSinceBoot);
        Log.d("timingA", "testingWeatherClass: " + timeonThread);
        Log.d("timingA", "testingWeatherClass: " + timeSinceBootNotSleeping);
        Log.d("timingA", "testingWeatherClass: " + timeMillis);
        Log.d("timingA", "testingWeatherClass: " + timeNanos);

    }

    private void exceptionBasics() {
        System.out.println("Type an integer in the console: ");
        Scanner consoleScanner = new Scanner(System.in);
        try {
            System.out.println("You typed the integer value: " + consoleScanner.nextInt());
        } catch (InputMismatchException e1) {
            System.out.println(e1.getLocalizedMessage());
        } catch (NoSuchElementException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private void experimentingPermissionsSDK() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                doYourJOb();
            }
        }
    }

    private void experimentingPermissionCompat() {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            doYourJOb();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults != null && grantResults.length > 0 && requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doYourJOb();
            } else {
                doAnotherJob();
            }
        }
    }

    private void doAnotherJob() {
        Toast.makeText(this, "I am NOT writing to disk", Toast.LENGTH_SHORT).show();
    }

    private void doYourJOb() {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "mio.txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("Esto el lo que mas me preocupa".getBytes());
            Toast.makeText(this, "done!!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
