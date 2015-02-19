package com.example.android.wearable.synchronizednotifications;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Top-level {@link dagger.Module} for Synchronized notification app.
 */
@Module(
        includes = {
                WearModule.class
        },
        injects = {
                DismissListener.class,
                MainActivity.class,
        }
)
public class SynchronizedNotificationModule {

    private final SynchronizedNotificationApplication mApplication;

    public SynchronizedNotificationModule(SynchronizedNotificationApplication application) {
        mApplication = application;
    }

    @Provides @Singleton
    public Application providesApplication() {
        return mApplication;
    }
}
