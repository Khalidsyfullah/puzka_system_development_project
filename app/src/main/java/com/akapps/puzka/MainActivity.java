package com.akapps.puzka;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.securepreferences.SecurePreferences;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    boolean firstLaunch = false;
    public final static String DATABASE_NAME = "myDataBase";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        sharedPreferences = new SecurePreferences(MainActivity.this);
        firstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true);
        if(firstLaunch){
            createAllDatabase();
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startActivity(new Intent(MainActivity.this, StartPage.class));
                finish();
            }, 4000);
        }
        else{
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startActivity(new Intent(MainActivity.this, StartPage.class));
                finish();
            }, 1500);


        }


    }

    void createAllDatabase()
    {
        final String TABLE_NAME1 = "ColorTable";
        final String TABLE_NAME2 = "ChipTable";
        final String TABLE_NAME3 = "DocumentTable";
        final String TABLE_NAME4 = "DiaryTable";
        final String TABLE_NAME5 = "DrawableTable";
        final String TABLE_NAME6 = "AccountTable";
        final String TABLE_NAME7 = "TransactionTable";
        final String TABLE_NAME8 = "DailyRoutineTable";
        final String TABLE_NAME9 = "WeeklyRoutineTable";
        final String TABLE_NAME10 = "NotesTable";
        final String TABLE_NAME11 = "LinksTable";

        SQLiteDatabase db = MainActivity.this.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        String COMMAND_TO_CREATE1 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME1 + "(ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, COLOR_CODE TEXT)";
        String COMMAND_TO_CREATE2 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME2 + "(ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, CHIP_NAME TEXT)";
        String COMMAND_TO_CREATE3 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME3 + "(ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, Type INTEGER, Label INTEGER, Title TEXT, Body TEXT, Preview TEXT)";
        String COMMAND_TO_CREATE4 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME4 + "(ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, YEAR INTEGER, MONTH INTEGER, DAY INTEGER, Body TEXT, Title TEXT)";
        String COMMAND_TO_CREATE5 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME5 + "(ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, Bitmap TEXT, UndoBit TEXT, MainBit TEXT, Name TEXT)";
        String COMMAND_TO_CREATE6 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME6 + "(ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, Name TEXT, Type TEXT, Balance REAL)";
        String COMMAND_TO_CREATE7 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME7 + "(ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, Date TEXT, Account TEXT, Notes TEXT, Amount REAL, Echarges REAL, Type INTEGER)";
        String COMMAND_TO_CREATE8 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME8 + "(Day INTEGER, Month INTEGER, " +
                "Year INTEGER, Routine TEXT, PRIMARY KEY (Day, Month, Year))";
        String COMMAND_TO_CREATE9 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME9 + "(ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, Value TEXT)";
        String COMMAND_TO_CREATE10 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME10 + "(ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, Date TEXT, Notes TEXT)";
        String COMMAND_TO_CREATE11 = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME11 + "(ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT, Date TEXT, Links TEXT)";

        db.execSQL(COMMAND_TO_CREATE1);
        db.execSQL(COMMAND_TO_CREATE2);
        db.execSQL(COMMAND_TO_CREATE3);
        db.execSQL(COMMAND_TO_CREATE4);
        db.execSQL(COMMAND_TO_CREATE5);
        db.execSQL(COMMAND_TO_CREATE6);
        db.execSQL(COMMAND_TO_CREATE7);
        db.execSQL(COMMAND_TO_CREATE8);
        db.execSQL(COMMAND_TO_CREATE9);
        db.execSQL(COMMAND_TO_CREATE10);
        db.execSQL(COMMAND_TO_CREATE11);

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

        ContentValues contentValues = new ContentValues();
        contentValues.put("Name", "My Account");
        contentValues.put("Type", "Cash-Account");
        contentValues.put("Balance", 0.0);

        db.insert(TABLE_NAME6, null, contentValues);

        for(int i=0; i<7; i++){
            ContentValues cv = new ContentValues();
            cv.put("Value", "nil");
            db.insert(TABLE_NAME9, null, cv);
        }


        db.close();
    }



}