package testability.android.support.v4.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Wrapper class which offers mockable version of final or static methods.
 */
public class FragmentWrapper extends Fragment {

    private FragmentWrapper mDelegate = null;

    public FragmentActivity getAttachedActivity() {
        if (mDelegate != null) {
            return mDelegate.getAttachedActivity();
        } else {
            return super.getActivity();
        }
    }
}
