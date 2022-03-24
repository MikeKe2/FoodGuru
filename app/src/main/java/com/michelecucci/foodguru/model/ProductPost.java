package com.michelecucci.foodguru.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductPost {
	@SerializedName("token")
	private final String sessionToken;
	@SerializedName("name")
	@Expose
	private final String name;
	@SerializedName("description")
	@Expose
	private final String description;
	@SerializedName("barcode")
	@Expose
	private final String barcode;
	@SerializedName("test")
	@Expose
	private final boolean test;

	public ProductPost(String sessionToken, String name, String description, String barcode, boolean test) {
		this.sessionToken = sessionToken;
		this.name = name;
		this.description = description;
		this.barcode = barcode;
		this.test = test;
	}
}
