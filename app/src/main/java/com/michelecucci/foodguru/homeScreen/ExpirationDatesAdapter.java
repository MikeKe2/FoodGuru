package com.michelecucci.foodguru.homeScreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.michelecucci.foodguru.R;
import com.michelecucci.foodguru.room.ProductExpirationDates;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ExpirationDatesAdapter extends RecyclerView.Adapter<ExpirationDatesAdapter.ViewHolder> {

	private final Context context;
	private final ArrayList<ProductExpirationDates> productExpirationDates;

	public ExpirationDatesAdapter(Context context, ArrayList<ProductExpirationDates> expirationDates) {
		this.context = context;
		this.productExpirationDates = expirationDates;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(context).inflate(R.layout.expiration_date_card, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ExpirationDatesAdapter.ViewHolder holder, int position) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
		holder.acquisitionDate.setText("Acquisition date: " + simpleDateFormat.format(this.productExpirationDates.get(position).acquiredDate));
		holder.expirationDate.setText("Expiration date: " + simpleDateFormat.format(this.productExpirationDates.get(position).expireDate));

		holder.removeBtn.setOnClickListener(v -> {
			FullProductCard activity = (FullProductCard) context;
			activity.removeExpirationDate(position);
		});
	}

	@Override
	public int getItemCount() {
		//When a product is added from remote DB, it doesn't create new instances
		if (productExpirationDates == null)
			return 0;
		else
			return productExpirationDates.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final TextView acquisitionDate, expirationDate;
		private final ImageButton removeBtn;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			this.acquisitionDate = itemView.findViewById(R.id.acquisition_date_product);
			this.expirationDate = itemView.findViewById(R.id.expiration_date_product);
			this.removeBtn = itemView.findViewById(R.id.remove_product_button);
		}
	}
}
