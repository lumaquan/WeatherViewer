package com.example.weatherviewer.threading;

public class IncreaseCounterTask implements Runnable {
    private CounterNotThreadSafe counter;

    public IncreaseCounterTask(CounterNotThreadSafe counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        counter.inc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Increasing count in thread %s: %d\n", Thread.currentThread().getName(), counter.getCount());
    }

}
