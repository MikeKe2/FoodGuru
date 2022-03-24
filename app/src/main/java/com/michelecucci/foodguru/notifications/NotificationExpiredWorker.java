package com.michelecucci.foodguru.notifications;

import static com.michelecucci.foodguru.Constants.CHANNEL_EXPIRED_PRODUCT_ID;
import static com.michelecucci.foodguru.Constants.GROUP_KEY_EXPIRED_PRODUCT;
import static com.michelecucci.foodguru.Constants.NOTIFICATION_ID_EXPIRED_PRODUCT;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.michelecucci.foodguru.R;
import com.michelecucci.foodguru.login.LoginActivity;
import com.michelecucci.foodguru.room.Product;
import com.michelecucci.foodguru.room.ProductDatabase;
import com.michelecucci.foodguru.room.ProductExpirationDates;

import java.util.ArrayList;
import java.util.Date;

public class NotificationExpiredWorker extends Worker {

	public NotificationExpiredWorker(@NonNull Context context, @NonNull WorkerParameters params) {
		super(context, params);
	}

	@NonNull
	@Override
	public Result doWork() {
		Date currentDate = new Date();

		ProductDatabase productDatabase = ProductDatabase.getInstance(getApplicationContext());
		ArrayList<Product> productLiveData = (ArrayList<Product>) productDatabase.getProductDao().getAll();
		for (Product product : productLiveData) {
			for (ProductExpirationDates expirationDates : product.expirationDates) {
				if (!expirationDates.expireDate.after(currentDate))
					triggerNotification(product);
				break;
			}
		}

		return Result.success();
	}

	private void triggerNotification(Product product) {

		// Create an explicit intent for an Activity in your app
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.putExtra("selectedProduct", product);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_EXPIRED_PRODUCT_ID)
				.setSmallIcon(R.drawable.ic_baseline_emoji_food_beverage_24)
				.setContentTitle("Product expiring")
				.setContentText(product.name + " is about to expire!")
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setContentIntent(pendingIntent)
				.setGroup(GROUP_KEY_EXPIRED_PRODUCT)
				.setAutoCancel(true);
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
		notificationManager.notify(NOTIFICATION_ID_EXPIRED_PRODUCT, builder.build());
	}
}
