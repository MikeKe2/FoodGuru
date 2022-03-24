package com.michelecucci.foodguru.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductRating {
	@SerializedName("token")
	@Expose
	private final String token;
	@SerializedName("rating")
	@Expose
	private final int rating;
	@SerializedName("productId")
	@Expose
	private final String productId;
	@SerializedName("id")
	@Expose
	private String id;
	@SerializedName("userId")
	@Expose
	private String userId;
	@SerializedName("createdAt")
	@Expose
	private String createdAt;
	@SerializedName("updatedAt")
	@Expose
	private String updatedAt;

	public ProductRating(String token, int rating, String productId) {
		this.token = token;
		this.rating = rating;
		this.productId = productId;
	}
}
