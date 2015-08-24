package com.supenta.flitchio.taskerplugin.utils;

import android.content.Intent;
import android.os.Bundle;

import timber.log.Timber;

/**
 * Helper class to scrub Bundles of invalid extras. This is a workaround for an Android bug:
 * http://code.google.com/p/android/issues/detail?id=16006 .
 */
public final class BundleScrubber {

    /**
     * Scrubs Intents for private serializable subclasses in the Intent extras. If the Intent's
     * extras contain a private serializable subclass, the Bundle is cleared. The Bundle will not be
     * set to null. If the Bundle is null, has no extras, or the extras do not contain a private
     * serializable subclass, the Bundle is not mutated.
     *
     * @param intent {@code Intent} to scrub. This parameter may be mutated if scrubbing is necessary.
     *               This parameter may be null.
     */
    public static void scrub(final Intent intent) {
        if (intent == null) {
            return;
        }

        Bundle carriedBundle = intent.getExtras();
        scrub(carriedBundle);
    }

    /**
     * Scrubs Bundles for private serializable subclasses in the extras. If the Bundle's extras
     * contain a private serializable subclass, the Bundle is cleared. If the Bundle is null, has no
     * extras, or the extras do not contain a private serializable subclass, the Bundle is not mutated.
     *
     * @param bundle {@code Bundle} to scrub. This parameter may be mutated if scrubbing is necessary.
     *               This parameter may be null.
     */
    public static void scrub(final Bundle bundle) {
        if (bundle == null) {
            return;
        }

        // This is a hack to work around a private serializable classloader attack
        try {
            // If a private serializable exists, this will throw an exception
            bundle.containsKey(null);
        } catch (final Exception e) {
            Timber.i("Bundle was scrubbed");

            bundle.clear();
        }
    }
}