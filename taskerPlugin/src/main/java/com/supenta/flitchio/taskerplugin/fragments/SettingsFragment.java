package com.supenta.flitchio.taskerplugin.fragments;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.supenta.flitchio.taskerplugin.R;

import timber.log.Timber;

public class SettingsFragment extends PreferenceFragment {

    public static final String PREF_TOGGLE_WITH_FLITCHIO = "PREF_TOGGLE_WITH_FLITCHIO";

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.v("onCreate");

        addPreferencesFromResource(R.xml.settings);
    }
}
