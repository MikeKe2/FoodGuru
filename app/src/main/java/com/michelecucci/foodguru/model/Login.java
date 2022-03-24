package com.michelecucci.foodguru.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Login {
	@SerializedName("email")
	@Expose
	private String email;
	@SerializedName("password")
	@Expose
	private String password;
	@SerializedName("accessToken")
	@Expose
	private String accessToken;

	public Login(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public String getAccessToken() {
		return accessToken;
	}

}

