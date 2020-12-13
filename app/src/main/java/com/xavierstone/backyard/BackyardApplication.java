package com.xavierstone.backyard;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import com.xavierstone.backyard.db.DBHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackyardApplication extends Application {
    // Create a pool of four threads
    static ExecutorService executorService = Executors.newFixedThreadPool(4);
    static Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

    static DBHandler dbHandler = new DBHandler();

    public static DBHandler getDB() { return dbHandler; }
    public static Handler getThreadHandler() { return mainThreadHandler; }
    public static ExecutorService getExecutorService() { return executorService; }
}
