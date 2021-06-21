package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.securepreferences.SecurePreferences;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PdfAllVal extends AppCompatActivity {
    GridView gridView;
    SharedPreferences sharedPreferences;
    String foldername = "";
    File file;
    int count = 0;
    TextView textView;
    ActivityResultLauncher<Intent> resultLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_all_val);
        textView = findViewById(R.id.textView138);
        gridView = findViewById(R.id.grid_fgh);
        sharedPreferences = new SecurePreferences(this);
        foldername = sharedPreferences.getString("filenameList", "pdf0");
        file = new File(getExternalFilesDir(null), "PdfFiles/"+foldername);
        File[] listFile = file.listFiles();
        if(listFile!= null && listFile.length > 0){
            count = listFile.length;
            gridView.setAdapter(new GridVad(listFile));
        }

         resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData()!= null){
                        Uri selectedImageUri = result.getData().getData();

                        sharedPreferences.edit().putString("Foldenametooperate", foldername).apply();
                        String name = "img"+ count+ ".jpeg";
                        sharedPreferences.edit().putString("Filenametooperate", name).apply();


                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            String filename = new SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.US).format(System.currentTimeMillis()) + ".jpeg";
                            File filennm = new File(getExternalFilesDir(null), filename);
                            FileOutputStream fileOutputStream;
                            sharedPreferences.edit().putString("Imagename", filename).apply();
                            fileOutputStream = new FileOutputStream(filennm);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                startActivity(new Intent(PdfAllVal.this, ImageProcessing.class));
                                finish();
                            }, 100);

                        } catch (Exception e) {
                            Toast.makeText(PdfAllVal.this, "Error!"+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(PdfAllVal.this, "You haven't Selected any image!", Toast.LENGTH_LONG).show();
                    }
                }
        );


        textView.setOnClickListener(v -> {
            File[] listfiles = file.listFiles();
            if(listfiles == null || listfiles.length == 0){
                Toast.makeText(PdfAllVal.this, "No Image Found!", Toast.LENGTH_LONG).show();
                return;
            }
            File mainDoc = new File(getExternalFilesDir(null), "mainPdf.pdf");
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(mainDoc));
                document.open();
                for(File f: listfiles){
                    document.newPage();
                    Image image = Image.getInstance(f.getAbsolutePath());
                    image.setAbsolutePosition(0, 0);
                    image.setBorderWidth(0);
                    image.scaleAbsolute(PageSize.A4.getWidth(), PageSize.A4.getHeight());
                    document.add(image);
                }
                document.close();

                Uri path = Uri.fromFile(mainDoc);
                Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                pdfOpenintent.setDataAndType(path, "application/pdf");
                startActivity(pdfOpenintent);
                Toast.makeText(PdfAllVal.this, "Successfully Created!", Toast.LENGTH_LONG).show();
            }catch (Exception e){
                Toast.makeText(PdfAllVal.this, "Error!"+e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

    class GridVad extends BaseAdapter{
        File[] fileList;

        public GridVad(File[] fileList) {
            this.fileList = fileList;
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
            textView.setVisibility(View.GONE);
            try{
                File file1 = new File(file, fileList[position].getName());
                Bitmap myBitmap = BitmapFactory.decodeFile(file1.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }catch (Exception ignored){

            }

            imageView.setOnClickListener(v -> {
                sharedPreferences.edit().putString("Foldenametooperate", foldername).apply();
                sharedPreferences.edit().putString("Filenametooperate", fileList[position].getName()).apply();
                sharedPreferences.edit().putString("Imagename", "no").apply();
                startActivity(new Intent(PdfAllVal.this, ImageProcessing.class));
                finish();
            });

            return view;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PdfAllVal.this, ImageToPdf.class));
        finish();
    }

    public void onAddImageClicked(View view) {
        sharedPreferences.edit().putString("Foldenametooperate", foldername).apply();
        String name = "img"+ count+ ".jpeg";
        sharedPreferences.edit().putString("Filenametooperate", name).apply();
        startActivity(new Intent(PdfAllVal.this, CameraActivity.class));
        finish();
    }

    @SuppressLint("IntentReset")
    public void onAddFromGalary(View view) {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        resultLauncher.launch(galleryIntent);

    }
}