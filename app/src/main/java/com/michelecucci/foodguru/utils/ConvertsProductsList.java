package com.michelecucci.foodguru.utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.michelecucci.foodguru.room.ProductExpirationDates;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ConvertsProductsList {

	@TypeConverter
	public List<ProductExpirationDates> stringToProductsList(String data) {
		if (data == null) {
			return Collections.emptyList();
		}
		Type listType = new TypeToken<List<ProductExpirationDates>>() {
		}.getType();
		return new Gson().fromJson(data, listType);
	}

	@TypeConverter
	public String ProductsListToString(List<ProductExpirationDates> products) {
		Gson gson = new Gson();
		return gson.toJson(products);
	}
}
