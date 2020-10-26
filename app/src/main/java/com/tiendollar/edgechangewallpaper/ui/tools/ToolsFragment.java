package com.tiendollar.edgechangewallpaper.ui.tools;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.tiendollar.edgechangewallpaper.R;
import com.tiendollar.edgechangewallpaper.utils.support.Constants;
import com.tiendollar.edgechangewallpaper.utils.support.TimeUtil;
import com.tiendollar.edgechangewallpaper.utils.support.WindowUtils;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.tiendollar.edgechangewallpaper.utils.support.Constants.PREF_EDGE_MODE_KEY;

public class ToolsFragment extends PreferenceFragmentCompat {

    private static final int OVERLAY_PERMISSION_CODE = 1005;
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();
        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.

            Preference frequencyPref = findPreference(Constants.PREF_AUTO_CHANGE_FREQUENCY_KEY);
            Preference customFrequencyPref = findPreference(Constants.PREF_CUSTOM_FREQUENCY_KEY);
            assert frequencyPref != null;
            if (frequencyPref.getSummary() != null && frequencyPref.getSummary().equals("Custom")) {
                assert customFrequencyPref != null;
                customFrequencyPref.setEnabled(true);
            } else {
                assert customFrequencyPref != null;
                customFrequencyPref.setEnabled(false);
            }
            findPreference(Constants.PREF_CUSTOM_FREQUENCY_KEY).setEnabled(stringValue.equals("-1"));
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);

        } else {
            preference.setSummary(stringValue);
        }
        return true;
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        bindPreferenceSummaryToValue(Objects.requireNonNull(findPreference(Constants.PREF_AUTO_CHANGE_FREQUENCY_KEY)));
        bindPreferenceSummaryToValue(Objects.requireNonNull(findPreference(Constants.PREF_AUTO_CHANGE_MODE_KEY)));
        Preference preference = findPreference(Constants.PREF_CUSTOM_FREQUENCY_KEY);
        Preference frequencyPref = findPreference(Constants.PREF_AUTO_CHANGE_FREQUENCY_KEY);
        assert frequencyPref != null;
        if (frequencyPref.getSummary().equals("Custom")) {
            assert preference != null;
            preference.setEnabled(true);
        }
        assert preference != null;
        preference.setOnPreferenceClickListener(preference1 -> {
            int time = TimeUtil.parseTime(preference1.getSummary().toString());
            TimePickerDialog.OnTimeSetListener listener = (view, hourOfDay, minute) -> {
                StringBuilder builder = new StringBuilder();
                if ((hourOfDay | minute) == 0) {
                    hourOfDay = 24;
                }
                builder.append(hourOfDay);
                builder.append(hourOfDay > 1 ? " hours " : " hour ");
                builder.append(minute);
                builder.append(minute > 1 ? " minutes" : " minute");
                preference1.setSummary(builder.toString());
                SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putString(preference1.getKey(), builder.toString()).apply();

            };
            TimePickerDialog dialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog, listener, time / 60, time % 60, true);
            dialog.setTitle("Set time");
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            return false;
        });
        bindPreferenceSummaryToValue(preference);

        Preference sharePreference = findPreference(Constants.PREF_SHARE_APP_KEY);
        assert sharePreference != null;
        sharePreference.setOnPreferenceClickListener(preference12 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            builder.setTitle(R.string.pref_title_share_app)
                    .setMessage(getString(R.string.pref_summary_share_app))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> WindowUtils.shareApp(getActivity()))
            ;
            builder.create().show();
            return false;
        });

        Preference ratePreference = findPreference(Constants.PREF_RATE_APP_KEY);
        if (ratePreference != null) {
            ratePreference.setOnPreferenceClickListener(preference13 -> {
                WindowUtils.rateApp(getActivity());
                return false;
            });
        }

        final SwitchPreference edgeMode = findPreference(PREF_EDGE_MODE_KEY);
        assert edgeMode != null;
        edgeMode.setOnPreferenceClickListener(preference14 -> {
            checkOverlayPermission(preference14);
            return false;
        });
        SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (key.equals(PREF_EDGE_MODE_KEY)) {
                boolean status = sharedPreferences.getBoolean(PREF_EDGE_MODE_KEY, false);
                edgeMode.setChecked(status);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == OVERLAY_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= 23) {
                SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.edit().putBoolean(PREF_EDGE_MODE_KEY, Settings.canDrawOverlays(getContext())).apply();
            }
        }
    }

    private void checkOverlayPermission(Preference preference) {
        if (Build.VERSION.SDK_INT >= 23) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getActivity().getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
        } else {
            ((SwitchPreference) preference).setChecked(true);
        }
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
}