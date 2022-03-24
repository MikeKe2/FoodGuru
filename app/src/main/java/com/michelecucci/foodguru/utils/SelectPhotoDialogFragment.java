package com.michelecucci.foodguru.utils;

import static android.app.Activity.RESULT_OK;
import static com.michelecucci.foodguru.Constants.CAMERA_REQUEST_CODE;
import static com.michelecucci.foodguru.Constants.GALLERY_REQUEST_CODE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.michelecucci.foodguru.R;

import java.io.ByteArrayOutputStream;

public class SelectPhotoDialogFragment extends BottomSheetDialogFragment {

	private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			result -> {
				if (result.getResultCode() == RESULT_OK) {
					// There are no request codes
					GetPhoto(CAMERA_REQUEST_CODE, result.getResultCode(), result.getData());
				}
			});
	private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			result -> {
				if (result.getResultCode() == RESULT_OK) {
					// There are no request codes
					GetPhoto(GALLERY_REQUEST_CODE, result.getResultCode(), result.getData());
				}
			});

	private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
			registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
				if (isGranted) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					cameraActivityResultLauncher.launch(intent);
				}
			});

	private final ActivityResultLauncher<String> requestGalleryPermissionLauncher =
			registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
				if (isGranted) {
					Intent galleryIntent = new Intent();
					galleryIntent.setType("image/*");
					galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
					galleryActivityResultLauncher.launch(galleryIntent);
				}
			});

	private void getCameraPermission() {
		if (ContextCompat.checkSelfPermission(getContext(),
				Manifest.permission.CAMERA)
				== PackageManager.PERMISSION_GRANTED) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			cameraActivityResultLauncher.launch(intent);
		} else {
			requestGalleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
		}
	}

	private void getGalleryPermission() {
		if (ContextCompat.checkSelfPermission(getContext(),
				Manifest.permission.CAMERA)
				== PackageManager.PERMISSION_GRANTED) {
			Intent galleryIntent = new Intent();
			galleryIntent.setType("image/*");
			galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
			galleryActivityResultLauncher.launch(galleryIntent);
		} else {
			requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.select_photo_bottom_dialog, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ConstraintLayout linearLayout1 = view.findViewById(R.id.layout_camera);
		ConstraintLayout linearLayout2 = view.findViewById(R.id.layout_gallery);
		linearLayout1.setOnClickListener(view1 -> getCameraPermission());
		linearLayout2.setOnClickListener(view1 -> getGalleryPermission());
	}

	private void GetPhoto(int requestCode, int resultCode, Intent data) {
		Bitmap bitmap = null;
		if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
			//CAMERA REQUEST
			bitmap = (Bitmap) data.getExtras().get("data");

		} else if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
			//GALLERY REQUEST
			String imageIdString = data.getData().getPathSegments().get(data.getData().getPathSegments().size() - 1);
			long imageId = Long.parseLong(imageIdString.substring("image:".length()));
			bitmap = MediaStore.Images.Thumbnails.getThumbnail(getActivity().getContentResolver(),
					imageId, MediaStore.Images.Thumbnails.MINI_KIND, null);
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		assert bitmap != null;
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Title", null);
		Bundle result = new Bundle();
		result.putString("bundleKey", path);
		getParentFragmentManager().setFragmentResult("requestKey", result);
		this.dismiss();
	}
}