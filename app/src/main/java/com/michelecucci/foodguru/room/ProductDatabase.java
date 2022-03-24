package com.michelecucci.foodguru.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(version = 1, entities = {Product.class}, exportSchema = false)
public abstract class ProductDatabase extends RoomDatabase {

	private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
		@Override
		public void onCreate(@NonNull SupportSQLiteDatabase db) {
			super.onCreate(db);
		}
	};

	private static ProductDatabase database;

	public static synchronized ProductDatabase getInstance(final Context context) {
		if (database == null) {
			database = Room.databaseBuilder(context.getApplicationContext(),
					ProductDatabase.class, "Product DB")
					.fallbackToDestructiveMigration()
					.addCallback(sRoomDatabaseCallback)
					.build();
		}
		return database;
	}

	abstract public ProductDao getProductDao();
}
