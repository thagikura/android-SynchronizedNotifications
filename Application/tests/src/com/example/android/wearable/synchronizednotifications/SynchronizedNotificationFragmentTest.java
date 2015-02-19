package com.example.android.wearable.synchronizednotifications;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataRequest;

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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import testability.DaggerInjector;
import testability.android.app.PendingIntentWrapper;
import testability.android.support.v4.app.FragmentWrapper;
import testability.android.support.v4.app.NotificationManagerCompatWrapper;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for {@link SynchronizedNotificationsFragment}.
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class SynchronizedNotificationFragmentTest {

    private static final int NOTIFICATION_ID = 100;
    private static final String PHONE_TITLE = "phone_title";
    private static final String WATCH_TITLE = "watch_title";
    private static final String CONTENT = "content";
    private static final String PATH = "/test-path";
    private static final Status STATUS_SUCCESS = new Status(0);
    private static final Status STATUS_FAILURE = new Status(1);

    @Mock private GoogleApiClient mockGoogleApiClient;
    @Mock private DataApi mockDataApi;
    @Mock private MainActivity mockActivity;
    @Mock private PendingIntentWrapper mockPendingIntentWrapper;
    @Mock private PendingIntent mockPendingIntent;
    @Mock private FragmentWrapper mockFragmentWrapper;
    @Mock private NotificationManagerCompatWrapper mockNotificationManagerCompatWrapper;
    @Mock private PendingResult<DataApi.DataItemResult> mockPendingDataItemResult;
    @Mock private DataApi.DataItemResult mockDataItemResult;

    @Captor private ArgumentCaptor<Notification> mNotificationArgumentCaptor;
    @Captor private ArgumentCaptor<Intent> mIntentArgumentCaptor;
    @Captor private ArgumentCaptor<PutDataRequest> mPutDataRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<ResultCallback<DataApi.DataItemResult>>
            mResultCallbackArgumentCaptor;

    @InjectMocks private SynchronizedNotificationsFragment mFragment;

    @Before
    public void setUp() {
        // Disable Dagger injection by passing null for the DaggerInjector.
        DaggerInjector.init(null);
        initMocks(this);

        when(mockFragmentWrapper.getAttachedActivity()).thenReturn(mockActivity);
        when(mockPendingIntentWrapper
                .getService(eq(mockActivity), eq(0), isA(Intent.class),
                        eq(PendingIntent.FLAG_UPDATE_CURRENT))).thenReturn(mockPendingIntent);
        when(mockNotificationManagerCompatWrapper.from(mockActivity))
                .thenReturn(mockNotificationManagerCompatWrapper);
        when(mockGoogleApiClient.isConnected()).thenReturn(true);
        when(mockDataApi.putDataItem(eq(mockGoogleApiClient), isA(PutDataRequest.class)))
                .thenReturn(mockPendingDataItemResult);
        when(mockDataItemResult.getStatus()).thenReturn(STATUS_SUCCESS);
    }

    @Test
    public void testOnCreate() {
        mFragment.onCreate(null);

        verify(mockGoogleApiClient).registerConnectionCallbacks(mFragment);
        verify(mockGoogleApiClient).registerConnectionFailedListener(mFragment);
    }

    @Test
    public void testOnStart() {
        mFragment.onStart();

        verify(mockGoogleApiClient).connect();
    }

    @Test
    public void testLocalOnlyNotification() {
        mFragment.buildLocalOnlyNotification(PHONE_TITLE, CONTENT, NOTIFICATION_ID, true);

        verify(mockPendingIntentWrapper)
                .getService(eq(mockActivity), eq(0), mIntentArgumentCaptor.capture(),
                        eq(PendingIntent.FLAG_UPDATE_CURRENT));
        Intent captoredIntent = mIntentArgumentCaptor.getValue();
        assertEquals(Constants.ACTION_DISMISS, captoredIntent.getAction());
        assertEquals(Constants.BOTH_ID,
                captoredIntent.getExtras().getInt(Constants.KEY_NOTIFICATION_ID));

        verify(mockNotificationManagerCompatWrapper).from(mockActivity);
        verify(mockNotificationManagerCompatWrapper)
                .notify(eq(NOTIFICATION_ID), mNotificationArgumentCaptor.capture());
        Notification captoredNotification = mNotificationArgumentCaptor.getValue();
        assertEquals(R.drawable.ic_launcher, captoredNotification.icon);
        // TODO(thagikura) To verify more fields in Notification that are not exposed, need to
        // wrap a Notification or Builder
    }

    @Test
    public void testBuildWearableOnlyNotification() {
        when(mockDataItemResult.getStatus()).thenReturn(STATUS_FAILURE);
        mFragment.buildWearableOnlyNotification(WATCH_TITLE, CONTENT, PATH);

        verify(mockDataApi)
                .putDataItem(eq(mockGoogleApiClient), mPutDataRequestArgumentCaptor.capture());
        PutDataRequest captoredPutDataRequest = mPutDataRequestArgumentCaptor.getValue();
        DataMap dataMap = DataMap.fromByteArray(captoredPutDataRequest.getData());
        assertEquals(PATH, captoredPutDataRequest.getUri().getPath());
        assertEquals(CONTENT, dataMap.getString(Constants.KEY_CONTENT));
        assertEquals(WATCH_TITLE, dataMap.getString(Constants.KEY_TITLE));

        verify(mockPendingDataItemResult)
                .setResultCallback(mResultCallbackArgumentCaptor.capture());
        mResultCallbackArgumentCaptor.getValue().onResult(mockDataItemResult);
    }

    @Test
    public void testBuildMirroredNotifications() {
        // To confirm the methods in under test are invoked, spying the mFragment.
        mFragment = spy(mFragment);

        mFragment.buildMirroredNotifications(PHONE_TITLE, WATCH_TITLE, CONTENT);

        verify(mFragment).buildWearableOnlyNotification(WATCH_TITLE, CONTENT,
                Constants.BOTH_PATH);
        verify(mFragment).buildLocalOnlyNotification(PHONE_TITLE, CONTENT,
                Constants.BOTH_ID, true);
    }
}
