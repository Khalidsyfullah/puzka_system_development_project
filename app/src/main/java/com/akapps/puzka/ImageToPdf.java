package com.akapps.puzka;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class ImageToPdf extends AppCompatActivity {
    File file;
    GridView gridView ;
    boolean ff = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_pdf);
        file = new File(getExternalFilesDir(null), "PdfFiles");
        if(!file.exists()){
            ff = file.mkdirs();
        }
        gridView = findViewById(R.id.grid_tou);
        File[] files = file.listFiles();
        if(files != null && file.length() > 0){

        }

    }

    class GridPdfList extends BaseAdapter{
        File[] file;

        public GridPdfList(File[] file) {
            this.file = file;
        }

        @Override
        public int getCount() {
            return file.length;
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
            textView.setText(file[position].getName());
            try{
                //File file1 = new File(file, "")
            }catch (Exception ignored){

            }
            return view;
        }
    }


    public void onCameraClicked(View view){
        startActivity(new Intent(ImageToPdf.this, CameraActivity.class));
    }
}