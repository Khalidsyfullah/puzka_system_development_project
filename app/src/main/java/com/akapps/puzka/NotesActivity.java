package com.akapps.puzka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class NotesActivity extends AppCompatActivity {
    ArrayList<TextValsEdit> arrayList = new ArrayList<>();
    final String TABLE_NAME = "NotesTable";
    GridView gridView;
    TextInputLayout textInputLayout;
    Button sent_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Material1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        gridView = findViewById(R.id.grod);

        getList();

        textInputLayout = findViewById(R.id.outlinedTextField2);
        sent_btn = findViewById(R.id.button33);

        sent_btn.setOnClickListener(v -> {
            String text = Objects.requireNonNull(textInputLayout.getEditText()).getText().toString();
            if(text.isEmpty()){
                textInputLayout.setError("Please Enter Some Notes!");
                return;
            }
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String dr = formatter.format(date);
            SQLiteDatabase db = NotesActivity.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Date", dr);
            contentValues.put("Notes", text);
            db.insert(TABLE_NAME, null, contentValues);
            Toast.makeText(NotesActivity.this, "Successfully Added!", Toast.LENGTH_SHORT).show();
            getList();
        });
    }

    void getList()
    {
        if(arrayList.size() > 0){
            arrayList.clear();
        }
        SQLiteDatabase db = NotesActivity.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
        String COMM1 = "SELECT COUNT(*) FROM "+TABLE_NAME;
        String COMM2 = "SELECT * FROM "+TABLE_NAME;
        Cursor cursor = db.rawQuery(COMM1, null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount > 0){
            cursor = db.rawQuery(COMM2, null);
            int id_index = cursor.getColumnIndex("ID");
            int date_index = cursor.getColumnIndex("Date");
            int note_index = cursor.getColumnIndex("Notes");
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                arrayList.add(new TextValsEdit(cursor.getInt(id_index), cursor.getString(date_index),
                        cursor.getString(note_index)));
                cursor.moveToNext();
            }
        }

        gridView.setAdapter(new GridVaf());

        cursor.close();
        db.close();
    }


    class GridVaf extends BaseAdapter{

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater().inflate(R.layout.grid_showtextvals, null);
            TextView tx1, tx2;
            tx1 = view.findViewById(R.id.textView123);
            tx2 = view.findViewById(R.id.textView124);

            tx2.setText(arrayList.get(position).getDates());
            tx1.setText(arrayList.get(position).getVal());

            view.setOnLongClickListener(v -> {
                PopupUpdateorEdit popupUpdateorEdit = new PopupUpdateorEdit(NotesActivity.this,
                        arrayList.get(position));
                popupUpdateorEdit.show();
                return true;
            });
            return view;
        }
    }


    class PopupUpdateorEdit extends Dialog{
        TextValsEdit textValsEdit;
        public PopupUpdateorEdit(@NonNull Context context, TextValsEdit textValsEdit) {
            super(context);
            this.textValsEdit = textValsEdit;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_edittextval);

            TextView textView = findViewById(R.id.textView6);
            String str = "Modify";
            textView.setText(str);
            TextView cancel;
            EditText main;
            Button update, delete;
            this.setCancelable(false);
            cancel = findViewById(R.id.textView29);
            main = findViewById(R.id.textView41);
            cancel.setOnClickListener(v -> this.dismiss());

            main.setText(textValsEdit.getVal());

            update = findViewById(R.id.button17);
            delete = findViewById(R.id.button15);


            update.setOnClickListener(v -> {
                String string = main.getText().toString();

                if(string.isEmpty()){
                    main.setError("Required!");
                    return;
                }
                this.dismiss();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String text = formatter.format(date);
                SQLiteDatabase db = NotesActivity.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                String COMM1 = "UPDATE "+TABLE_NAME+ " SET Date = '"+ text+"' , Notes = '"+ string+ "' WHERE ID = "+
                        textValsEdit.getId();
                db.execSQL(COMM1);
                Toast.makeText(NotesActivity.this, "Successfully Updated!", Toast.LENGTH_SHORT).show();
                getList();
            });

            delete.setOnClickListener(v -> {
                SQLiteDatabase db = NotesActivity.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                String COMM1 = "DELETE FROM "+TABLE_NAME+ " WHERE ID = "+ textValsEdit.getId();
                db.execSQL(COMM1);
                Toast.makeText(NotesActivity.this, "Successfully Deleted!", Toast.LENGTH_SHORT).show();
                getList();
                this.dismiss();
            });


        }
    }

}