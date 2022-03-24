package com.michelecucci.foodguru.ui;

import static com.michelecucci.foodguru.Constants.PREF_PASSWORD;
import static com.michelecucci.foodguru.Constants.PREF_USERNAME;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.michelecucci.foodguru.R;
import com.michelecucci.foodguru.login.LoginActivity;

public class SettingsFragment extends PreferenceFragmentCompat {

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.root_preferences, rootKey);

		String username = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getString(PREF_USERNAME, null);

		EditTextPreference userPreference = findPreference("user");
		Preference button = findPreference("feedback");
		userPreference.setSummary(username);

		button.setOnPreferenceClickListener(preference -> {
			getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
					.edit()
					.remove(PREF_USERNAME)
					.remove(PREF_PASSWORD)
					.apply();
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
			getActivity().finish();
			return true;
		});
	}
}