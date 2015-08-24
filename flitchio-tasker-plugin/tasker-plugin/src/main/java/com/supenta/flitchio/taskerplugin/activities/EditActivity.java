package com.supenta.flitchio.taskerplugin.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.supenta.flitchio.taskerplugin.R;
import com.supenta.flitchio.taskerplugin.lists.ButtonListAdapter;
import com.supenta.flitchio.taskerplugin.services.FlitchioBindingService;
import com.supenta.flitchio.taskerplugin.utils.ServiceUtils;

/**
 * This Activity will be started by Tasker when the "Edit" button is pressed in the plugin interface.
 * Tasker has the ability to do this due to the intent filter assigned in the manifest.
 * <p/>
 * This is responsible for getting the user's selected button and storing it in a bundle which we send
 * to Tasker as well as a "label" for it to display in the plugin interface.
 */
public class EditActivity extends AppCompatActivity {
    /**
     * The key used to store the selected button from the user.
     */
    public static final String TASKER_BUNDLE_KEY_FLITCHIO_SELECTED_BUTTON = "TASKER_BUNDLE_KEY_FLITCHIO_SELECTED_BUTTON";

    private ButtonListAdapter listAdapter;
    private FloatingActionButton fabDone;

    private ServiceStatusReceiver serviceStatusReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listAdapter = new ButtonListAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView list = (RecyclerView) findViewById(R.id.list_buttons);
        list.setAdapter(listAdapter);
        list.setLayoutManager(layoutManager);

        fabDone = (FloatingActionButton) findViewById(R.id.fab_done);

        serviceStatusReceiver = new ServiceStatusReceiver();
    }

    /**
     * By opening this Activity we assume the user is intending to use the plugin therefore if the
     * service is not running then we want to start it in order for the user to not have to
     * explicitly start it.
     */
    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(serviceStatusReceiver, serviceStatusReceiver.getIntentFilter());

        if (!ServiceUtils.isServiceRunning(this, FlitchioBindingService.class)) {
            startService(new Intent(this, FlitchioBindingService.class));
        }
    }

    /**
     * In Tasker it doesn't make sense for an Event plugin to not have a condition set therefore we
     * do not allow the user to exit without having selected a button.
     */
    @Override
    public void onBackPressed() {
        if (!listAdapter.hasAtLeastOneSelection()) {
            Snackbar.make(fabDone, R.string.snack_please_select_button, Snackbar.LENGTH_SHORT).show();
            return;
        }

        super.onBackPressed();
    }
    @SuppressWarnings("UnusedParameters")
    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(serviceStatusReceiver);

        super.onPause();
    }

    /**
     * This must only trigger when the user has selected a button. We send to Tasker the selected
     * button by wrapping it in a bundle as well as the label to be displayed in the UI.
     */
    @Override
    public void finish() {
        Bundle selectedButtonBundle = new Bundle();
        selectedButtonBundle.putString(TASKER_BUNDLE_KEY_FLITCHIO_SELECTED_BUTTON, listAdapter.getSelectedButtonId());

        Intent selectedButtonIntent = new Intent();
        selectedButtonIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, selectedButtonBundle);
        selectedButtonIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB,
                getString(listAdapter.getSelectedButtonLabelId()));

        setResult(RESULT_OK, selectedButtonIntent);

        super.finish();
    }

    /**
     * This is responsible for providing feedback to the user when the {@link FlitchioBindingService}
     * connects or disconnects.
     */
    private class ServiceStatusReceiver extends BroadcastReceiver {

        private IntentFilter getIntentFilter() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(FlitchioBindingService.ACTION_SERVICE_STARTED);
            filter.addAction(FlitchioBindingService.ACTION_SERVICE_STOPPED);

            return filter;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(FlitchioBindingService.ACTION_SERVICE_STARTED)) {
                Snackbar.make(fabDone, R.string.snack_connection_started, Snackbar.LENGTH_LONG).show();
            } else if (action.equals(FlitchioBindingService.ACTION_SERVICE_STOPPED)) {
                Snackbar.make(fabDone, R.string.snack_connection_stopped, Snackbar.LENGTH_LONG).show();
            }
        }
    }
}