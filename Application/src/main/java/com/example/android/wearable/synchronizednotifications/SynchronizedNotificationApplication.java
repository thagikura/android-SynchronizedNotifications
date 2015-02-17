package com.example.android.wearable.synchronizednotifications;

import android.app.Application;

import testability.DaggerInjector;

/**
 * Entry point of the application.
 */
public class SynchronizedNotificationApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerInjector.init(new SynchronizedNotificationModule(this));
    }
}
