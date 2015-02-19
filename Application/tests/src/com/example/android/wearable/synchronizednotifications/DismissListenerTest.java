package com.example.android.wearable.synchronizednotifications;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;

import com.example.android.wearable.synchronizednotifications.common.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.net.Uri;

import java.util.Iterator;

import testability.DaggerInjector;
import testability.android.support.v4.app.NotificationManagerCompatWrapper;

import static com.google.android.gms.wearable.PutDataRequest.WEAR_URI_SCHEME;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for {@link com.example.android.wearable.synchronizednotifications.DismissListener}.
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class DismissListenerTest {

    private static final Uri URI = new Uri.Builder().scheme(WEAR_URI_SCHEME)
            .path(Constants.BOTH_PATH).build();
    private static final Status STATUS_SUCCESS = new Status(0);

    @Mock private GoogleApiClient mockGoogleApiClient;
    @Mock private DataApi mockDataApi;
    @Mock private NotificationManagerCompatWrapper mockNotificationManagerCompatWrapper;
    @Mock private PendingResult<DataApi.DeleteDataItemsResult> mockPendingDeleteDataItemsResult;
    @Mock private DataApi.DeleteDataItemsResult mockDeleteDataItemsResult;
    @Mock private DataEventBuffer mockDataEventBuffer;
    @Mock private Iterator<DataEvent> mockDataEventIterator;
    @Mock private DataEvent mockDataEvent;
    @Mock private DataItem mockDataItem;

    @Captor
    private ArgumentCaptor<ResultCallback<DataApi.DeleteDataItemsResult>>
            mResultCallbackArgumentCaptor;

    @InjectMocks private DismissListener mDismissListener;

    @Before
    public void setUp() {
        // Disable Dagger injection by passing null for the DaggerInjector.
        DaggerInjector.init(null);
        initMocks(this);

        when(mockDataApi.deleteDataItems(mockGoogleApiClient, URI))
                .thenReturn(mockPendingDeleteDataItemsResult);
        when(mockDeleteDataItemsResult.getStatus()).thenReturn(STATUS_SUCCESS);
        when(mockDataEventBuffer.iterator()).thenReturn(mockDataEventIterator);
        when(mockDataEventIterator.hasNext()).thenReturn(true, false);
        when(mockDataEventIterator.next()).thenReturn(mockDataEvent);
        when(mockDataEvent.getType()).thenReturn(DataEvent.TYPE_DELETED);
        when(mockDataEvent.getDataItem()).thenReturn(mockDataItem);
        when(mockDataItem.getUri()).thenReturn(URI);
        when(mockNotificationManagerCompatWrapper.from(mDismissListener))
                .thenReturn(mockNotificationManagerCompatWrapper);
    }

    @Test
    public void testOnCreate() {
        mDismissListener.onCreate();

        verify(mockGoogleApiClient).registerConnectionCallbacks(mDismissListener);
        verify(mockGoogleApiClient).registerConnectionFailedListener(mDismissListener);
    }

    @Test
    public void testOnDataChanged() {
        mDismissListener.onDataChanged(mockDataEventBuffer);

        verify(mockNotificationManagerCompatWrapper).from(mDismissListener);
        verify(mockNotificationManagerCompatWrapper).cancel(Constants.BOTH_ID);
    }

    @Test
    public void testOnConnected() {
        mDismissListener.onConnected(null);

        verify(mockDataApi).deleteDataItems(mockGoogleApiClient, URI);
        verify(mockPendingDeleteDataItemsResult)
                .setResultCallback(mResultCallbackArgumentCaptor.capture());
        mResultCallbackArgumentCaptor.getValue().onResult(mockDeleteDataItemsResult);
        verify(mockGoogleApiClient).disconnect();
    }
}
