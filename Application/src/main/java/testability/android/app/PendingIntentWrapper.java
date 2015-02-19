package testability.android.app;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import javax.inject.Inject;

/**
 * A class that offers mockable version of static methods for {@link android.app.PendingIntent}.
 */
public class PendingIntentWrapper {

    @Inject
    public PendingIntentWrapper() {
    }

    public PendingIntent getService(Context context, int requestCode,
            @NonNull Intent intent, int flags) {
        return PendingIntent.getService(context, requestCode, intent, flags);
    }
}
