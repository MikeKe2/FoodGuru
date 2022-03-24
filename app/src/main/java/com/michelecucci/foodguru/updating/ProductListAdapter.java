package com.michelecucci.foodguru.updating;

import static com.michelecucci.foodguru.Constants.SESSION_TOKEN;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.michelecucci.foodguru.R;
import com.michelecucci.foodguru.model.ProductRating;
import com.michelecucci.foodguru.remote.ApiUtils;
import com.michelecucci.foodguru.remote.UserInterface;
import com.michelecucci.foodguru.room.Product;
import com.michelecucci.foodguru.room.ProductDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {
	private final Context context;
	private final ArrayList<Product> productArrayList;
	private final FragmentActivity fragmentActivity;
	private final Executor myExecutor = Executors.newSingleThreadExecutor();
	private final String token;
	private Date expireDate, acquiredDate;


	public ProductListAdapter(Context context, FragmentActivity fragmentActivity, ArrayList<Product> productArrayList, String token) {
		this.context = context;
		this.fragmentActivity = fragmentActivity;
		this.productArrayList = productArrayList;
		this.token = token;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.list_of_products_card, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

		holder.txName.setText(this.productArrayList.get(position).name);
		holder.txDescription.setText(this.productArrayList.get(position).description);

		if (productArrayList.get(position).image.isEmpty()) {
			holder.imageView.setImageResource(R.drawable.ic_baseline_image_not_supported_24);
		} else {
			Picasso.get()
					.load(Uri.parse(productArrayList.get(position).image))
					.placeholder(R.drawable.ic_baseline_food_bank_24)
					.into(holder.imageView);
		}
		holder.btnAdd.setOnClickListener(view -> {

			ProductDatabase productDatabase = ProductDatabase.getInstance(context);
			Product product = new Product(this.productArrayList.get(position).id,
					this.productArrayList.get(position).barcode,
					this.productArrayList.get(position).name,
					this.productArrayList.get(position).description,
					null, false);
			myExecutor.execute(() -> productDatabase.getProductDao().insert(product));

			acquiredDate = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(acquiredDate);
			c.add(Calendar.DAY_OF_MONTH, 7);
			expireDate = c.getTime();
			product.addDates(1, acquiredDate, expireDate);

			myExecutor.execute(() -> {

				productDatabase.getProductDao().update(product);

				//POST PRODUCT PREFERENCE
				String sessionToken = fragmentActivity.getSharedPreferences(fragmentActivity.getString(R.string.session_key), Context.MODE_PRIVATE).getString(SESSION_TOKEN, null);
				UserInterface userInterface = ApiUtils.getUserServiceWithAccessToken(token);
				final ProductRating productRating = new ProductRating(sessionToken, 1, product.id);
				Call<ProductRating> call = userInterface.setProductPreference(productRating);
				call.enqueue(new Callback<ProductRating>() {
					@Override
					public void onResponse(Call<ProductRating> call, Response<ProductRating> response) {
						if (response.isSuccessful())
							Log.d("RATING RESPONSE", response.toString());
						else {
							try {
								JSONObject jObjError = new JSONObject(response.errorBody().string());
								Log.d("onResponse: ", jObjError.getJSONObject("error").getString("message"));
							} catch (Exception e) {
								Log.d("onResponse: ", e.getMessage());
							}
						}
					}

					@Override
					public void onFailure(Call<ProductRating> call, Throwable t) {
						Log.d("RATING ERROR", t.toString());
					}
				});
			});

			holder.btnAdd.setClickable(false);
			holder.btnAdd.setVisibility(View.INVISIBLE);
			Snackbar.make(view, R.string.product_added_to_DB, Snackbar.LENGTH_SHORT).show();
		});

	}

	@Override
	public int getItemCount() {
		return productArrayList.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final TextView txName;
		private final TextView txDescription;
		private final ImageView imageView;
		private final ImageButton btnAdd;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			imageView = itemView.findViewById(R.id.product_list_pantry_image);
			txName = itemView.findViewById(R.id.product_list_pantry_name);
			txDescription = itemView.findViewById(R.id.product_list_pantry_description);
			btnAdd = itemView.findViewById(R.id.product_list_add_btn);
		}
	}
}
