package com.example.weatherviewer.threading;

import java.security.SecureRandom;

class PrintTask implements Runnable {

    private final String taskName;
    private final int sleepTime;
    private static final SecureRandom generator = new SecureRandom();

    public PrintTask(String taskName) {
        this.taskName = taskName;
        sleepTime = 100;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                System.out.printf("%s on thread %s doing task %d\n", taskName, Thread.currentThread().getName(), i);
                Thread.sleep(sleepTime);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    static class DaughterPrintTask extends PrintTask {

        public DaughterPrintTask(String taskName) {
            super(taskName + " daughter");
        }
    }

    static class SonPrintTask extends PrintTask {

        public SonPrintTask(String taskName) {
            super(taskName + " son");
        }
    }

}


