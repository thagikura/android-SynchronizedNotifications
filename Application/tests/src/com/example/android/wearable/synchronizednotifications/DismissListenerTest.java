package com.example.android.wearable.synchronizednotifications;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import testability.DaggerInjector;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for {@link com.example.android.wearable.synchronizednotifications.DismissListener}.
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class DismissListenerTest {

    @Mock private GoogleApiClient mockGoogleApiClient;
    @Mock private DataApi mockDataApi;

    @InjectMocks DismissListener mDismissListener;

    @Before
    public void setUp() {
        // Disable Dagger injection for unit testing.
        DaggerInjector.init(null);
        initMocks(this);
    }

    @Test
    public void testOnCreate() {
        mDismissListener.onCreate();

        verify(mockGoogleApiClient).registerConnectionCallbacks(mDismissListener);
        verify(mockGoogleApiClient).registerConnectionFailedListener(mDismissListener);
    }
}
