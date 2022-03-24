package com.michelecucci.foodguru.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListOfProducts {
	@SerializedName("products")
	@Expose
	private List<Pantry> products;
	@SerializedName("token")
	@Expose
	private String token;

	public List<Pantry> getProducts() {
		return products;
	}

	public String getToken() {
		return token;
	}
}
