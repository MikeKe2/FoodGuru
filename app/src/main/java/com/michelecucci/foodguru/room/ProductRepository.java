package com.michelecucci.foodguru.room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ProductRepository {

	private final ProductDao mProductDao;

	public ProductRepository(Application application) {
		ProductDatabase db = ProductDatabase.getInstance(application);
		mProductDao = db.getProductDao();
	}

	public void insert(Product product) {
		new insertProductAsyncTask(mProductDao).execute(product);
	}

	public void delete(Product product) {
		new deleteProductAsyncTask(mProductDao).execute(product);
	}

	public void update(Product product) {
		new updateProductAsyncTask(mProductDao).execute(product);
	}

	public Product findById(Product product) {
		return mProductDao.findById(product.id);
	}

	public LiveData<List<Product>> getProducts(String nameFilter) {
		if (nameFilter.equals("")) {
			return mProductDao.getAllLD();
		} else {
			return mProductDao.getAllLDFiltered(nameFilter);
		}
	}

	private static class insertProductAsyncTask extends AsyncTask<Product, Void, Void> {
		private final ProductDao productDao;

		private insertProductAsyncTask(ProductDao productDao) {
			this.productDao = productDao;
		}

		@Override
		protected Void doInBackground(Product... products) {
			productDao.insert(products[0]);
			return null;
		}
	}

	private static class deleteProductAsyncTask extends AsyncTask<Product, Void, Void> {
		private final ProductDao productDao;

		private deleteProductAsyncTask(ProductDao productDao) {
			this.productDao = productDao;
		}

		@Override
		protected Void doInBackground(Product... products) {
			productDao.delete(products[0]);
			return null;
		}
	}

	private static class updateProductAsyncTask extends AsyncTask<Product, Void, Void> {
		private final ProductDao productDao;

		private updateProductAsyncTask(ProductDao productDao) {
			this.productDao = productDao;
		}

		@Override
		protected Void doInBackground(Product... products) {
			productDao.update(products[0]);
			return null;
		}
	}

}
