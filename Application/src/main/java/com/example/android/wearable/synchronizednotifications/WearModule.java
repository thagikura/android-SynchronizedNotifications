package com.example.android.wearable.synchronizednotifications;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.Wearable;

import android.app.Application;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * A {@link dagger.Module} for wearable specific classes.
 */
@Module(
        library = true,
        complete = false
)
public class WearModule {

    @Provides @Named("WearableApiClient")
    public GoogleApiClient providesGoogleApiClient(Application application) {
        return new GoogleApiClient.Builder(application)
                .addApi(Wearable.API)
                .build();
    }

    @Provides
    public DataApi providesDataApi() {
        return Wearable.DataApi;
    }
}
