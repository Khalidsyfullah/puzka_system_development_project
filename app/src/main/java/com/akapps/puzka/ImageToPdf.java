package com.akapps.puzka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ImageToPdf extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_pdf);
    }

    public void onCameraClicked(View view){
        startActivity(new Intent(ImageToPdf.this, CameraActivity.class));
    }
}