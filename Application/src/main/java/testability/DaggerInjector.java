package testability;

import javax.annotation.Nullable;

import dagger.ObjectGraph;

/**
 * Dependency injector using dagger.
 */
public class DaggerInjector {

    private static final String TAG = DaggerInjector.class.getSimpleName();

    @Nullable
    private static volatile ObjectGraph mObjectGraph;

    /**
     * Initializes the ObjectGraph for Dagger injection.
     * Pass null for unit testing.
     * @param module
     */
    public static void init(Object module) {
        mObjectGraph = module != null ? ObjectGraph.create(module) : null;
    }

    public static void inject(Object instance) {
        if (mObjectGraph == null) {
            return;
        }
        mObjectGraph.inject(instance);
    }
}