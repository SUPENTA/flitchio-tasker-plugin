package com.supenta.flitchio.taskerplugin.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.supenta.flitchio.taskerplugin.R;
import com.supenta.flitchio.taskerplugin.fragments.SettingsFragment;

import timber.log.Timber;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.v("onCreate");

        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, SettingsFragment.newInstance())
                .commit();
    }
}
