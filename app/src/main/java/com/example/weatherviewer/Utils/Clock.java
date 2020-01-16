package com.example.weatherviewer.Utils;

import android.os.SystemClock;

public class Clock {

    private long mStartTime = 0;
    private boolean started = false;

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

    public String messageElapsedTimeMillis(String message){
        return message + getElapsedTimeMillis() +  " ms";
    }

    public String messageElapsedTimeMillis(String message, long newOrigin){
        return message + (getElapsedTimeMillis()- newOrigin) +  " ms";
    }


}
