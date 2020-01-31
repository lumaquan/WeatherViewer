package com.example.weatherviewer.Utils;

import android.os.SystemClock;
import android.util.Log;

public class Clock {

    private long mStartTime = 0;
    private boolean started = false;
    private String TAG = "none";

    public Clock() {
    }

    public Clock(String TAG) {
        this.TAG = TAG;
    }

    public void init() {
        mStartTime = SystemClock.elapsedRealtime();
        started = true;
    }

    public long getElapsedTimeMillis() {
        if (!started) {
            return -1;
        } else {
            return SystemClock.elapsedRealtime() - mStartTime;
        }
    }

    public String messageElapsedTimeMillis(String message) {
        return message + ": "+ getElapsedTimeMillis() + " ms";
    }

    public String messageElapsedTimeMillis(String message, long newOrigin) {
        return message + ": " + (getElapsedTimeMillis() - newOrigin) + " ms";
    }

    public void logMessageElapsedTimeMillis(String message) {
        Log.d(TAG, messageElapsedTimeMillis(message));
    }

    public void logMessageElapsedTimeMillis(String TAG, String message) {
        Log.d(TAG, messageElapsedTimeMillis(message));
    }

    public void logMessageElapsedTimeMillis(String message, long newOrigin) {
        Log.d(TAG, messageElapsedTimeMillis(message, newOrigin));
    }

    public void logMessageElapsedTimeMillis(String TAG, String message, long newOrigin) {
        Log.d(TAG, messageElapsedTimeMillis(message, newOrigin));
    }
}
