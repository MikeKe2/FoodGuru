package com.michelecucci.foodguru.room;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.michelecucci.foodguru.utils.ConvertsProductsList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity(tableName = "product")
public class Product implements Parcelable {

	public static final Parcelable.Creator<Product> CREATOR
			= new Parcelable.Creator<Product>() {
		public Product createFromParcel(Parcel in) {
			return new Product(in);
		}

		public Product[] newArray(int size) {
			return new Product[size];
		}
	};

	@PrimaryKey
	public @NonNull
	String id;
	public String name;
	public String description;
	public String barcode;
	public String image;
	public boolean type;
	public String sessionToken;
	public boolean liked;

	//info about expiration dates of the singular product
	@TypeConverters({ConvertsProductsList.class})
	public List<ProductExpirationDates> expirationDates;

	public Product(@NonNull String id, String barcode, String name, String description,
				   String image, boolean type) {
		this.id = id;
		this.barcode = barcode;
		this.name = name;
		this.description = description;
		this.image = image != null ? image : "";
		this.type = type;
		this.expirationDates = new ArrayList<>();
		this.liked = false;
	}

	//PARCELABLE IMPLEMENTATION

	@SuppressWarnings("unchecked")
	private Product(Parcel in) {
		this.id = in.readString();
		this.barcode = in.readString();
		this.name = in.readString();
		this.description = in.readString();
		this.image = in.readString();
		this.type = in.readByte() != 0;
		this.expirationDates = (List<ProductExpirationDates>) in.readValue(Product.class.getClassLoader());
		this.liked = in.readByte() != 0;    //liked == true if byte != 0
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(id);
		out.writeString(barcode);
		out.writeString(name);
		out.writeString(description);
		out.writeString(image);
		out.writeByte((byte) (type ? 1 : 0));
		out.writeValue(expirationDates);
		out.writeByte((byte) (liked ? 1 : 0));    //if liked == true, byte == 1
	}

	//PARCELABLE END

	public void addDates(Integer amount, Date acquiredDate, Date expireDate) {
		for (int i = 0; i < amount; i++) {
			ProductExpirationDates pi = new ProductExpirationDates(acquiredDate, expireDate);
			this.expirationDates.add(pi);
		}
		Collections.sort(this.expirationDates);
	}
}



