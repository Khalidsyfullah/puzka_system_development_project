package com.akapps.puzka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.securepreferences.SecurePreferences;

import java.io.File;

public class ImageToPdf extends AppCompatActivity {
    File file;
    GridView gridView ;
    boolean ff = false;
    SharedPreferences sharedPreferences;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_pdf);
        file = new File(getExternalFilesDir(null), "PdfFiles");
        if(!file.exists()){
            ff = file.mkdirs();
        }
        sharedPreferences = new SecurePreferences(this);
        gridView = findViewById(R.id.grid_tou);
        File[] files = file.listFiles();
        if(files != null && file.length() > 0){
            gridView.setAdapter(new GridPdfList(files));
            count = files.length;
        }

        if (ContextCompat.checkSelfPermission(ImageToPdf.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ImageToPdf.this, new String[] {Manifest.permission.CAMERA}, 101);
        }
    }

    class GridPdfList extends BaseAdapter{
        File[] fileList;

        public GridPdfList(File[] file) {
            this.fileList = file;
        }

        @Override
        public int getCount() {
            return fileList.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater().inflate(R.layout.grid_pdfvals, null);
            ImageView imageView = view.findViewById(R.id.imageView3);
            TextView textView = view.findViewById(R.id.textView127);
            String filername = fileList[position].getName();
            textView.setText(filername);
            try{
                File file1 = new File(file, filername+"/img0.jpeg");
                Bitmap myBitmap = BitmapFactory.decodeFile(file1.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }catch (Exception ignored){

            }
            view.setOnClickListener(v -> {
                sharedPreferences.edit().putString("filenameList", fileList[position].getName()).apply();
                startActivity(new Intent(ImageToPdf.this, PdfAllVal.class));
                finish();
            });

            return view;
        }
    }


    public void onCameraClicked(View view){
        String str = "pdf"+ count;
        sharedPreferences.edit().putString("filenameList", str).apply();
        File file2 = new File(file, str);
        if(!file2.exists()){
            ff = file2.mkdirs();
        }
        startActivity(new Intent(ImageToPdf.this, PdfAllVal.class));
        finish();
    }
}