package com.akapps.puzka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class StartPage extends AppCompatActivity {
    int position = 0;
    String[] strings = new String[]{"Welcome!", "Notebook", "Diary", "Drawing", "Transactions", "Routine"};
    String[] details = new String[]{"It's me, Puzka!\nI'm here to assist you........",
                    "Create & Manage your notes here!", "Share & Save your Daily important Memories Here!",
                    "Create your Beautiful Handmade Drawing!", "Maintain your Financial Transactions from Here!",
                    "Maintain your Daily Routines, Set Reminder from here!"};
    int[] ids = {R.drawable.puzkadd, R.drawable.notebook, R.drawable.diary, R.drawable.crayons,
                R.drawable.transactions, R.drawable.assistance};
    FirebaseAuth mAuth;
    Button next_btn;
    ImageView imageView;
    TextView title, notes;
    ConstraintLayout constraintLayout;
    View[] views = new View[6];
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Material2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        next_btn = findViewById(R.id.button37);
        imageView = findViewById(R.id.imageView7);
        title = findViewById(R.id.textView130);
        notes = findViewById(R.id.textView131);
        constraintLayout = findViewById(R.id.consfft);
        views[0] = findViewById(R.id.view13);
        views[1] = findViewById(R.id.view14);
        views[2] = findViewById(R.id.view15);
        views[3] = findViewById(R.id.view16);
        views[4] = findViewById(R.id.view17);
        views[5] = findViewById(R.id.view18);
        mAuth = FirebaseAuth.getInstance();
        setVal();

        next_btn.setOnClickListener(v -> {
            position++;
            if(position >= 6){
                if(mAuth.getCurrentUser() != null){
                    startActivity(new Intent(StartPage.this, AuthenticatorActivity.class));
                }
                else{
                    startActivity(new Intent(StartPage.this, GoogleSignIn.class));
                }
                finish();
            }
            else{
                setVal();
            }
        });

        constraintLayout.setOnTouchListener(new OnSwipeTouchListener(StartPage.this){
            @Override
            public void onSwipeLeft() {
                try {
                    position++;
                    if(position >= 6){
                        if(mAuth.getCurrentUser() != null){
                            startActivity(new Intent(StartPage.this, AuthenticatorActivity.class));
                        }
                        else{
                            startActivity(new Intent(StartPage.this, GoogleSignIn.class));
                        }
                        finish();
                    }
                    else{
                        setVal();
                    }
                }catch (Exception ignored){

                }
            }

            @Override
            public void onSwipeRight() {
                try {
                    position--;
                    if(position < 0){
                        position = 0;
                    }
                    else{
                        setVal();
                    }
                }catch (Exception ignored){

                }
            }
        });


    }

    void setVal()
    {
        views[position].setBackgroundResource(R.drawable.g9);
        if(position< 5) views[position+1].setBackgroundResource(R.drawable.g8);
        imageView.setBackgroundResource(ids[position]);
        title.setText(strings[position]);
        notes.setText(details[position]);
    }
}