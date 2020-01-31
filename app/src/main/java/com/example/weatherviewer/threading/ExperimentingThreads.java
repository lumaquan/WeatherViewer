package com.example.weatherviewer.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class ExperimentingThreads {


    public static void main(String[] args) {

        int N = 10;
        List<PrintTask> tasks = new ArrayList<>();
        List<PrintTask.DaughterPrintTask> daughterPrintTasks = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            tasks.add(new PrintTask("Task#" + i));
        }
        for (int i = 0; i < N; i++) {
            daughterPrintTasks.add(new PrintTask.DaughterPrintTask("Task#" + i));
        }

        CounterNotThreadSafe counterNotThreadSafe = new CounterNotThreadSafe();

        List<IncreaseCounterTask> counterTasks = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            counterTasks.add(new IncreaseCounterTask(counterNotThreadSafe));
        }

        ExecutorService executorService = Executors.newCachedThreadPool();
        //executeOnExecutor(executorService, tasks);
        //executorService.shutdown();

        Executor simpleExecutor = command -> command.run();
        // executeOnExecutor(simpleExecutor, tasks);

        Executor newThreadExecutor = command -> new Thread(command).start();
        Executor serialExecutor = new SerialExecutor(newThreadExecutor);
        //executeOnExecutor(newThreadExecutor, tasks);
        // executeOnExecutor(newThreadExecutor, daughterPrintTasks);

        executeOnExecutor(serialExecutor, counterTasks);
    }

    private static void executeOnExecutor(Executor executor, List<? extends Runnable> tasks) {
        tasks.forEach((task) -> executor.execute(task));
    }


}
