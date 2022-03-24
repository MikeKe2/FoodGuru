package com.michelecucci.foodguru.updating;

import static com.michelecucci.foodguru.Constants.ACCESS_TOKEN;
import static com.michelecucci.foodguru.Constants.BARCODE;
import static com.michelecucci.foodguru.Constants.PRODUCT;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import com.michelecucci.foodguru.databinding.ActivityInsertProductBinding;
import com.michelecucci.foodguru.login.AuthenticationStateAdapter;
import com.michelecucci.foodguru.room.Product;


public class InsertProductActivity extends AppCompatActivity {

	private ViewPager2 viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		com.michelecucci.foodguru.databinding.ActivityInsertProductBinding binding = ActivityInsertProductBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		viewPager = binding.viewpager;

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String accessToken = sharedPreferences.getString(ACCESS_TOKEN, "null");

		Bundle bundle = new Bundle();
		bundle.putString(ACCESS_TOKEN, accessToken);
		//get barcode from activity caller
		Intent intent = getIntent();
		if (intent.getParcelableExtra("selectedProduct") != null) {
			Product product = intent.getParcelableExtra("selectedProduct");
			bundle.putParcelable(PRODUCT, intent.getParcelableExtra("selectedProduct"));
			bundle.putString(ACCESS_TOKEN, accessToken);
			bundle.putString(BARCODE, product.barcode);
		} else {
			String barcode = intent.getStringExtra(BARCODE);
			bundle.putString(BARCODE, barcode);
		}

		// give barcode to InsertProductAdapter
		ProductListFragment productListFragment = new ProductListFragment();
		productListFragment.setArguments(bundle);
		// give barcode to NewProductFragment
		NewProductFragment newProductFragment = new NewProductFragment();
		newProductFragment.setArguments(bundle);

		AuthenticationStateAdapter pagerAdapter = new AuthenticationStateAdapter(this);
		pagerAdapter.addFragment(productListFragment);
		pagerAdapter.addFragment(newProductFragment);
		viewPager.setAdapter(pagerAdapter);
		if (intent.getParcelableExtra("selectedProduct") != null) viewPager.setCurrentItem(1);
	}

	@Override
	public void onBackPressed() {
		if (viewPager.getCurrentItem() == 0) {
			// If the user is currently looking at the first step, allow the system to handle the
			// Back button. This calls finish() on this activity and pops the back stack.
			super.onBackPressed();
		} else {
			// Otherwise, select the previous step.
			viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}