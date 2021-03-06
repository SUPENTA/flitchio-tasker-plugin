package com.supenta.flitchio.taskerplugin.activities;


import android.content.Intent;
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
import com.supenta.flitchio.taskerplugin.receiver.ServiceStatusReceiverTemplate;
import com.supenta.flitchio.taskerplugin.services.FlitchioBindingService;
import com.supenta.flitchio.taskerplugin.utils.PackageUtils;
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

        Timber.v("onCreate");

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

        Timber.v("onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.menu_main_activity_bottom, actionMenuView.getMenu());
        getMenuInflater().inflate(R.menu.menu_activities_top, menu);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Timber.v("onResume");

        registerReceiver(serviceStatusReceiver, serviceStatusReceiver.getIntentFilter());

        if (ServiceUtils.isServiceRunning(this, FlitchioBindingService.class)) {
            fabStartService.hide();
        } else {
            fabStartService.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.launch_flitchio:
                Timber.d("The Flitchio Manager button was pressed");

                PackageUtils.launchFromPackageName(this, PACKAGE_NAME_FLITCHIO_MANAGER);
                return true;
            case R.id.launch_tasker:
                Timber.d("The Tasker button was pressed");

                PackageUtils.launchFromPackageName(this, PACKAGE_NAME_TASKER);
                return true;
            case R.id.settings:
                Timber.d("The Settings button was pressed");

                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            default:
                Timber.wtf("Unknown button was pressed: " + item);

                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        Timber.v("onPause");

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
        Timber.v("onClick: startBindingService");

        if (!ServiceUtils.isServiceRunning(this, FlitchioBindingService.class)) {
            startService(new Intent(this, FlitchioBindingService.class));
        }
    }

    /**
     * This is responsible for providing feedback to the user when the {@link FlitchioBindingService}
     * connects or disconnects as well as hiding and showing the
     * {@link R.id#fab_launch_binding_service}.
     */
    private class ServiceStatusReceiver extends ServiceStatusReceiverTemplate {

        /**
         * The approximate time it will take for the Snackbar to dismiss (in ms). This is used because
         * the Snackbar does not provide an onDismiss listener, see http://goo.gl/qqLEcn .
         */
        private static final int APPROXIMATE_SNACKBAR_LONG_LENGTH = 3800;

        /**
         * Using a Main Thread handler provides higher chance of smooth animations.
         */
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        protected void onServiceStarted() {
            Snackbar.make(toolbarBottom, R.string.snack_connection_started, Snackbar.LENGTH_LONG).show();
            mainThreadHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fabStartService.hide();
                }
            }, APPROXIMATE_SNACKBAR_LONG_LENGTH);
        }

        @Override
        protected void onServiceStopped() {
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
