package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class LinksActivity extends AppCompatActivity {
    ArrayList<TextValsEdit> arrayList = new ArrayList<>();
    final String TABLE_NAME = "LinksTable";
    GridView gridView;
    TextInputLayout textInputLayout;
    Button sent_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Material1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_links);

        textInputLayout = findViewById(R.id.outlinedTextField2);
        sent_btn = findViewById(R.id.button33);
        gridView = findViewById(R.id.grod_val);

        sent_btn.setOnClickListener(v -> {
            String text = Objects.requireNonNull(textInputLayout.getEditText()).getText().toString();
            if(text.isEmpty()){
                textInputLayout.setError("Please Enter Some Notes!");
                return;
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String dr = formatter.format(date);
            SQLiteDatabase db = LinksActivity.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Date", dr);
            contentValues.put("Links", text);
            db.insert(TABLE_NAME, null, contentValues);
            Toast.makeText(LinksActivity.this, "Successfully Added!", Toast.LENGTH_SHORT).show();
            getList();
        });


        getList();
    }


    void getList()
    {
        if(arrayList.size() > 0){
            arrayList.clear();
        }
        SQLiteDatabase db = LinksActivity.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
        String COMM1 = "SELECT COUNT(*) FROM "+TABLE_NAME;
        String COMM2 = "SELECT * FROM "+TABLE_NAME;
        Cursor cursor = db.rawQuery(COMM1, null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if(icount > 0){
            cursor = db.rawQuery(COMM2, null);
            int id_index = cursor.getColumnIndex("ID");
            int date_index = cursor.getColumnIndex("Date");
            int note_index = cursor.getColumnIndex("Links");
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                arrayList.add(new TextValsEdit(cursor.getInt(id_index), cursor.getString(date_index),
                        cursor.getString(note_index)));
                cursor.moveToNext();
            }
        }

        gridView.setAdapter(new GridVafFF());

        cursor.close();
        db.close();
    }


    class GridVafFF extends BaseAdapter {

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
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater().inflate(R.layout.grid_showlinkvals, null);
            TextView tx1, tx2;

            tx1 = view.findViewById(R.id.textView125);
            tx2 = view.findViewById(R.id.textView126);

            tx1.setOnClickListener(v -> {
                Uri uri;
                try {
                    uri = Uri.parse(arrayList.get(position).getVal());
                }catch (Exception ignored){
                    Toast.makeText(LinksActivity.this, "Error Parsing Link!", Toast.LENGTH_SHORT).show();
                    return;
                }

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setPackage("com.android.chrome");
                customTabsIntent.launchUrl(LinksActivity.this, uri);
            });

            tx1.setOnLongClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", tx1.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(LinksActivity.this, "Copied On Clipboard!", Toast.LENGTH_SHORT).show();
                return true;
            });

            tx2.setText(arrayList.get(position).getDates());
            tx1.setText(arrayList.get(position).getVal());

            view.setOnLongClickListener(v -> {
                PopupUpdateLinks popupUpdateLinks = new PopupUpdateLinks(LinksActivity.this,
                        arrayList.get(position));
                popupUpdateLinks.show();
                return true;
            });
            return view;
        }
    }


    class PopupUpdateLinks extends Dialog {
        TextValsEdit textValsEdit;
        public PopupUpdateLinks(@NonNull Context context, TextValsEdit textValsEdit) {
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
                SQLiteDatabase db = LinksActivity.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                String COMM1 = "UPDATE "+TABLE_NAME+ " SET Date = '"+ text+"' , Links = '"+ string+ "' WHERE ID = "+
                        textValsEdit.getId();
                db.execSQL(COMM1);
                Toast.makeText(LinksActivity.this, "Successfully Updated!", Toast.LENGTH_SHORT).show();
                getList();
            });

            delete.setOnClickListener(v -> {
                SQLiteDatabase db = LinksActivity.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
                String COMM1 = "DELETE FROM "+TABLE_NAME+ " WHERE ID = "+ textValsEdit.getId();
                db.execSQL(COMM1);
                Toast.makeText(LinksActivity.this, "Successfully Deleted!", Toast.LENGTH_SHORT).show();
                getList();
                this.dismiss();
            });


        }
    }
}