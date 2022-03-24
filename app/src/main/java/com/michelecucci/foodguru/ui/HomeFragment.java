package com.michelecucci.foodguru.ui;

import static com.michelecucci.foodguru.Constants.CURRENT_SEARCH;
import static com.michelecucci.foodguru.Constants.ONLY_LIKED;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.michelecucci.foodguru.R;
import com.michelecucci.foodguru.databinding.FragmentHomeBinding;
import com.michelecucci.foodguru.homeScreen.ProductsAdapter;
import com.michelecucci.foodguru.homeScreen.TypeNewProductBarcode;
import com.michelecucci.foodguru.room.Product;
import com.michelecucci.foodguru.room.ProductViewModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

	private FragmentHomeBinding binding;
	private ProductViewModel productViewModel;
	private SearchView searchView;
	private TextView textView;
	private FloatingActionButton floatingActionButton;
	private ArrayList<Product> productsArrayList = new ArrayList<>();
	private final ArrayList<Product> products = new ArrayList<>();
	private RecyclerView recyclerView;
	private ProductsAdapter productsAdapter;
	private ImageButton imageButton;
	private boolean liked;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		binding = FragmentHomeBinding.inflate(inflater, container, false);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		textView = view.findViewById(R.id.empty_view);
		searchView = view.findViewById(R.id.searchView);
		imageButton = view.findViewById(R.id.searchLikedProductBtn);
		recyclerView = view.findViewById(R.id.recycler_viewCardView);
		floatingActionButton = view.findViewById(R.id.floatingActionButton);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		//search/filters options
		if (savedInstanceState != null) {
			liked = savedInstanceState.getBoolean(ONLY_LIKED);
			if (liked) {
				imageButton.setColorFilter(Color.RED);
				for (int i = 0; i < productsArrayList.size(); i++)
					if (productsArrayList.get(i).liked) products.add(productsArrayList.get(i));

			}
		}

		updateAdapter();

		productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
		productViewModel.getProductsLiveData().observe(getViewLifecycleOwner(), productList -> {
			productsArrayList = (ArrayList<Product>) productList;
			productsAdapter.setProducts(productsArrayList);

			recyclerView.setVisibility(productsArrayList.isEmpty() ? View.GONE : View.VISIBLE);
			textView.setVisibility(productsArrayList.isEmpty() ? View.VISIBLE : View.GONE);
		});

		new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
			@Override
			public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
				return false;
			}

			@Override
			public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
				productViewModel.delete(productsAdapter.getProduct(viewHolder.getBindingAdapterPosition()));
			}
		}).attachToRecyclerView(recyclerView);


		this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				productViewModel.filterModel(newText);
				return true;
			}
		});

		imageButton.setOnClickListener(view1 -> {
			products.clear();
			imageButton.setColorFilter(liked ? Color.DKGRAY : Color.RED);
			if (!liked) {
				for (int i = 0; i < productsArrayList.size(); i++)
					if (productsArrayList.get(i).liked) products.add(productsArrayList.get(i));
			}
			liked = !liked;
			updateAdapter();
		});

		floatingActionButton.setOnClickListener(view1 -> new TypeNewProductBarcode().show(getChildFragmentManager(), "PurchaseConfirmationDialog"));
	}

	private void updateAdapter() {
		productsAdapter = new ProductsAdapter(getContext(), liked ? products : productsArrayList);
		recyclerView.setAdapter(productsAdapter);
		productsAdapter.notifyDataSetChanged();
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(CURRENT_SEARCH, searchView.toString());
		outState.putBoolean(ONLY_LIKED, liked);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}


}