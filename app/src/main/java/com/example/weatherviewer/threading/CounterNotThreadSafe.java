package com.example.weatherviewer.threading;

public class CounterNotThreadSafe {

    private int count =0;

    public int getCount() {
        return count;
    }

    public void inc(){
        count++;
    }
}
