package com.michelecucci.foodguru.updating;

import static com.michelecucci.foodguru.Constants.ACCESS_TOKEN;
import static com.michelecucci.foodguru.Constants.BARCODE;
import static com.michelecucci.foodguru.Constants.SESSION_TOKEN;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.michelecucci.foodguru.MainActivity;
import com.michelecucci.foodguru.R;
import com.michelecucci.foodguru.model.ListOfProducts;
import com.michelecucci.foodguru.model.Pantry;
import com.michelecucci.foodguru.remote.ApiUtils;
import com.michelecucci.foodguru.remote.UserInterface;
import com.michelecucci.foodguru.room.Product;
import com.michelecucci.foodguru.room.ProductRepository;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListFragment extends Fragment {

	private final ArrayList<Product> productArrayList = new ArrayList<>();
	private final ArrayList<Product> productArrayList1 = new ArrayList<>();
	private ProductListAdapter productListAdapter;
	private RecyclerView recyclerView;
	private String token, barcode;
	private FloatingActionButton floatingActionButton;
	private TextView noProductView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_product_list, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		barcode = getArguments().getString(BARCODE);
		token = getArguments().getString(ACCESS_TOKEN);

		recyclerView = view.findViewById(R.id.recycler_view_products_list);
		floatingActionButton = view.findViewById(R.id.floatingActionButton2);
		TextView textView = view.findViewById(R.id.textView);
		noProductView = view.findViewById(R.id.no_products_textView);
		textView.setText("Remote products with barcode:" + barcode);

		if (isConnectingToInternet()) requestListOfProducts();

	}

	private void requestListOfProducts() {

		Executor myExecutor = Executors.newSingleThreadExecutor();
		ProductRepository productRepository = new ProductRepository(getActivity().getApplication());

		UserInterface userInterface = ApiUtils.getUserServiceWithAccessToken(token);
		Call<ListOfProducts> call = userInterface.getProducts(barcode);

		floatingActionButton.setOnClickListener(view -> {
			Intent intent = new Intent(getContext(), MainActivity.class);
			intent.putExtra(ACCESS_TOKEN, token);
			startActivity(intent);
			getActivity().finish();
		});

		call.enqueue(new Callback<ListOfProducts>() {
			@Override
			public void onResponse(Call<ListOfProducts> call, Response<ListOfProducts> response) {
				if (response.isSuccessful()) {
					getActivity().getSharedPreferences(getString(R.string.session_key), Context.MODE_PRIVATE)
							.edit()
							.putString(SESSION_TOKEN, response.body().getToken())
							.apply();
					for (Pantry pantry : response.body().getProducts()) {
						Product product = new Product(
								pantry.getId(),
								pantry.getBarcode(),
								pantry.getName(),
								pantry.getDescription(),
								null,
								false);
						productArrayList.add(product);
					}
					myExecutor.execute(() -> {
						for (Product product : productArrayList) {
							if (productRepository.findById(product) == null) {
								productArrayList1.add(product);
							}
						}
						if (productArrayList1.size() <= 0 && response.body().getProducts().size() > 0)
							Snackbar.make(getView(), R.string.no_new_products_found, Snackbar.LENGTH_LONG).show();
						getActivity().runOnUiThread(() -> productListAdapter.notifyDataSetChanged());

					});
					productListAdapter = new ProductListAdapter(getContext(), getActivity(), productArrayList1, token);
					recyclerView.setAdapter(productListAdapter);
					recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
				}
				if (response.body().getProducts().size() <= 0) {
					recyclerView.setVisibility(View.GONE);
					noProductView.setVisibility(View.VISIBLE);
				} else {
					recyclerView.setVisibility(View.VISIBLE);
					noProductView.setVisibility(View.GONE);

				}
			}

			@Override
			public void onFailure(Call<ListOfProducts> call, Throwable t) {
			}
		});
	}

	private boolean isConnectingToInternet() {
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			Toast.makeText(getContext(), "no internet access, cannot recover Products", Toast.LENGTH_LONG).show();
			return false;
		} else
			return true;
	}
}
