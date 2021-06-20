package com.akapps.puzka;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentSettings extends Fragment {
    CircleImageView circleImageView;
    TextInputLayout tx1, tx2, tx3, tx4;
    Button upload_btn, edit, save, cancel;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    StorageReference stRef = FirebaseStorage.getInstance().getReference();
    Bitmap bitmap;
    Context myContext;
    final long FIVE_MEGABYTE = 5 * 1024 * 1024;
    LinearLayout linearLayout;
    @SuppressLint("IntentReset")
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_settings, container, false);
        tx1 = view.findViewById(R.id.outlinedTextField);
        tx2 = view.findViewById(R.id.outlinedTextField1);
        tx3 = view.findViewById(R.id.outlinedTextField2);
        tx4 = view.findViewById(R.id.outlinedTextField3);
        circleImageView = view.findViewById(R.id.profile_image3);
        upload_btn = view.findViewById(R.id.button41);
        edit = view.findViewById(R.id.button42);
        save = view.findViewById(R.id.button43);
        cancel = view.findViewById(R.id.button44);
        linearLayout = view.findViewById(R.id.lin);

        upload_btn.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        tx4.setVisibility(View.GONE);
        tx3.setVisibility(View.GONE);

        tx1.setEnabled(false);
        tx2.setEnabled(false);

        if(mAuth.getCurrentUser() != null){
            stRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).getBytes(FIVE_MEGABYTE)
                    .addOnSuccessListener(bytes -> {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        circleImageView.setImageBitmap(bmp);
                    }).addOnFailureListener(e -> {

            });

            Objects.requireNonNull(tx2.getEditText()).setText(mAuth.getCurrentUser().getEmail());
            myRef.child(mAuth.getCurrentUser().getUid()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if(!snapshot.exists()) return;
                    String str = Objects.requireNonNull(snapshot.getValue()).toString();
                    if(!str.equals("No")){
                        Objects.requireNonNull(tx1.getEditText()).setText(str);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
        else{
            linearLayout.setVisibility(View.GONE);
        }



        ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK && result.getData()!= null){
                        Uri selectedImageUri = result.getData().getData();
                        circleImageView.setImageURI(selectedImageUri);
                        Drawable drawable = circleImageView.getDrawable();
                        bitmap = ((BitmapDrawable) drawable).getBitmap();
                    }
                    else{
                        Toast.makeText(myContext, "You haven't Selected any image!", Toast.LENGTH_LONG).show();
                    }
                }
        );

        upload_btn.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");
            resultLauncher.launch(galleryIntent);
        });

        edit.setOnClickListener(v -> {
            edit.setVisibility(View.GONE);
            upload_btn.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            tx3.setVisibility(View.VISIBLE);
            tx4.setVisibility(View.VISIBLE);
            tx1.setEnabled(true);
        });

        cancel.setOnClickListener(v -> {
            edit.setVisibility(View.VISIBLE);
            upload_btn.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            tx3.setVisibility(View.GONE);
            tx4.setVisibility(View.GONE);
            tx1.setEnabled(false);
        });

        save.setOnClickListener(v -> {
            String str1 = Objects.requireNonNull(tx1.getEditText()).getText().toString();
            if(str1.isEmpty()){
                tx1.setError("Required!");
                return;
            }
            if(bitmap != null){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                if(data.length > FIVE_MEGABYTE){
                    Toast.makeText(myContext, "Image size is too large!", Toast.LENGTH_LONG).show();
                }
                else{
                    PopupLoadingScreen popupLoadingScreen = new PopupLoadingScreen(myContext, "Loading....");
                    popupLoadingScreen.show();
                    stRef.child(mAuth.getCurrentUser().getUid()).putBytes(data).addOnSuccessListener(taskSnapshot -> {
                        popupLoadingScreen.dismiss();
                        Toast.makeText(myContext, "Profile Picture Successfully Uploaded!", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        popupLoadingScreen.dismiss();
                        Toast.makeText(myContext, "Profile Picture Upload Error!"+e.getMessage(), Toast.LENGTH_LONG).show();

                    });
                }
            }

            myRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("name").setValue(str1);
            tx1.setEnabled(false);

            String str3 = Objects.requireNonNull(tx3.getEditText()).getText().toString();
            String str4 = Objects.requireNonNull(tx4.getEditText()).getText().toString();

            if(str3.isEmpty() || str4.isEmpty()){
                edit.setVisibility(View.VISIBLE);
            }
            else{
                myRef.child(mAuth.getCurrentUser().getUid()).child("password").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            myRef.child(mAuth.getCurrentUser().getUid()).child("password").setValue(str4);
                            Toast.makeText(myContext, "Password Updated Successfully!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String str = Objects.requireNonNull(snapshot.getValue()).toString();
                            if(!str.equals(str3)){
                                Toast.makeText(myContext, "Password Not Matched!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                myRef.child(mAuth.getCurrentUser().getUid()).child("password").setValue(str4);
                                Toast.makeText(myContext, "Password Updated Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }

            edit.setVisibility(View.VISIBLE);
            upload_btn.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            tx3.setVisibility(View.GONE);
            tx4.setVisibility(View.GONE);

        });

        return view;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        myContext = context;
    }
}
