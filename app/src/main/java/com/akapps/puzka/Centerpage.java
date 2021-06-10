package com.akapps.puzka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class Centerpage extends AppCompatActivity {
    String defaultColor = "#000000";
    String selectedColor = "#00bbd3";
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centerpage);
        tabLayout = findViewById(R.id.tablayout);

        TabLayout.Tab tab1 = tabLayout.getTabAt(0);
        if(tab1 != null){
            tab1.select();
            Objects.requireNonNull(tab1.getIcon()).setColorFilter(Color.parseColor(selectedColor), PorterDuff.Mode.SRC_IN);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.frame, new Homepage()).commit();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setColorFilter(Color.parseColor(selectedColor), PorterDuff.Mode.SRC_IN);
                //int numoi = tab.getPosition();
                startActivity(new Intent(Centerpage.this, DiaryActivity.class));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setColorFilter(Color.parseColor(defaultColor), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (ContextCompat.checkSelfPermission(Centerpage.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Centerpage.this, new String[] {Manifest.permission.CAMERA}, 101);
        }
    }

    public void onimgClicked(View view){
        startActivity(new Intent(Centerpage.this, ImageToPdf.class));
    }

    public void ontextClicked(View view){
        startActivity(new Intent(Centerpage.this, NotepadPage.class));
    }

    public void onDrawingClicked(View view){
        startActivity(new Intent(Centerpage.this, DrawingCenterpage.class));
    }
}