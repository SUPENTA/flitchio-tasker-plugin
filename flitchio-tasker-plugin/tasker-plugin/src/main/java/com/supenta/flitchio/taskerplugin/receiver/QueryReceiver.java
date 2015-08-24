package com.supenta.flitchio.taskerplugin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.supenta.flitchio.taskerplugin.activities.EditActivity;
import com.supenta.flitchio.taskerplugin.utils.BundleScrubber;
import com.supenta.flitchio.taskerplugin.utils.TaskerPlugin;

/**
 * This is the "query" BroadcastReceiver for a Locale Plug-in condition. When Tasker wants to query
 * our plugin he sends a broadcast and listens for the result of the broadcast. If the result code
 * is {@link com.twofortyfouram.locale.Intent.RESULT_CONDITION_SATISFIED} then Tasker believes that
 * the condition has been triggered and therefore executes the linked task otherwise does nothing.
 *
 * @see com.twofortyfouram.locale.Intent#ACTION_QUERY_CONDITION
 * @see com.twofortyfouram.locale.Intent#EXTRA_BUNDLE
 */
@SuppressWarnings("JavadocReference")
public final class QueryReceiver extends BroadcastReceiver {

    /**
     * We want the linked task to be triggered only if the button set from the user is the same
     * button that was sent from the
     * {@link com.supenta.flitchio.taskerplugin.services.FlitchioBindingService}
     * <p/>
     * If the receiver was triggered by any app other than tasker or if the bundles are invalid
     * then we want to cancel and return.
     *
     * @param context {@inheritDoc}.
     * @param intent  The incoming {@link com.twofortyfouram.locale.Intent#ACTION_QUERY_CONDITION} Intent.
     *                This should always contain the {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE}
     *                that was saved by {@link EditActivity} and later broadcast by Locale.
     */
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!com.twofortyfouram.locale.Intent.ACTION_QUERY_CONDITION.equals(intent.getAction())) {
            return;
        }

        BundleScrubber.scrub(intent);

        final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        BundleScrubber.scrub(bundle);

        final Bundle bundleFromSelectedButton = intent.getExtras();
        final Bundle bundleFromListener = TaskerPlugin.Event.retrievePassThroughData(intent);
        if (bundleFromSelectedButton == null || bundleFromListener == null) {
            return;
        }

        final String selectedButton
                = bundleFromSelectedButton.getString(EditActivity.TASKER_BUNDLE_KEY_FLITCHIO_SELECTED_BUTTON);
        final String buttonFromListener
                = bundleFromListener.getString(EditActivity.TASKER_BUNDLE_KEY_FLITCHIO_SELECTED_BUTTON);
        if (selectedButton == null || buttonFromListener == null) {
            return;
        }

        if (buttonFromListener.equals(selectedButton)) {
            setResultCode(com.twofortyfouram.locale.Intent.RESULT_CONDITION_SATISFIED);
        }
    }
}