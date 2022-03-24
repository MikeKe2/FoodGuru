package com.michelecucci.foodguru.homeScreen;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.michelecucci.foodguru.MainActivity;
import com.michelecucci.foodguru.R;
import com.michelecucci.foodguru.room.Product;
import com.michelecucci.foodguru.room.ProductDatabase;
import com.michelecucci.foodguru.room.ProductExpirationDates;
import com.michelecucci.foodguru.room.ProductViewModel;
import com.michelecucci.foodguru.updating.InsertProductActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FullProductCard extends AppCompatActivity {
	private final Executor myExecutor = Executors.newSingleThreadExecutor();
	private Product product;
	private FloatingActionButton floatingActionButton, editBtn, removeBtn, likedBtn;
	private Animation rotateOpen, rotateClose, fromBottom, toBottom;
	private ProductViewModel productViewModel;
	private ExpirationDatesAdapter expirationDatesAdapter;

	private boolean clicked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_product_card);

		Intent intent = getIntent();

		product = intent.getParcelableExtra("selectedProduct");
		ProductDatabase productDatabase = ProductDatabase.getInstance(this);

		TextView productBarcode = findViewById(R.id.product_barcode);
		TextView productName = findViewById(R.id.product_name);
		TextView productDescription = findViewById(R.id.product_description);
		ImageView productImage = findViewById(R.id.product_image);

		productViewModel = new ProductViewModel(getApplication());

		floatingActionButton = findViewById(R.id.floating_button);
		likedBtn = findViewById(R.id.like_product_button);
		removeBtn = findViewById(R.id.remove_product_button);
		editBtn = findViewById(R.id.edit_product_button);
		rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
		rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
		fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_botton_anim);
		toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

		productBarcode.setText("Barcode: " + product.barcode);
		productName.setText(product.name);
		productDescription.setText(product.description);

		if (product.liked) {
			likedBtn.setColorFilter(Color.RED);
		}

		if (product.image.isEmpty()) {
			productImage.setImageResource(R.drawable.ic_baseline_image_not_supported_24);
		} else {
			Picasso.get()
					.load(Uri.parse(product.image))
					.placeholder(R.drawable.ic_baseline_food_bank_24)
					.into(productImage);
		}

		floatingActionButton.setOnClickListener(view -> onFloatingButtonClicked());

		editBtn.setOnClickListener(view -> {
			Intent intent1 = new Intent(this, InsertProductActivity.class);
			intent1.putExtra("selectedProduct", product);
			startActivity(intent1);
			finish();
		});

		removeBtn.setOnClickListener(view -> {

			myExecutor.execute(() -> productViewModel.delete(product));
			Intent intent1 = new Intent(this, MainActivity.class);
			startActivity(intent1);
			finish();
		});

		likedBtn.setOnClickListener(view -> {
			if (product.liked) {
				product.liked = false;
				likedBtn.setColorFilter(Color.DKGRAY);
				Snackbar.make(view, "Product removed from your favorites", Snackbar.LENGTH_LONG).show();
			} else {
				product.liked = true;
				likedBtn.setColorFilter(Color.RED);
				Snackbar.make(view, "Product added to your favorites", Snackbar.LENGTH_LONG).show();
			}
			myExecutor.execute(() -> productDatabase.getProductDao().update(product));
		});

		RecyclerView recyclerView = findViewById(R.id.recycler_view_expirations_dates);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		expirationDatesAdapter = new ExpirationDatesAdapter(this, (ArrayList<ProductExpirationDates>) product.expirationDates);
		recyclerView.setAdapter(expirationDatesAdapter);
	}

	private void onFloatingButtonClicked() {
		setVisibility(clicked);
		setAnimation(clicked);
		setClickable(clicked);
		clicked = !clicked;
	}

	private void setClickable(boolean clicked) {
		if (!clicked) {
			editBtn.setClickable(true);
			likedBtn.setClickable(true);
			removeBtn.setClickable(true);
			editBtn.setFocusable(true);
			likedBtn.setFocusable(true);
			removeBtn.setFocusable(true);
		} else {
			editBtn.setClickable(false);
			likedBtn.setClickable(false);
			removeBtn.setClickable(false);
			editBtn.setFocusable(false);
			likedBtn.setFocusable(false);
			removeBtn.setFocusable(false);
		}

	}

	private void setAnimation(boolean clicked) {
		if (!clicked) {
			editBtn.startAnimation(fromBottom);
			likedBtn.startAnimation(fromBottom);
			removeBtn.startAnimation(fromBottom);
			floatingActionButton.startAnimation(rotateOpen);
		} else {
			editBtn.startAnimation(toBottom);
			likedBtn.startAnimation(toBottom);
			removeBtn.startAnimation(toBottom);
			floatingActionButton.startAnimation(rotateClose);
		}
	}

	private void setVisibility(boolean clicked) {
		if (!clicked) {
			editBtn.setVisibility(View.VISIBLE);
			likedBtn.setVisibility(View.VISIBLE);
			removeBtn.setVisibility(View.VISIBLE);
		} else {
			editBtn.setVisibility(View.INVISIBLE);
			likedBtn.setVisibility(View.INVISIBLE);
			removeBtn.setVisibility(View.INVISIBLE);
		}
	}

	public void removeExpirationDate(int position) {
		product.expirationDates.remove(position);
		productViewModel.update(product);
		expirationDatesAdapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {
		if (clicked) onFloatingButtonClicked();
		super.onBackPressed();
	}
}