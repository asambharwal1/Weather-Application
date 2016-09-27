package com.example.aashishssg.myapplication;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.view.View;
import android.widget.CheckBox;

import static com.example.aashishssg.myapplication.R.xml.preferences;

/**
 * Created by Aashish SSG on 4/23/2016.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //noinspection deprecation
        addPreferencesFromResource(preferences);
        //noinspection deprecation
        EditTextPreference location = (EditTextPreference)findPreference("location");
        //noinspection deprecation
        final SwitchPreference imperial = (SwitchPreference) findPreference("imperial");
        //imperial.setOnPreferenceChangeListener(this);
        //noinspection deprecation
        final SwitchPreference metric = (SwitchPreference) findPreference("metric");
        //metric.setOnPreferenceChangeListener(this);
        imperial.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                boolean switchedOn = (Boolean)newValue;
                if (switchedOn) {

                    //category.addPreference(prefB);

                    //metric.setEnabled(false);
                    metric.setChecked(false);

                } else {

                    //prefB = findPreference("preference_B");
                    //category.removePreference(prefB);
                    metric.setEnabled(true);
                    metric.setChecked(true);
                }

                return true;
            }
        });

        metric.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                boolean switchedOn = (Boolean)newValue;
                if (switchedOn) {

                    //category.addPreference(prefB);

                   // imperial.setEnabled(false);
                    imperial.setChecked(false);

                } else {

                    //prefB = findPreference("preference_B");
                    //category.removePreference(prefB);
                    imperial.setEnabled(true);
                    imperial.setChecked(true);
                }

                return true;
            }
        });

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }
}
