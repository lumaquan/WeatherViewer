package com.example.weatherviewer.threading;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

class SerialExecutor implements Executor {

    private Queue<Runnable> tasks = new ArrayDeque<>();
    private Executor executor;
    Runnable activeTask;

    SerialExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public synchronized void execute(Runnable task) {
        tasks.add(() -> {
            try {
                task.run();
            } finally {
                scheduleNext();
            }
        });
        if (activeTask == null) {
            scheduleNext();
        }
    }

    synchronized void scheduleNext() {
        if ((activeTask = tasks.poll()) != null) {
            executor.execute(activeTask);
        }
    }
}
