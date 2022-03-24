package com.michelecucci.foodguru.room;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ProductExpirationDates implements Parcelable, Comparable<ProductExpirationDates> {

	public static final Creator<ProductExpirationDates> CREATOR = new Creator<ProductExpirationDates>() {
		@Override
		public ProductExpirationDates createFromParcel(Parcel in) {
			return new ProductExpirationDates(in);
		}

		@Override
		public ProductExpirationDates[] newArray(int size) {
			return new ProductExpirationDates[size];
		}
	};

	public Date acquiredDate, expireDate;

	//PARCELABLE IMPLEMENTATION
	public ProductExpirationDates(Date acquiredDate, Date expireDate) {
		this.acquiredDate = acquiredDate;
		this.expireDate = expireDate;
	}

	protected ProductExpirationDates(Parcel in) {
		this.acquiredDate = new Date(in.readLong());
		this.expireDate = new Date(in.readLong());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeLong(this.acquiredDate.getTime());
		parcel.writeLong(this.expireDate.getTime());
	}

	@Override
	public int compareTo(ProductExpirationDates productExpirationDates) {
		return this.expireDate.compareTo(productExpirationDates.expireDate);
	}
	//PARCELABLE END
}
