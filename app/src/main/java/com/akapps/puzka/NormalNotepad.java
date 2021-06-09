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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.securepreferences.SecurePreferences;

import org.jsoup.Jsoup;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class NormalNotepad extends AppCompatActivity {
    EditText title, body;
    TextView save_txt;
    SharedPreferences sharedPreferences;
    boolean isNew = true;
    int id = -1;
    DocumentClass documentClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_notepad);
        title = findViewById(R.id.title);
        body = findViewById(R.id.edit_story);
        save_txt = findViewById(R.id.textView34);
        sharedPreferences = new SecurePreferences(this);

        isNew = sharedPreferences.getBoolean("isNewText", true);
        sharedPreferences.edit().putBoolean("isNewText", true).apply();

        if(!isNew){
            id = sharedPreferences.getInt("idtoSearch", -1);
            sharedPreferences.edit().putInt("idtoSearch", -1).apply();
            if(id != -1){
                try {
                    SQLiteDatabase myDatabase = NormalNotepad.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                    String TABLE_NAME = "DocumentTable";
                    String COMM = "SELECT * FROM "+ TABLE_NAME+ " WHERE ID = "+ id;
                    Cursor cursor = myDatabase.rawQuery(COMM, null);
                    int id_index = cursor.getColumnIndex("ID");
                    int type_index = cursor.getColumnIndex("Type");
                    int label_index = cursor.getColumnIndex("Label");
                    int title_index = cursor.getColumnIndex("Title");
                    int body_index = cursor.getColumnIndex("Body");
                    int prev_index = cursor.getColumnIndex("Preview");
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()){

                        if(cursor.getInt(id_index) == id){
                            documentClass = new DocumentClass(cursor.getInt(id_index),
                                    cursor.getInt(type_index),
                                    cursor.getInt(label_index),
                                    cursor.getString(title_index),
                                    cursor.getString(body_index),
                                    cursor.getString(prev_index));

                            break;
                        }
                        cursor.moveToNext();
                    }
                    foundID();
                    cursor.close();
                    myDatabase.close();

                }catch (Exception e){
                    Toast.makeText(NormalNotepad.this, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            else{
                isNew = true;
            }
        }

        save_txt.setOnClickListener(v -> {
            String t1 = title.getText().toString();
            if(t1.isEmpty()){
                title.setError("Enter a Title!");
                return;
            }
            String t2 = body.getText().toString();
            if(t2.isEmpty()){
                body.setError("Enter Notes!");
                return;
            }
            PopupConfirmSave confirmSave = new PopupConfirmSave(NormalNotepad.this, t1, t2);
            confirmSave.show();
        });

    }


    @Override
    public void onBackPressed() {
        if(!save_txt.isEnabled()){
            finish();
            return;
        }
        PopupAlertDialog popupAlertDialog = new PopupAlertDialog(NormalNotepad.this);
        popupAlertDialog.show();
    }

    void foundID ()
    {
        if(documentClass == null){
            isNew = true;
            return;
        }

        int v_type = documentClass.getType();
        title.setText(documentClass.getText());
        String text = documentClass.getBody();

        if(v_type == 0){
            Type type = new TypeToken<ArrayList<ValueStorer>>(){}.getType();
            ArrayList<ValueStorer> list = new Gson().fromJson(text, type);
            StringBuilder stringBuilder = new StringBuilder();
            for(ValueStorer vs: list){
                stringBuilder.append(vs.getHtml_txt());
            }
            String f_text = stringBuilder.toString().replaceAll("<br>", "\n");
            body.setText(html2text(f_text));
        }
        else{
            body.setText(text);
        }

    }

    class PopupConfirmSave extends Dialog{
        Button bt1, bt2;
        String t1, t2;
        TextView cancel_txt;
        public PopupConfirmSave(@NonNull Context context, String t1, String t2) {
            super(context);
            this.t1 = t1;
            this.t2 = t2;
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

                String prev;
                if(t2.length()< 100){
                    prev = t2;
                }
                else{
                    prev = t2.substring(0, 100).trim();
                }

                if(isNew){
                    int label_ID = sharedPreferences.getInt("labelID", 1);

                    try{
                        SQLiteDatabase db = NormalNotepad.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("Type", 1);
                        contentValues.put("Label", label_ID);
                        contentValues.put("Title", t1);
                        contentValues.put("Body", t2);
                        contentValues.put("Preview", prev);
                        db.insert("DocumentTable",null,  contentValues);
                        Toast.makeText(NormalNotepad.this, "Successfully Saved!", Toast.LENGTH_LONG).show();
                        save_txt.setEnabled(false);
                        title.setEnabled(false);
                        body.setEnabled(false);
                        finish();
                    }catch (Exception e){
                        Toast.makeText(NormalNotepad.this, "Error!"+ e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    try{
                        SQLiteDatabase db = NormalNotepad.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                        String UPDATE_COMM = "UPDATE DocumentTable SET Type = 1, Title = '"+t1+ "', Body = '"+
                                t2 + "', Preview = '"+ prev+ "' WHERE ID = "+id;
                        db.execSQL(UPDATE_COMM);
                        Toast.makeText(NormalNotepad.this, "Successfully Updated!", Toast.LENGTH_LONG).show();
                        save_txt.setEnabled(false);
                        title.setEnabled(false);
                        body.setEnabled(false);
                        finish();
                    }catch (Exception e){
                        Toast.makeText(NormalNotepad.this, "Error!"+ e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

}