package com.michelecucci.foodguru.notifications;

import static com.michelecucci.foodguru.Constants.CHANNEL_OLD_PRODUCT_ID;
import static com.michelecucci.foodguru.Constants.GROUP_KEY_OLD_PRODUCT;
import static com.michelecucci.foodguru.Constants.NOTIFICATION_ID_OLD_PRODUCT;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.michelecucci.foodguru.R;
import com.michelecucci.foodguru.login.LoginActivity;
import com.michelecucci.foodguru.room.Product;
import com.michelecucci.foodguru.room.ProductDatabase;
import com.michelecucci.foodguru.room.ProductExpirationDates;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotificationOldWorker extends Worker {

	public NotificationOldWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
		super(context, workerParams);
	}

	@NonNull
	@Override
	public Result doWork() {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		int thresholdDay = Integer.parseInt(prefs.getString(getApplicationContext().
				getString(R.string.expire_time_tag), "0"));

		Date oldDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(oldDate);
		c.add(Calendar.DATE, Math.min(thresholdDay, 0));
		oldDate = c.getTime();

		ProductDatabase productDatabase = ProductDatabase.getInstance(getApplicationContext());
		ArrayList<Product> productLiveData = (ArrayList<Product>) productDatabase.getProductDao().getAll();
		for (Product product : productLiveData) {
			for (ProductExpirationDates expirationDates : product.expirationDates) {
				if (expirationDates.acquiredDate.before(oldDate))
					triggerNotification(product);
				break;
			}
		}

		return Result.success();

	}

	private void triggerNotification(Product product) {

		// Create an explicit intent for an Activity in your app
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_OLD_PRODUCT_ID)
				.setSmallIcon(R.drawable.ic_baseline_emoji_food_beverage_24)
				.setContentTitle("Product is old")
				.setContentText(product.name + "'s acquired date is older than the threshold")
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)        // Set the intent that will fire when the user taps the notification
				.setContentIntent(pendingIntent)
				.setGroup(GROUP_KEY_OLD_PRODUCT)
				.setAutoCancel(true);
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

		// notificationId is a unique int for each notification that you must define
		notificationManager.notify(NOTIFICATION_ID_OLD_PRODUCT, builder.build());
	}
}
