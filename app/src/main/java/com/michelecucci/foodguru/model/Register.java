package com.michelecucci.foodguru.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Register {

	@SerializedName("id")
	@Expose
	private String id;
	@SerializedName("username")
	@Expose
	private final String username;
	@SerializedName("email")
	@Expose
	private final String email;
	@SerializedName("password")
	@Expose
	private final String password;
	@SerializedName("createdAt")
	@Expose
	private String createdAt;
	@SerializedName("updatedAt")
	@Expose
	private String updatedAt;

	public Register(String username, String email, String password){
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}

