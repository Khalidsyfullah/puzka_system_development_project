package com.akapps.puzka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class DiaryActivity extends AppCompatActivity {
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Material);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        tabLayout = findViewById(R.id.tabLay);
        getSupportFragmentManager().beginTransaction().add(R.id.frsame, new RegularCalendar()).commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    getSupportFragmentManager().beginTransaction().replace(R.id.frsame, new RegularCalendar()).commit();
                }
                else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frsame, new RegularDiary()).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    public void change_Activity()
    {
        startActivity(new Intent(DiaryActivity.this, MyNotebook.class));
    }
}