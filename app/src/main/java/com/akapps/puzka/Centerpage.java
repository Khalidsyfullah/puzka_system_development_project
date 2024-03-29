package com.akapps.puzka;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class Centerpage extends AppCompatActivity {
    String defaultColor = "#000000";
    String selectedColor = "#00bbd3";
    TabLayout tabLayout;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Material3);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centerpage);
        tabLayout = findViewById(R.id.tablayout);
        textView = findViewById(R.id.textView115);
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
                if(tab.getPosition() == 0){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Homepage()).commit();
                }
                else if(tab.getPosition() == 1){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new FragmentMenu()).commit();
                }
                else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new FragmentSettings()).commit();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setColorFilter(Color.parseColor(defaultColor), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        textView.setOnClickListener(v -> setBackPressed());

    }

    @Override
    public void onBackPressed() {
        setBackPressed();
    }

    void setBackPressed()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(Centerpage.this)
                .setMessage("Are you sure to Exit This app?")
                .setPositiveButton("Yes", (dialog, which) -> Centerpage.this.finish())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()).create();

        alertDialog.show();
    }



    public void notepadPageOpen()
    {
        startActivity(new Intent(Centerpage.this, NotepadPage.class));
    }

    public void drawingPage()
    {
        startActivity(new Intent(Centerpage.this, DrawingCenterpage.class));
    }

    public void diaryPage()
    {
        startActivity(new Intent(Centerpage.this, DiaryActivity.class));
    }

    public void walletPage()
    {
        startActivity(new Intent(Centerpage.this, WaletCenterpage.class));
    }

    public void routinePage()
    {
        startActivity(new Intent(Centerpage.this, RoutineCerterpage.class));
    }

    public void startLinks()
    {
        startActivity(new Intent(Centerpage.this, LinksActivity.class));
    }

    public void startNotes()
    {
        startActivity(new Intent(Centerpage.this, NotesActivity.class));
    }

    public void imagePageCerter()
    {
        startActivity(new Intent(Centerpage.this, ImageToPdf.class));
    }

}