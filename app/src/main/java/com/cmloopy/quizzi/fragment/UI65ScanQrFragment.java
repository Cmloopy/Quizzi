package com.cmloopy.quizzi.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.cmloopy.quizzi.R;

public class UI65ScanQrFragment extends Fragment {

    private PreviewView previewView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ui_65_scan_qr_fragment, container, false);

        previewView = new PreviewView(requireContext());
        FrameLayout previewContainer = view.findViewById(R.id.previewViewContainer);
        previewContainer.addView(previewView);

        startCamera();

        return view;
    }

    private void startCamera() {
        ProcessCameraProvider.getInstance(requireContext()).addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(requireContext()).get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext()), image -> {
                    // QR Code xử lý tại đây nếu cần
                    image.close();
                });

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                Log.e("Camera", "startCamera: ", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

}
