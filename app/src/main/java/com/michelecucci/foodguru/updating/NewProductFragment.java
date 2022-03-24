package com.michelecucci.foodguru.updating;

import static com.michelecucci.foodguru.Constants.ACCESS_TOKEN;
import static com.michelecucci.foodguru.Constants.BARCODE;
import static com.michelecucci.foodguru.Constants.PRODUCT;
import static com.michelecucci.foodguru.Constants.SESSION_TOKEN;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.michelecucci.foodguru.MainActivity;
import com.michelecucci.foodguru.R;
import com.michelecucci.foodguru.utils.HorizontalNumberPicker;
import com.michelecucci.foodguru.utils.SelectPhotoDialogFragment;
import com.michelecucci.foodguru.model.Pantry;
import com.michelecucci.foodguru.model.ProductPost;
import com.michelecucci.foodguru.remote.ApiUtils;
import com.michelecucci.foodguru.remote.UserInterface;
import com.michelecucci.foodguru.room.Product;
import com.michelecucci.foodguru.room.ProductViewModel;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewProductFragment extends Fragment {

	private HorizontalNumberPicker productQuantity;
	private ProductViewModel productViewModel;
	private EditText productName, productDescription, expirationDate;
	private TextView productId;
	private ImageView productImg;
	private DatePickerDialog datePickerDialog;
	private Date expireDate, acquiredDate;
	private LinearLayout mProgressBar;
	private Product product;

	private boolean alreadyInDB = false;
	private String token = null;
	private String barcode = null;
	private String imgString = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_new_product, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		productViewModel = new ProductViewModel(getActivity().getApplication());
		getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
			imgString = bundle.getString("bundleKey");
			setImagePreview();
		});

		Button insertNewProduct = view.findViewById(R.id.add_product);
		productId = view.findViewById(R.id.product_id);
		productImg = view.findViewById(R.id.add_image_preview);
		productName = view.findViewById(R.id.add_name);
		productDescription = view.findViewById(R.id.add_descr);
		expirationDate = view.findViewById(R.id.ExpirationDate);
		productQuantity = view.findViewById(R.id.product_quantity);
		mProgressBar = view.findViewById(R.id.progress_bar);

		token = getArguments().getString(ACCESS_TOKEN);
		mProgressBar.setVisibility(View.GONE);

		productQuantity.setMax(20);
		productQuantity.setMin(0);
		productQuantity.setValue(1);

		alreadyInDB = false;
		barcode = getArguments().getString(BARCODE);
		productId.setText("Barcode: " + barcode);

		if (getArguments().getParcelable(PRODUCT) != null) {
			alreadyInDB = true;
			product = getArguments().getParcelable(PRODUCT);
			productId.setText("Barcode: " + product.barcode);
			productName.setText(product.name);
			productDescription.setText(product.description);
			imgString = product.image;
			insertNewProduct.setText("update product");
			insertNewProduct.setBackgroundColor(Color.MAGENTA);
			setImagePreview();
		}

		expirationDate.setOnClickListener(view1 -> {
			final Calendar calendar = Calendar.getInstance();
			int mYear = calendar.get(Calendar.YEAR);
			int mMonth = calendar.get(Calendar.MONTH);
			int mDay = calendar.get(Calendar.DAY_OF_MONTH);
			datePickerDialog = new DatePickerDialog(getContext(),
					(view2, year, monthOfYear, dayOfMonth) -> {
						// set day of month , month and year value in the edit text
						Calendar c = Calendar.getInstance();
						c.set(Calendar.YEAR, year);
						c.set(Calendar.MONTH, monthOfYear);
						c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						expireDate = c.getTime();
						Calendar c1 = Calendar.getInstance();
						c1.setTime(new Date());
						acquiredDate = c1.getTime();
						if (expireDate.compareTo(acquiredDate) >= 0) {
							expirationDate.setText(DateFormat.getDateInstance().format(c.getTime()));
						} else {
							expirationDate.setError("Expiration dates cannot be a past date");
							expireDate = acquiredDate;
						}
					}, mYear, mMonth, mDay);
			datePickerDialog.show();
		});

		productImg.setOnClickListener(view1 -> {
			SelectPhotoDialogFragment selectPhotoDialogFragment = new SelectPhotoDialogFragment();
			selectPhotoDialogFragment.show(getActivity().getSupportFragmentManager(), "add_photo_dialog_fragment");
		});

		insertNewProduct.setOnClickListener(view1 -> insertProductToDB());
	}

	private void insertProductToDB() {
		String name = productName.getText().toString();
		String description = productDescription.getText().toString();
		if (!name.isEmpty() && barcode != null && token != null) {
			if (isConnectingToInternet()) {
				//insert item online and in the db
				if (!alreadyInDB)
					sendNewItem(name, description, imgString);
				else
					updateCurrentItem(name, description, imgString);
			} else
				Toast.makeText(getContext(), getString(R.string.no_internet_available_update), Toast.LENGTH_LONG).show();
		} else {
			if (name.isEmpty()) {
				productName.setError(getString(R.string.no_name_error));
			} else
				productId.setError(getString(R.string.no_barcode_error));
		}
	}

	private boolean isConnectingToInternet() {
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			Toast.makeText(getContext(), "no internet access", Toast.LENGTH_LONG).show();
			return false;
		} else
			return true;
	}

	private void updateCurrentItem(String name, String description, String imgString) {

		mProgressBar.setVisibility(View.VISIBLE);

		product.barcode = barcode;
		product.name = name;
		product.description = description;
		product.image = imgString;

		int amount = productQuantity.getValue();

		if (acquiredDate == null && expireDate == null) {
			acquiredDate = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(acquiredDate);
			c.add(Calendar.DAY_OF_MONTH, 7);
			expireDate = c.getTime();
		}

		product.addDates(amount, acquiredDate, expireDate);
		productViewModel.update(product);

		Snackbar.make(getView(), "Product updated", Snackbar.LENGTH_LONG).show();

		Intent intent = new Intent(getContext(), MainActivity.class);
		intent.putExtra(ACCESS_TOKEN, token);
		startActivity(intent);
		getArguments().clear();
		getActivity().finish();

	}

	private void sendNewItem(String itemName, String itemDescr, String itemImage) {

		mProgressBar.setVisibility(View.VISIBLE);

		UserInterface userInterface = ApiUtils.getUserServiceWithAccessToken(token);
		String sessionToken = getActivity().getSharedPreferences(getString(R.string.session_key), Context.MODE_PRIVATE).getString(SESSION_TOKEN, null);

		if (sessionToken != null) {
			int amount = productQuantity.getValue();
			final ProductPost postProduct = new ProductPost(sessionToken, itemName, itemDescr, barcode, false);
			Call<Pantry> call = userInterface.setProductDetails(postProduct);
			call.enqueue(new Callback<Pantry>() {
				@Override
				public void onResponse(Call<Pantry> call, Response<Pantry> response) {
					if (response.isSuccessful()) {
						String id = response.body().getId();

						Product product = new Product(id, barcode, itemName, itemDescr, itemImage != null ? itemImage : "null", false);

						productViewModel.insert(product);

						if (acquiredDate == null && expireDate == null) {
							acquiredDate = new Date();
							Calendar c = Calendar.getInstance();
							c.setTime(acquiredDate);
							c.add(Calendar.DAY_OF_MONTH, 7);
							expireDate = c.getTime();
						}
						product.addDates(amount, acquiredDate, expireDate);
						productViewModel.update(product);

						Intent intent = new Intent(getContext(), MainActivity.class);
						intent.putExtra(ACCESS_TOKEN, token);
						startActivity(intent);
						getActivity().finish();

						Snackbar.make(getView(), "Product added to db online", Snackbar.LENGTH_LONG).show();
					} else {
						try {
							JSONObject jObjError = new JSONObject(response.errorBody().string());
							Toast.makeText(getContext(), jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
						} catch (Exception e) {
							Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
							Log.e("onResponse: ", e.getMessage());
						}
					}
				}

				@Override
				public void onFailure(Call<Pantry> call, Throwable t) {
					t.printStackTrace();
				}
			});
		}
	}

	private void setImagePreview() {
		if (imgString.isEmpty()) {
			productImg.setImageResource(R.drawable.ic_baseline_no_food_24);
		} else {
			Picasso.get()
					.load(Uri.parse(imgString))
					.placeholder(R.drawable.ic_baseline_no_food_24)
					.into(productImg, new com.squareup.picasso.Callback() {
						@Override
						public void onSuccess() {
						}

						@Override
						public void onError(Exception e) {
							Toast.makeText(getContext(), getString(R.string.fail_img_error),
									Toast.LENGTH_SHORT).show();
						}
					});
		}
	}
}