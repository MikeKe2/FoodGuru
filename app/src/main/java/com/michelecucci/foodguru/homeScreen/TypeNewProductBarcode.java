package com.michelecucci.foodguru.homeScreen;

import static com.michelecucci.foodguru.Constants.BARCODE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.michelecucci.foodguru.updating.InsertProductActivity;

public class TypeNewProductBarcode extends DialogFragment {

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		final EditText editText = new EditText(getContext());
		//editText.setPadding(16, 16, 16, 16);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		return new AlertDialog.Builder(requireContext())
				.setTitle("Add product")
				.setMessage("Insert barcode for new product")
				.setPositiveButton("Ok", (dialogInterface, i) -> {
					String barcode = editText.getText().toString();
					Intent intent = new Intent(getContext(), InsertProductActivity.class);
					intent.putExtra(BARCODE, barcode);
					startActivity(intent);
				})
				.setNegativeButton("Cancel", (dialogInterface, i) ->
						TypeNewProductBarcode.this.getDialog().cancel())
				.setView(editText)
				.create();
	}
}
