package com.akapps.puzka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

public class MyNotebook extends AppCompatActivity {
    TextView back_txt, delete_txt, save_txt, title_text;
    EditText note_title_text, body_text;
    SharedPreferences sharedPreferences;
    int id = -1;
    boolean isNew = false;
    DiaryClass diaryClass1;
    String[] months = new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    int day = -1, month = -1, year = -1;
    boolean deleteUnlocked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notebook);
        back_txt = findViewById(R.id.textView45);
        title_text = findViewById(R.id.textView44);
        delete_txt = findViewById(R.id.textView47);
        save_txt = findViewById(R.id.textView46);
        note_title_text = findViewById(R.id.title2);
        body_text = findViewById(R.id.edit_story);

        sharedPreferences = new SecurePreferences(MyNotebook.this);
        isNew = sharedPreferences.getBoolean("diaryNew", true);
        sharedPreferences.edit().putBoolean("diaryNew", true).apply();

        if(!isNew){
            id = sharedPreferences.getInt("diaryIDf", -1);
            sharedPreferences.edit().putInt("diaryIDf", -1).apply();
            if(id == -1){
                isNew = true;
                getNewTabl();
            }
            else{
                try {
                    SQLiteDatabase db = MyNotebook.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                    String TABLE_NAME = "DiaryTable";
                    String COMM = "SELECT * FROM "+ TABLE_NAME + " WHERE ID = "+id;
                    Cursor cursor = db.rawQuery(COMM, null);
                    int mon_index = cursor.getColumnIndex("MONTH");
                    int year_index = cursor.getColumnIndex("YEAR");
                    int day_index = cursor.getColumnIndex("DAY");
                    int id_index = cursor.getColumnIndex("ID");
                    int body_index = cursor.getColumnIndex("Body");
                    int title_index = cursor.getColumnIndex("Title");
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()){
                        if(cursor.getInt(id_index) == id){
                            diaryClass1 = new DiaryClass(cursor.getInt(year_index),
                                    cursor.getInt(mon_index),
                                    cursor.getInt(day_index),
                                    cursor.getInt(id_index),
                                    cursor.getString(title_index),
                                    cursor.getString(body_index));
                            getValProcessing(diaryClass1);
                            break;
                        }

                        cursor.moveToNext();
                    }
                    if(diaryClass1 == null){
                        save_txt.setEnabled(false);
                        delete_txt.setEnabled(false);
                        Toast.makeText(MyNotebook.this, "Data Not Found!", Toast.LENGTH_LONG).show();
                    }
                    cursor.close();
                    db.close();
                }catch (Exception e){
                    save_txt.setEnabled(false);
                    delete_txt.setEnabled(false);
                    Toast.makeText(MyNotebook.this, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        else{
            getNewTabl();
        }


        back_txt.setOnClickListener(v -> {
            PopupAlertDialog popupAlertDialog = new PopupAlertDialog(MyNotebook.this);
            popupAlertDialog.show();
        });


        save_txt.setOnClickListener(v -> {
            String t1 = note_title_text.getText().toString();
            if(t1.isEmpty()){
                note_title_text.setError("Enter a Title!");
                return;
            }
            String t2 = body_text.getText().toString();
            if(t2.isEmpty()){
                body_text.setError("Enter Note!");
                return;
            }

            PopupConfirmSave popupConfirmSave = new PopupConfirmSave(MyNotebook.this, t1, t2);
            popupConfirmSave.show();

        });

        delete_txt.setOnClickListener(v -> {
            deleteUnlocked = true;
            Toast.makeText(MyNotebook.this, "Hold To Delete!", Toast.LENGTH_LONG).show();
        });

        delete_txt.setOnLongClickListener(v -> {
            if(!deleteUnlocked){
                deleteUnlocked = true;
                Toast.makeText(MyNotebook.this, "Hold Again To Delete!", Toast.LENGTH_LONG).show();
            }
            else{
                try {
                    SQLiteDatabase db = MyNotebook.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                    String COMM = "DELETE FROM DiaryTable WHERE ID = "+ id;
                    db.execSQL(COMM);
                    Toast.makeText(MyNotebook.this, "Successfully Deleted!", Toast.LENGTH_LONG).show();
                    finish();
                }catch (Exception e){
                    Toast.makeText(MyNotebook.this, "Error! "+ e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        PopupAlertDialog popupAlertDialog = new PopupAlertDialog(MyNotebook.this);
        popupAlertDialog.show();
    }


    void getValProcessing(DiaryClass dc)
    {
        day = dc.getDay();
        month = dc.getMonth();
        year = dc.getYear();
        delete_txt.setVisibility(View.VISIBLE);
        String date_buid = dc.getDay() +" "+  months[dc.getMonth()] + ", "+ dc.getYear();
        title_text.setText(date_buid);
        note_title_text.setText(dc.getTitle());
        body_text.setText(dc.getBody());
    }

    void getNewTabl()
    {
        day = sharedPreferences.getInt("Daydd", -1);
        month = sharedPreferences.getInt("Monthdd", -1);
        year = sharedPreferences.getInt("Yeardd", -1);
        sharedPreferences.edit().putInt("Daydd", -1).apply();
        sharedPreferences.edit().putInt("Monthdd", -1).apply();
        sharedPreferences.edit().putInt("Yeardd", -1).apply();

        if(day == -1 || month == -1 || year == -1){
            Toast.makeText(MyNotebook.this, "Something Goes Wrong! Please Restart!", Toast.LENGTH_LONG).show();
            return;
        }
        String date_buid = day +" "+  months[month] + ", "+ year;
        title_text.setText(date_buid);

    }


    class PopupConfirmSave extends Dialog {
        Button bt1, bt2;
        String tex1, tex2;
        TextView cancel_txt;
        public PopupConfirmSave(@NonNull Context context, String tex1, String tex2) {
            super(context);
            this.tex1 = tex1;
            this.tex2 = tex2;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_confirmsave);
            bt1 = findViewById(R.id.button17);
            bt2 = findViewById(R.id.button15);
            cancel_txt = findViewById(R.id.textView29);

            this.setCancelable(false);
            bt2.setOnClickListener(v -> this.dismiss());
            cancel_txt.setOnClickListener(v -> this.dismiss());
            bt1.setOnClickListener(v -> {
                this.dismiss();

                if(isNew){
                    try{
                        SQLiteDatabase db = MyNotebook.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("DAY", day);
                        contentValues.put("MONTH", month);
                        contentValues.put("YEAR", year);
                        contentValues.put("Body", tex2);
                        contentValues.put("Title", tex1);
                        db.insert("DiaryTable",null,  contentValues);
                        Toast.makeText(MyNotebook.this, "Successfully Saved!", Toast.LENGTH_LONG).show();
                        save_txt.setEnabled(false);
                        finish();
                    }catch (Exception e){
                        Toast.makeText(MyNotebook.this, "Error!"+ e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    if(id == -1) return;

                    try{
                        SQLiteDatabase db = MyNotebook.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                        String UPDATE_COMM = "UPDATE DiaryTable SET Title = '"+tex1+ "' , Body = '"+ tex2 + "' WHERE ID = "+id;
                        db.execSQL(UPDATE_COMM);
                        Toast.makeText(MyNotebook.this, "Successfully Updated!", Toast.LENGTH_LONG).show();
                        save_txt.setEnabled(false);
                        finish();
                    }catch (Exception e){
                        Toast.makeText(MyNotebook.this, "Error!"+ e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}