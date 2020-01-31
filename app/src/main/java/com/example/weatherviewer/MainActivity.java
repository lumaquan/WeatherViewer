package com.example.weatherviewer;

import android.Manifest;
import android.content.pm.PackageManager;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherviewer.Utils.OpenWeatherMapUtils;
import com.example.weatherviewer.open_weather_map.GetWeatherTask;
import com.example.weatherviewer.open_weather_map.OpenWeatherMapApi;
import com.example.weatherviewer.open_weather_map.Weather;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements OpenWeatherMapApi.Weather16DaysCallback {
    private List<Weather> weatherList = new ArrayList<>();
    private WeatherArrayAdapter weatherArrayAdapter;
    private WeatherRecyclerViewAdapter weatherRecyclerViewAdapter;
    private ListView weatherForecast;
    private RecyclerView weatherForecastRecyclerView;
    private CoordinatorLayout coordinatorLayout;
    private CheckBox retrofitCheckBox;
    private CheckBox cacheCheckBox;
    private CheckBox glideCheckBox;
    private CheckBox poolCheckBox;
    private boolean useRecyclerView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialSetup();
    }

    private void initialSetup() {
        weatherForecast = findViewById(R.id.weatherForecast);
        weatherForecastRecyclerView = findViewById(R.id.weatherForecastRV);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        retrofitCheckBox = findViewById(R.id.retrofit_checkbox);
        cacheCheckBox = findViewById(R.id.cache_checkbox);
        glideCheckBox = findViewById(R.id.glide_checkbox);
        poolCheckBox = findViewById(R.id.pool_checkbox);

        if (useRecyclerView) {
            weatherForecast.setVisibility(View.GONE);
            weatherForecastRecyclerView.setVisibility(View.VISIBLE);
            weatherRecyclerViewAdapter = new WeatherRecyclerViewAdapter(weatherList);
            weatherForecastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            weatherForecastRecyclerView.setAdapter(weatherRecyclerViewAdapter);
        } else {
            weatherForecastRecyclerView.setVisibility(View.GONE);
            weatherForecast.setVisibility(View.VISIBLE);
            weatherArrayAdapter = new WeatherArrayAdapter(this, weatherList);
            weatherForecast.setAdapter(weatherArrayAdapter);
        }
        FloatingActionButton fectchWeatherForcaset = findViewById(R.id.fab);
        fectchWeatherForcaset.setOnClickListener(fetchWeatherListener);

    }

    private View.OnClickListener fetchWeatherListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText locationEditText = findViewById(R.id.locationEditText);
            String cityNotEncoded = locationEditText.getText().toString();
            if (!TextUtils.isEmpty(cityNotEncoded)) {
                URL url = OpenWeatherMapUtils.createURL(MainActivity.this, cityNotEncoded);
                if (url != null) {
                    // Clear list of weather conditions and refect that into the view
                    weatherList.clear();
                    if (useRecyclerView) {
                        weatherRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        weatherArrayAdapter.notifyDataSetChanged();
                    }
                    // Use retrofit or not to start download of forecast data
                    if (retrofitCheckBox.isChecked()) {
                        OpenWeatherMapApi.useRetrofit(cityNotEncoded, MainActivity.this);
                    } else {
                        new GetWeatherTask(MainActivity.this).execute(url);
                    }
                }
            } else {
                Snackbar.make(coordinatorLayout, R.string.invalid_url, Snackbar.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void getWeather(List<Weather> forecast) {
        updateUI(forecast);
    }

    @Override
    public void error(String error) {
        Snackbar.make(coordinatorLayout, R.string.connect_error, Snackbar.LENGTH_LONG).show();
    }

    private void updateUI(List<Weather> weather) {
        weatherList.addAll(weather);

        if (useRecyclerView) {
            weatherRecyclerViewAdapter.cleanCache();
            weatherRecyclerViewAdapter.setUseCache(cacheCheckBox.isChecked());
            weatherRecyclerViewAdapter.setUseGlide(glideCheckBox.isChecked());
            weatherRecyclerViewAdapter.setUsePool(poolCheckBox.isChecked());

            weatherRecyclerViewAdapter.restartClock();
            weatherRecyclerViewAdapter.notifyDataSetChanged();
            weatherForecastRecyclerView.scrollToPosition(0);
        } else {
            weatherArrayAdapter.cleanCache();
            weatherArrayAdapter.setUseCache(cacheCheckBox.isChecked());
            weatherArrayAdapter.setUseGlide(glideCheckBox.isChecked());
            weatherArrayAdapter.setUsePool(poolCheckBox.isChecked());
            weatherArrayAdapter.restartClock();
            weatherArrayAdapter.notifyDataSetChanged();
            weatherForecast.smoothScrollToPosition(0);
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////


    // EXPERIMENTING

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
