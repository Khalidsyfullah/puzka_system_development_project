package com.akapps.puzka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageProcessing extends AppCompatActivity {
    CropImageView cropImageView;
    SharedPreferences sharedPreferences;
    String filename;
    Bitmap bitmap;
    TextView save_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);
        cropImageView = findViewById(R.id.cropImageView);
        save_txt = findViewById(R.id.textView4);
        sharedPreferences = new SecurePreferences(this);
        filename = sharedPreferences.getString("Imagename", "no");
        File file = new File(getExternalFilesDir(null), filename);
        cropImageView.setImageUriAsync(Uri.fromFile(file));
        cropImageView.setOnCropImageCompleteListener((view, result) -> {
            cropImageView.setImageBitmap(result.getBitmap());
        });

        save_txt.setOnClickListener(v -> {
            Uri uri = cropImageView.getImageUri();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                File file1 = new File(getExternalFilesDir(null), "file.png");
                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                Toast.makeText(ImageProcessing.this, "Successfully Added!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(ImageProcessing.this, "Error!"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
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