package com.michelecucci.foodguru.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pantry {
	@SerializedName("id")
	@Expose
	private String id;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("description")
	@Expose
	private String description;
	@SerializedName("barcode")
	@Expose
	private String barcode;
	@SerializedName("userId")
	@Expose
	private String userId;
	@SerializedName("test")
	@Expose
	private Boolean test;
	@SerializedName("createdAt")
	@Expose
	private String createdAt;
	@SerializedName("updatedAt")
	@Expose
	private String updatedAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
}