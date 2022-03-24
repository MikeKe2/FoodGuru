package com.michelecucci.foodguru.remote;

import com.michelecucci.foodguru.model.ListOfProducts;
import com.michelecucci.foodguru.model.Login;
import com.michelecucci.foodguru.model.Pantry;
import com.michelecucci.foodguru.model.ProductPost;
import com.michelecucci.foodguru.model.ProductRating;
import com.michelecucci.foodguru.model.Register;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserInterface {
	@Headers({
			"Accept: application/json",
			"Content-type: application/json"
	})

	@POST("users")
	Call<Register> register(@Body Register register);

	@POST("auth/login")
	Call<Login> login(@Body Login login);

	@GET("products")
	Call<ListOfProducts> getProducts(@Query("barcode") String barcode);

	@POST("products")
	Call<Pantry> setProductDetails(@Body ProductPost productPost);

	@POST("votes")
	Call<ProductRating> setProductPreference(@Body ProductRating productRating);

}
