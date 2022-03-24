package com.michelecucci.foodguru.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDao {

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	void insert(Product product);

	@Delete
	void delete(Product product);

	@Update
	void update(Product product);

	@Query("SELECT * FROM product WHERE id IN (:id)")
	Product findById(String id);

	@Query("SELECT * FROM product ORDER BY name ASC")
	LiveData<List<Product>> getAllLD();

	@Query("SELECT * FROM product WHERE name LIKE '%' || :pantryItemName || '%' ORDER BY name ASC")
	LiveData<List<Product>> getAllLDFiltered(String pantryItemName);

	@Query("SELECT * FROM product ORDER BY name ASC")
	List<Product> getAll();

}
