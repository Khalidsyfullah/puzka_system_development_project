package com.akapps.puzka;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.securepreferences.SecurePreferences;

import java.util.ArrayList;

public class DrawingCenterpage extends AppCompatActivity {
    GridView gridView;
    FloatingActionButton fab;
    TextView backButton, sync_text;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Material);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_centerpage);
        sharedPreferences = new SecurePreferences(this);
        backButton = findViewById(R.id.textView67);
        gridView = findViewById(R.id.grid_draw);
        fab = findViewById(R.id.floatingActionButton4);
        sync_text = findViewById(R.id.textView71);
        updateDrawableList();
        sync_text.setOnClickListener(v -> updateDrawableList());
        backButton.setOnClickListener(v -> finish());
        fab.setOnClickListener(v -> startActivity(new Intent(DrawingCenterpage.this, DrawingActivity.class)));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    void updateDrawableList()
    {
        try {
            ArrayList<DrawableSClass> list = new ArrayList<>();
            SQLiteDatabase myDatabase = DrawingCenterpage.this.openOrCreateDatabase("myDataBase", Context.MODE_PRIVATE, null);
            String TABLE_NAME = "DrawableTable";
            String COMM1 = "SELECT COUNT(*) FROM "+ TABLE_NAME;
            String COMM2 = "SELECT * FROM "+ TABLE_NAME;
            Cursor cursor = myDatabase.rawQuery(COMM1, null);
            cursor.moveToFirst();
            int cou = cursor.getInt(0);

            if(cou > 0){
                cursor = myDatabase.rawQuery(COMM2 , null);
                int id_index = cursor.getColumnIndex("ID");
                int bitmap_index = cursor.getColumnIndex("Bitmap");
                int undo_index = cursor.getColumnIndex("UndoBit");
                int main_index = cursor.getColumnIndex("MainBit");
                int name_index = cursor.getColumnIndex("Name");
                cursor.moveToFirst();

                while (!cursor.isAfterLast()){
                    DrawableSClass drawableSClass = new DrawableSClass(cursor.getString(bitmap_index),
                            cursor.getString(undo_index),
                            cursor.getString(main_index),
                            cursor.getString(name_index),
                            cursor.getInt(id_index));
                    list.add(drawableSClass);
                    cursor.moveToNext();
                }
                cursor.close();
                myDatabase.close();
            }
            gridView.setAdapter(new CustomSavedDrawable(list));

        }catch (Exception e){
            Toast.makeText(DrawingCenterpage.this, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    class CustomSavedDrawable extends BaseAdapter{
        ArrayList<DrawableSClass> arrayList;

        public CustomSavedDrawable(ArrayList<DrawableSClass> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
            if(arrayList.size() == 0){
                return 1;
            }
            else return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(arrayList.size() == 0){
                return getLayoutInflater().inflate(R.layout.grid_null, null);
            }
            View view = getLayoutInflater().inflate(R.layout.grid_drawvier, null );
            ImageView imageView = view.findViewById(R.id.imageView2);
            TextView textView = view.findViewById(R.id.textView69);

            imageView.setImageBitmap(getBitmapFromString(arrayList.get(position).getMainbitmap()));
            textView.setText(arrayList.get(position).getName());

            view.setOnClickListener(v -> {
                sharedPreferences.edit().putInt("drawableIDF", arrayList.get(position).getId()).apply();
                startActivity(new Intent(DrawingCenterpage.this, DrawingActivity.class));
            });

            return view;
        }
    }

    private Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

}