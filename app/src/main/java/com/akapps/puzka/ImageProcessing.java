package com.akapps.puzka;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;
import com.securepreferences.SecurePreferences;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageProcessing extends AppCompatActivity {
    CropImageView cropImageView;
    SharedPreferences sharedPreferences;
    String filename = "";
    Bitmap bitmap;
    TextView save_txt, text_text;
    Uri vuri;
    boolean isEdit = false;
    String foldername = "";
    String saved_filename = "";
    File file;
    boolean fff = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);
        cropImageView = findViewById(R.id.cropImageView);
        save_txt = findViewById(R.id.textView4);
        text_text = findViewById(R.id.textView135);
        text_text.setVisibility(View.GONE);
        sharedPreferences = new SecurePreferences(this);
        filename = sharedPreferences.getString("Imagename", "no");
        sharedPreferences.edit().putString("Imagename", "no").apply();
        saved_filename = sharedPreferences.getString("Filenametooperate", "");
        foldername = sharedPreferences.getString("Foldenametooperate", "");

        if(filename.equals("no")){
            isEdit = true;
            String buil = "PdfFiles/"+foldername+"/"+ saved_filename;
            file = new File(getExternalFilesDir(null), buil);
            vuri = Uri.fromFile(file);
        }
        else{
            file = new File(getExternalFilesDir(null), filename);
            vuri = Uri.fromFile(file);
        }

        cropImageView.setImageUriAsync(vuri);
        cropImageView.setOnCropImageCompleteListener((view, result) -> cropImageView.setImageBitmap(result.getBitmap()));

        save_txt.setOnClickListener(v -> {
            try {
                bitmap = cropImageView.getCroppedImage();
                String buil = "PdfFiles/"+foldername+"/"+ saved_filename;
                File file1 = new File(getExternalFilesDir(null), buil);
                if(!isEdit){
                    fff = file.delete();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, fileOutputStream);
                startActivity(new Intent(ImageProcessing.this, PdfAllVal.class));
                finish();

            } catch (Exception e) {
                Toast.makeText(ImageProcessing.this, "Error!"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onRotateClicked(View view) {
        cropImageView.rotateImage(90);
    }

    public void onFliphorizontallyClicked(View view) {
        cropImageView.flipImageHorizontally();
    }

    public void onFlipVerticallyClicked(View view) {
        cropImageView.flipImageVertically();
    }

    public void onCheckedClicked(View view) {
        cropImageView.getCroppedImageAsync();
    }

    public void onOcrClicked(View view) {
        if(text_text.getVisibility() == View.VISIBLE){
            text_text.setVisibility(View.GONE);
            return;
        }
        InputImage image;
        try {
            image = InputImage.fromFilePath(ImageProcessing.this, vuri);
        } catch (IOException e) {
            Toast.makeText(ImageProcessing.this, "Error!"+e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        Task<Text> result = recognizer.process(image).addOnSuccessListener(this::processText)
                        .addOnFailureListener(e -> Toast.makeText(ImageProcessing.this,
                                "Error!"+e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    private void processText(Text firebaseVisionText) {
        if (firebaseVisionText.getTextBlocks().isEmpty()) {
            Toast.makeText(ImageProcessing.this, "No text found or Text may not be clear", Toast.LENGTH_LONG).show();
        } else {
            StringBuilder text = new StringBuilder();
            for (Text.TextBlock block : firebaseVisionText.getTextBlocks()) {
                text.append(block.getText()).append(" ");
            }
            text_text.setText(text);
            text_text.setVisibility(View.VISIBLE);
            Toast.makeText(ImageProcessing.this, "Successfully Extracted!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(ImageProcessing.this)
                .setMessage("You will Lose All Saved progress, Continue?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    startActivity(new Intent(ImageProcessing.this, PdfAllVal.class));
                    ImageProcessing.this.finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()).create();

        alertDialog.show();
    }


}