package com.michelecucci.foodguru;

import static com.michelecucci.foodguru.Constants.ACCESS_TOKEN;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.michelecucci.foodguru.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

	protected OnBackPressedListener onBackPressedListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String AccessToken = "";

		ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			AccessToken = extras.getString(ACCESS_TOKEN);
			//The key argument here must match that used in the other activity
		}

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(ACCESS_TOKEN, AccessToken);
		editor.apply();

		new AppBarConfiguration.Builder(
				R.id.navigation_home, R.id.navigation_qr, R.id.navigation_settings)
				.build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		NavigationUI.setupWithNavController(binding.navView, navController);
	}

	public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
		this.onBackPressedListener = onBackPressedListener;
	}

	@Override
	public void onBackPressed() {
		if (onBackPressedListener != null)
			onBackPressedListener.doBack();
		else
			super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		onBackPressedListener = null;
		super.onDestroy();
	}

	public interface OnBackPressedListener {
		void doBack();
	}
}