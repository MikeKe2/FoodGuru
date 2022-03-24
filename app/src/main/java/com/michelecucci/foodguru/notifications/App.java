package com.michelecucci.foodguru.notifications;

import static com.michelecucci.foodguru.Constants.CHANNEL_EXPIRED_PRODUCT_ID;
import static com.michelecucci.foodguru.Constants.CHANNEL_OLD_PRODUCT_ID;
import static com.michelecucci.foodguru.Constants.EXPIRED_CHANNEL;
import static com.michelecucci.foodguru.Constants.OLD_CHANNEL;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.michelecucci.foodguru.R;

import java.util.concurrent.TimeUnit;

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		createNotificationChannel();
		PeriodicWorkRequest ExpiredRequest =
				new PeriodicWorkRequest.Builder(NotificationExpiredWorker.class, 1, TimeUnit.DAYS)
						.addTag(EXPIRED_CHANNEL)
						.build();
		WorkManager.getInstance(this).enqueueUniquePeriodicWork(EXPIRED_CHANNEL, ExistingPeriodicWorkPolicy.KEEP, ExpiredRequest);
		PeriodicWorkRequest OldRequest =
				new PeriodicWorkRequest.Builder(NotificationOldWorker.class, 1, TimeUnit.DAYS)
						.addTag(OLD_CHANNEL)
						.build();
		WorkManager.getInstance(this).enqueueUniquePeriodicWork(OLD_CHANNEL, ExistingPeriodicWorkPolicy.KEEP, OldRequest);

	}

	private void createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel_old_product = new NotificationChannel(
					CHANNEL_OLD_PRODUCT_ID,
					"channel old product",
					NotificationManager.IMPORTANCE_DEFAULT
			);
			channel_old_product.setDescription(getString(R.string.notification_channel_old_products));

			NotificationChannel channel_expired_product = new NotificationChannel(
					CHANNEL_EXPIRED_PRODUCT_ID,
					"channel expired product",
					NotificationManager.IMPORTANCE_DEFAULT
			);
			channel_expired_product.setDescription(getString(R.string.notification_channel_expired_products));

			NotificationManager manager = getSystemService(NotificationManager.class);
			manager.createNotificationChannel(channel_old_product);
			manager.createNotificationChannel(channel_expired_product);
		}

	}
}
