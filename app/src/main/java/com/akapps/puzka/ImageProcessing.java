package com.akapps.puzka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.securepreferences.SecurePreferences;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

public class ImageProcessing extends AppCompatActivity {
    CropImageView cropImageView;
    SharedPreferences sharedPreferences;
    String filename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);
        cropImageView = findViewById(R.id.cropImageView);
        sharedPreferences = new SecurePreferences(this);
        filename = sharedPreferences.getString("Imagename", "no");
        File file = new File(getExternalFilesDir(null), filename);
        cropImageView.setImageUriAsync(Uri.fromFile(file));
        cropImageView.setOnCropImageCompleteListener((view, result) -> {
            cropImageView.setImageBitmap(result.getBitmap());
        });
    }

    public void onRotateClicked(View view) {
        cropImageView.rotateImage(90);
    }

    public void onFliphorizontallyClicked(View view) {
        cropImageView.flipImageHorizontally();
    }

    public void onFlipVerticallyClicked(View view) {
        cropImageView.flipImageVertically();
    }

    public void onCheckedClicked(View view) {
        cropImageView.getCroppedImageAsync();
    }
}