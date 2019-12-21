package com.github.lykmapipo.location;

import android.os.Handler;

import com.google.android.gms.common.internal.Preconditions;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Shadow for {@link Preconditions} that disables threading checks. Since Robolectric fakes various
 * threading constructs, these would otherwise cause tests to fail.
 */
@Implements(Preconditions.class)
public class ShadowPreconditions {
    @Implementation
    public static void checkNotMainThread() {
        // Do nothing
    }

    @Implementation
    public static void checkNotMainThread(String errorMessage) {
        // Do nothing
    }

    @Implementation
    public static void checkHandlerThread(Handler handler) {
        // Do nothing
    }

    @Implementation
    public static void checkHandlerThread(Handler handler, String errorMessage) {
        // Do nothing
    }
}
