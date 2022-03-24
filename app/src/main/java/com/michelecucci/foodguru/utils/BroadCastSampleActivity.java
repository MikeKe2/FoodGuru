package com.michelecucci.foodguru.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

public class BroadCastSampleActivity extends Activity {
	private final BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();

			if (currentNetworkInfo.isConnected()) {
				Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_LONG).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.registerReceiver(this.mConnReceiver,
				new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}
}
