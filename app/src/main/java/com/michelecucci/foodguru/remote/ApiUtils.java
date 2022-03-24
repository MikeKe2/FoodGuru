package com.michelecucci.foodguru.remote;

public class ApiUtils {
	public static final String BASE_URL = "https://lam21.iot-prism-lab.cs.unibo.it/";


	public static UserInterface getUserService(){
		return RetrofitClient.getClient(BASE_URL).create(UserInterface.class);
	}

	public static UserInterface getUserServiceWithAccessToken(String accessToken){
		return RetrofitClient.getAccessTokenClient(BASE_URL, accessToken).create(UserInterface.class);
	}
}
