package com.akapps.puzka;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.securepreferences.SecurePreferences;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    int flash_mode = 0, auto_rotate = 0, image_quality = 0, camera_type = 1;
    int visi = 0;
    RadioGroup rotation, quality;
    ImageCapture imageCapture;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        previewView = findViewById(R.id.previewView);
        rotation = findViewById(R.id.radio1);
        quality = findViewById(R.id.radio2);
        imageCapture = new ImageCapture.Builder().build();
        sharedPreferences = new SecurePreferences(this);
        OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                int rotation;

                if (orientation >= 45 && orientation < 135) {
                    rotation = Surface.ROTATION_270;
                } else if (orientation >= 135 && orientation < 225) {
                    rotation = Surface.ROTATION_180;
                } else if (orientation >= 225 && orientation < 315) {
                    rotation = Surface.ROTATION_90;
                } else {
                    rotation = Surface.ROTATION_0;
                }

                imageCapture.setTargetRotation(rotation);
            }
        };

        orientationEventListener.enable();

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> bindPreview(camera_type), ContextCompat.getMainExecutor(this));


        rotation.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.radioButton1){
                auto_rotate = 0;
            }
            else if(checkedId == R.id.radioButton2){
                auto_rotate = 1;
            }
            else {
                auto_rotate = 2;
            }
            visi = 0;
            new Handler(Looper.getMainLooper()).postDelayed(runnable, 500);
        });

        quality.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.radioButton4){
                image_quality = 0;
            }
            else if(checkedId == R.id.radioButton5){
                image_quality = 1;
            }
            else if(checkedId == R.id.radioButton6){
                image_quality = 2;
            }
            else {
                image_quality = 3;
            }
            visi = 1;
            new Handler(Looper.getMainLooper()).postDelayed(runnable, 500);
        });


    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(visi == 0) rotation.setVisibility(View.GONE);
            else quality.setVisibility(View.GONE);
        }
    };

    void bindPreview(int n) {
        try {
            ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
            cameraProvider.unbindAll();
            Preview preview = new Preview.Builder()
                    .build();
            CameraSelector cameraSelector;
            if(n==0){
                cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();
            }
            else{
                cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
            }

            preview.setSurfaceProvider(previewView.getSurfaceProvider());

            cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview);
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(CameraActivity.this, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }



    @Override
    protected void onResume() {
        if(quality.getVisibility() == View.VISIBLE){
            quality.setVisibility(View.GONE);
        }
        if(rotation.getVisibility() == View.VISIBLE){
            rotation.setVisibility(View.GONE);
        }
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void onImageRotation(View view) {
        if(quality.getVisibility() == View.VISIBLE){
            quality.setVisibility(View.GONE);
        }
        if(rotation.getVisibility() == View.GONE){
            rotation.setVisibility(View.VISIBLE);
        }
        else if(rotation.getVisibility() == View.VISIBLE){
            rotation.setVisibility(View.GONE);
        }
        if(auto_rotate == 0){
            rotation.check(R.id.radioButton1);
        }
        else if(auto_rotate == 1){
            rotation.check(R.id.radioButton2);
        }
        else{
            rotation.check(R.id.radioButton3);
        }
    }

    public void onImageCaptured(View view){
        if(quality.getVisibility() == View.VISIBLE){
            quality.setVisibility(View.GONE);
        }
        if(rotation.getVisibility() == View.VISIBLE){
            rotation.setVisibility(View.GONE);
        }
        if(flash_mode == 0){
            imageCapture.setFlashMode(ImageCapture.FLASH_MODE_ON);
        }else if(flash_mode == 1){
            imageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
        }else {
            imageCapture.setFlashMode(ImageCapture.FLASH_MODE_AUTO);
        }

        String filename = new SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.US).format(System.currentTimeMillis()) + ".jpg";
        File file = new File(CameraActivity.this.getExternalFilesDir(null), filename);
        ImageCapture.OutputFileOptions outputFileOptions = new
                ImageCapture.OutputFileOptions.Builder(file).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(CameraActivity.this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NotNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(CameraActivity.this, "Successfully Saved!", Toast.LENGTH_SHORT).show();
                        sharedPreferences.edit().putString("Imagename", filename).apply();
                        new Handler(Looper.getMainLooper()).postDelayed(() -> startActivity(new Intent(CameraActivity.this,
                                ImageProcessing.class)), 500);
                    }
                    @Override
                    public void onError(@NotNull ImageCaptureException error) {
                        Toast.makeText(CameraActivity.this, "Error! "+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }


    public void onCameraController(View view) {
        if(quality.getVisibility() == View.VISIBLE){
            quality.setVisibility(View.GONE);
        }
        if(rotation.getVisibility() == View.VISIBLE){
            rotation.setVisibility(View.GONE);
        }
        if(camera_type == 1){
            camera_type = 0;
        }else{
            camera_type = 1;
        }
        bindPreview(camera_type);
    }

    public void onImageResolutionControl(View view) {

        if(rotation.getVisibility() == View.VISIBLE){
            rotation.setVisibility(View.GONE);
        }
        if(quality.getVisibility() == View.VISIBLE){
            quality.setVisibility(View.GONE);
        }
        else if(quality.getVisibility() == View.GONE){
            quality.setVisibility(View.VISIBLE);
        }

        if(image_quality == 0 ){
            quality.check(R.id.radioButton4);
        }
        else if(image_quality == 1){
            quality.check(R.id.radioButton5);
        }
        else if(image_quality == 2){
            quality.check(R.id.radioButton6);
        }
        else{
            quality.check(R.id.radioButton7);
        }

    }

    public void onFlashOnoffControl(View view) {
        if(quality.getVisibility() == View.VISIBLE){
            quality.setVisibility(View.GONE);
        }
        if(rotation.getVisibility() == View.VISIBLE){
            rotation.setVisibility(View.GONE);
        }
        Button button = (Button) view;
        if(flash_mode == 0){
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_flashoff, 0);
            flash_mode = 1;
        }
        else if(flash_mode == 1){
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_flashauto, 0);
            flash_mode = 2;
        }
        else{
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_flashon, 0);
            flash_mode = 0;
        }
    }
}