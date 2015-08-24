package com.supenta.flitchio.taskerplugin.activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.supenta.flitchio.taskerplugin.R;
import com.supenta.flitchio.taskerplugin.services.FlitchioBindingService;
import com.supenta.flitchio.taskerplugin.utils.ServiceUtils;

import timber.log.Timber;

/**
 * This acts as an introductory Activity in order to give the user some simple instructions as well
 * as a quick way to restart the {@link FlitchioBindingService}.
 */
public class MainActivity extends AppCompatActivity {

    private static final String PACKAGE_NAME_FLITCHIO_MANAGER = "com.supenta.flitchio.manager";
    private static final String PACKAGE_NAME_TASKER = "net.dinglisch.android.taskerm";
    private ActionMenuView actionMenuView;
    private Toolbar toolbarBottom;

    private ServiceStatusReceiver serviceStatusReceiver;

    /**
     * We don't want this to be showing when the service is already running in order to avoid
     * user confusion.
     */
    private FloatingActionButton fabStartService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.v("Created");

        setContentView(R.layout.activity_main);

        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarTop);

        toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);

        /*
         * We delegate the click actions from the {@link R.id#action_menu_view} to the default
         * onOptionsItemSelected of the Activity. This way we can have a bottom toolbar with an
         * inflated menu
         */
        actionMenuView = (ActionMenuView) toolbarBottom.findViewById(R.id.action_menu_view);
        actionMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });

        fabStartService = (FloatingActionButton) findViewById(R.id.fab_launch_binding_service);

        serviceStatusReceiver = new ServiceStatusReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        Timber.v("Options created");

        getMenuInflater().inflate(R.menu.menu_main_activity, actionMenuView.getMenu());

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Timber.v("Resumed");

        registerReceiver(serviceStatusReceiver, serviceStatusReceiver.getIntentFilter());

        if (ServiceUtils.isServiceRunning(this, FlitchioBindingService.class)) {
            fabStartService.hide();
        } else {
            fabStartService.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent launchIntent = null;
        final int id = item.getItemId();
        if (id == R.id.launch_flitchio) {
            Timber.d("The Flitchio Manager button was pressed");

            launchIntent = getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME_FLITCHIO_MANAGER);
        } else if (id == R.id.launch_tasker) {
            Timber.d("The Tasker button was pressed");

            launchIntent = getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME_TASKER);
        }

        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Timber.i("Launching: " + launchIntent);

            startActivity(launchIntent);
        }

        return true;
    }

    @Override
    public void onPause() {
        Timber.v("Paused");

        unregisterReceiver(serviceStatusReceiver);

        super.onPause();
    }

    /**
     * Even though starting a Service twice does no obvious harm, it re-allocates the memory needed
     * making the possibility of the GC kicking in during a bad time higher.
     *
     * @param view
     */
    @SuppressWarnings("UnusedParameters")
    public void startBindingService(View view) {
        Timber.v("Service start button was clicked");

        if (!ServiceUtils.isServiceRunning(this, FlitchioBindingService.class)) {
            startService(new Intent(this, FlitchioBindingService.class));
        }
    }

    /**
     * This is responsible for providing feedback to the user when the {@link FlitchioBindingService}
     * connects or disconnects as well as hiding and showing the
     * {@link R.id#fab_launch_binding_service}.
     */
    private class ServiceStatusReceiver extends BroadcastReceiver {

        /**
         * The approximate time it will take for the Snackbar to dismiss (in ms). This is used because
         * the Snackbar does not provide an onDismiss listener, see http://goo.gl/qqLEcn .
         */
        private static final int APPROXIMATE_SNACKBAR_LONG_LENGTH = 3800;

        /**
         * Using a Main Thread handler provides higher chance of smooth animations.
         */
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        private IntentFilter getIntentFilter() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(FlitchioBindingService.ACTION_SERVICE_STARTED);
            filter.addAction(FlitchioBindingService.ACTION_SERVICE_STOPPED);

            return filter;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Timber.d("Action received: " + action);

            if (action.equals(FlitchioBindingService.ACTION_SERVICE_STARTED)) {
                Snackbar.make(toolbarBottom, R.string.snack_connection_started, Snackbar.LENGTH_LONG).show();
                mainThreadHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fabStartService.hide();
                    }
                }, APPROXIMATE_SNACKBAR_LONG_LENGTH);
            } else if (action.equals(FlitchioBindingService.ACTION_SERVICE_STOPPED)) {
                Snackbar.make(toolbarBottom, R.string.snack_connection_stopped, Snackbar.LENGTH_LONG).show();
                mainThreadHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fabStartService.show();
                    }
                }, APPROXIMATE_SNACKBAR_LONG_LENGTH);
            }
        }
    }
}
