package com.clinic.myclinic.Activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.clinic.myclinic.R;

public class PreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
}
