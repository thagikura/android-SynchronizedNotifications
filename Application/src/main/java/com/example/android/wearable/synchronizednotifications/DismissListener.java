/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.wearable.synchronizednotifications;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.WearableListenerService;

import com.example.android.wearable.synchronizednotifications.common.Constants;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

import testability.DaggerInjector;
import testability.android.support.v4.app.NotificationManagerCompatWrapper;

import static com.google.android.gms.wearable.PutDataRequest.WEAR_URI_SCHEME;

/**
 * A {@link com.google.android.gms.wearable.WearableListenerService} that is invoked when certain
 * notifications are dismissed from either the phone or watch.
 */
public class DismissListener extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "DismissListener";

    @Inject @Named("WearableApiClient") GoogleApiClient mGoogleApiClient;
    @Inject DataApi mDataApi;
    @Inject NotificationManagerCompatWrapper mNotificationManagerCompatWrapper;

    public DismissListener() {
        super();
        DaggerInjector.inject(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient.registerConnectionCallbacks(this);
        mGoogleApiClient.registerConnectionFailedListener(this);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_DELETED) {
                if (Constants.BOTH_PATH.equals(dataEvent.getDataItem().getUri().getPath())) {
                    // notification on the phone should be dismissed
                    mNotificationManagerCompatWrapper.from(this).cancel(Constants.BOTH_ID);
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            String action = intent.getAction();
            if (Constants.ACTION_DISMISS.equals(action)) {
                // We need to dismiss the wearable notification. We delete the DataItem that
                // created the notification to inform the wearable.
                int notificationId = intent.getIntExtra(Constants.KEY_NOTIFICATION_ID, -1);
                if (notificationId == Constants.BOTH_ID) {
                    dismissWearableNotification(notificationId);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Removes the DataItem that was used to create a notification on the watch. By deleting the
     * data item, a {@link com.google.android.gms.wearable.WearableListenerService} on the watch
     * will be notified and the notification on the watch will be removed. To
     * access the Wearable DataApi, we first need to ensure the GoogleApiClient is ready,
     * which will then run the onConnected callback were the data removal is
     * defined.
     *
     * @param id The ID of the notification that should be removed
     */
    private void dismissWearableNotification(final int id) {
        mGoogleApiClient.connect();
    }

    @Override // ConnectionCallbacks
    public void onConnected(Bundle bundle) {
        final Uri dataItemUri =
                new Uri.Builder().scheme(WEAR_URI_SCHEME).path(Constants.BOTH_PATH).build();
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Deleting Uri: " + dataItemUri.toString());
        }
        mDataApi.deleteDataItems(
                mGoogleApiClient, dataItemUri)
                .setResultCallback(new ResultCallback<DataApi.DeleteDataItemsResult>() {
                    @Override
                    public void onResult(DataApi.DeleteDataItemsResult deleteDataItemsResult) {
                        if (!deleteDataItemsResult.getStatus().isSuccess()) {
                            Log.e(TAG, "dismissWearableNotification(): failed to delete DataItem");
                        }
                        mGoogleApiClient.disconnect();
                    }
                });
    }

    @Override // ConnectionCallbacks
    public void onConnectionSuspended(int i) {
    }

    @Override // OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Failed to connect to the Google API client");
    }
}
