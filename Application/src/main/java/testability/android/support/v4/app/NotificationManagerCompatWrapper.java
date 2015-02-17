package testability.android.support.v4.app;

import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;

import javax.inject.Inject;

/**
 * Wrapper of static methods of {@link android.support.v4.app.NotificationCompat}.
 */
public class NotificationManagerCompatWrapper {

    private NotificationManagerCompat mDelegate = null;

    @Inject
    public NotificationManagerCompatWrapper() {
    }

    public NotificationManagerCompatWrapper from(Context context) {
        mDelegate = NotificationManagerCompat.from(context);
        return this;
    }

    public void cancel(int id) {
        mDelegate.cancel(id);
    }
}
