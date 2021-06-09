package com.akapps.puzka;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.securepreferences.SecurePreferences;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    boolean firstLaunch = false;
    public final static String DATABASE_NAME = "myDataBase";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = new SecurePreferences(MainActivity.this);
        firstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true);

        if(firstLaunch){
            createAllDatabase();
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply();
            new Handler(Looper.getMainLooper()).postDelayed(() -> startActivity(new Intent(MainActivity.this, Centerpage.class)), 5000);
        }
        else{
            startActivity(new Intent(MainActivity.this, Centerpage.class));
        }


    }

    void createAllDatabase()
    {
        final String TABLE_NAME1 = "ColorTable";
        final String TABLE_NAME2 = "ChipTable";
        final String TABLE_NAME3 = "DocumentTable";
        final String TABLE_NAME4 = "DiaryTable";
        SQLiteDatabase db = MainActivity.this.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        String COMMAND_TO_CREATE1 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME1 + "(ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, COLOR_CODE TEXT)";
        String COMMAND_TO_CREATE2 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME2 + "(ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, CHIP_NAME TEXT)";
        String COMMAND_TO_CREATE3 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME3 + "(ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, Type INTEGER, Label INTEGER, Title TEXT, Body TEXT, Preview TEXT)";
        String COMMAND_TO_CREATE4 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME4 + "(ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, YEAR INTEGER, MONTH INTEGER, DAY INTEGER, Body TEXT, Title TEXT)";

        db.execSQL(COMMAND_TO_CREATE1);
        db.execSQL(COMMAND_TO_CREATE2);
        db.execSQL(COMMAND_TO_CREATE3);
        db.execSQL(COMMAND_TO_CREATE4);

        String[] strings = new String[] {"#FFFFFF", "#000000", "#123456", "#A34678", "#DFADC2", "#990099", "#003456",
                "#667788", "#990099", "#DDDDDD"};
        String[] chips = new String[] {"All", "Important", "Work"};

        for (String string : strings) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("COLOR_CODE", string);
            db.insert(TABLE_NAME1, null, contentValues);
        }

        for (String string : chips) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("CHIP_NAME", string);
            db.insert(TABLE_NAME2, null, contentValues);
        }

        db.close();
    }



}