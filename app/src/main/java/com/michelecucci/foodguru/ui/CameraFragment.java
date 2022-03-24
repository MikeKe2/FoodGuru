package com.michelecucci.foodguru.ui;

import static com.michelecucci.foodguru.Constants.BARCODE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.michelecucci.foodguru.R;
import com.michelecucci.foodguru.updating.InsertProductActivity;

import java.util.concurrent.ExecutionException;

@androidx.camera.core.ExperimentalGetImage
public class CameraFragment extends Fragment {

	private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
	private PreviewView previewView;
	private ProcessCameraProvider processCameraProvider;

	private final ActivityResultLauncher<String> requestPermissionLauncher =
			registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
				if (isGranted) {
					startCamera();
				}  // Explain to the user that the feature is unavailable because the
				// features requires a permission that the user has denied. At the
				// same time, respect the user's decision. Don't link to system
				// settings in an effort to convince the user to change their
				// decision.

			});

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
							 @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_qr, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		previewView = view.findViewById(R.id.previewView);
		cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
		processCameraProvider = null;
		getCameraPermission();
	}

	private void getCameraPermission() {
		if (ContextCompat.checkSelfPermission(getContext(),
				Manifest.permission.CAMERA)
				== PackageManager.PERMISSION_GRANTED) {
			startCamera();
		} else {
			requestPermissionLauncher.launch(Manifest.permission.CAMERA);
		}
	}

	private void startCamera() {
		cameraProviderFuture.addListener(() -> {
			try {
				processCameraProvider = cameraProviderFuture.get();
				imageAnalysis();
			} catch (ExecutionException | InterruptedException e) {
				e.printStackTrace();
			}
		}, ContextCompat.getMainExecutor(getContext()));
	}

	private void imageAnalysis() {

		ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
				.setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
				.setTargetResolution(new Size(720, 1080))
				.build();

		BarcodeScannerOptions options =
				new BarcodeScannerOptions.Builder()
						.setBarcodeFormats(
								Barcode.FORMAT_UPC_A,
								Barcode.FORMAT_EAN_8,
								Barcode.FORMAT_EAN_13,
								Barcode.FORMAT_ITF)
						.build();

		imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(getContext()), imageProxy -> {
			Image mediaImage = imageProxy.getImage();
			if (mediaImage != null) {
				InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

				BarcodeScanner scanner = BarcodeScanning.getClient(options);
				scanner.process(image)
						.addOnSuccessListener(barcodes -> {
									for (Barcode barcode : barcodes) {
										String rawValue = barcode.getRawValue();
										Intent intent = new Intent(getContext(), InsertProductActivity.class);
										intent.putExtra(BARCODE, rawValue);
										startActivity(intent);
										imageAnalysis.clearAnalyzer();
									}
								}
						)
						.addOnFailureListener(e -> Log.e("ERROR: ", e.toString()))
						.addOnCompleteListener(task -> {
							//always close image proxy from camerax
							imageProxy.close();
						});
			}
		});

		Preview preview = new Preview.Builder()
				.setTargetResolution(new Size(720, 1080))
				.build();
		preview.setSurfaceProvider(previewView.getSurfaceProvider());
		//processCameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis);
		processCameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, imageAnalysis, preview);
	}
}
