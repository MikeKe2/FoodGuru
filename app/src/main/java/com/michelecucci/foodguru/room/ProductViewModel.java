package com.michelecucci.foodguru.room;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {

	private final ProductRepository productRepository;
	private final LiveData<List<Product>> productLiveData;
	private final MutableLiveData<String> modelFilter = new MutableLiveData<>();

	public ProductViewModel(Application application) {
		super(application);
		productRepository = new ProductRepository(application);
		modelFilter.setValue("");
		productLiveData = Transformations.switchMap(modelFilter, productRepository::getProducts);
	}

	public LiveData<List<Product>> getProductsLiveData() {
		return productLiveData;
	}

	public void insert(Product product) {
		productRepository.insert(product);
	}

	public void update(Product product) {
		productRepository.update(product);
	}

	public void delete(Product product) {
		productRepository.delete(product);
	}

	public void filterModel(String filterState) {
		modelFilter.setValue(filterState);
	}

}
