package com.michelecucci.foodguru.homeScreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.michelecucci.foodguru.R;
import com.michelecucci.foodguru.room.Product;
import com.michelecucci.foodguru.room.ProductDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

	private final Executor myExecutor = Executors.newSingleThreadExecutor();
	protected Context context;
	protected ArrayList<Product> products;

	public ProductsAdapter(Context context, ArrayList<Product> products) {
		this.context = context;
		this.products = products;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.product_card, parent, false);
		return new ViewHolder(view);
	}

	public void setProducts(ArrayList<Product> products) {
		this.products = products;
		notifyDataSetChanged();
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.txName.setText(products.get(position).name);
		holder.txDescription.setText(products.get(position).description);
		/*color return to gray after clicking liked*/

		holder.btnPref.setColorFilter(products.get(position).liked ? Color.RED : Color.DKGRAY);

		if (products.get(position).image.isEmpty()) {
			holder.imageView.setImageResource(R.drawable.ic_baseline_image_not_supported_24);
		} else {
			Picasso.get()
					.load(Uri.parse(products.get(position).image))
					.placeholder(R.drawable.ic_baseline_food_bank_24)
					.into(holder.imageView);
		}

		holder.view.setOnClickListener(view -> {
			Product product = products.get(position);
			Intent intent = new Intent(context, FullProductCard.class);
			ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder.imageView, ViewCompat.getTransitionName(holder.imageView));
			intent.putExtra("selectedProduct", product);
			context.startActivity(intent, options.toBundle());
		});

		holder.btnPref.setOnClickListener(view -> {
			ProductDatabase productDatabase = ProductDatabase.getInstance(context);
			Product product = products.get(position);
			if (product.liked) {
				product.liked = false;
				holder.btnPref.setColorFilter(Color.DKGRAY);
			} else {
				product.liked = true;
				holder.btnPref.setColorFilter(Color.RED);
			}
			myExecutor.execute(() -> productDatabase.getProductDao().update(product));
		});
	}

	@Override
	public int getItemCount() {
		return this.products.size();
	}

	public Product getProduct(int position) {
		return products.get(position);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final TextView txName, txDescription;
		private final ImageView imageView;
		private final ImageButton btnPref;
		private final View view;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			this.txName = itemView.findViewById(R.id.product_name);
			this.txDescription = itemView.findViewById(R.id.product_description);
			this.imageView = itemView.findViewById(R.id.product_image);
			this.btnPref = itemView.findViewById(R.id.favorite_btn);
			this.view = itemView;

		}
	}
}
