package com.michelecucci.foodguru.remote;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
	private static Retrofit retrofit = null;

	public static Retrofit getClient(String url) {

		if (retrofit == null) {
			retrofit = new Retrofit.Builder()
					.baseUrl(url)
					.addConverterFactory(GsonConverterFactory.create())
					.build();
		}
		return retrofit;
	}

	public static Retrofit getAccessTokenClient(String url, String accessToken) {
		
		OkHttpClient httpClient = new OkHttpClient.Builder()
				.addInterceptor(chain -> {
					Request request = chain.request().newBuilder().addHeader("Authorization", "Bearer " + accessToken).build();
					return chain.proceed(request);
				})
				.build();

		return retrofit = new Retrofit.Builder()
				.baseUrl(url)
				.addConverterFactory(GsonConverterFactory.create())
				.client(httpClient)
				.build();

	}
}
