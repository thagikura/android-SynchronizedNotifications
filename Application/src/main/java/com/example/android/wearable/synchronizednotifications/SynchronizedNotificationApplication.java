package com.example.android.wearable.synchronizednotifications;

import testability.DaggerInjector;

import android.app.Application;

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
