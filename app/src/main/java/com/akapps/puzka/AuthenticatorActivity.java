package com.akapps.puzka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.securepreferences.SecurePreferences;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Executor;

public class AuthenticatorActivity extends AppCompatActivity {
    Executor executor;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Button fingerprint, password;
    TextInputLayout textInputLayout;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    SharedPreferences sharedPreferences;
    int user_type = 0;
    TextView back_btn, show_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Material2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);
        fingerprint = findViewById(R.id.button39);
        password = findViewById(R.id.button40);
        textInputLayout = findViewById(R.id.outlinedTextField);
        sharedPreferences = new SecurePreferences(this);
        back_btn = findViewById(R.id.textView137);
        show_text = findViewById(R.id.textView136);
        executor = ContextCompat.getMainExecutor(this);
        show_text.setVisibility(View.GONE);
        user_type = sharedPreferences.getInt("newUserFound", 0);

        if(user_type == 0){
            show_text.setVisibility(View.VISIBLE);
        }
        else{
            back_btn.setVisibility(View.GONE);
        }

        password.setOnClickListener(v -> {
            String pass = Objects.requireNonNull(textInputLayout.getEditText()).getText().toString();
            if(pass.isEmpty()){
                textInputLayout.setError("Require Password!");
                return;
            }

            if(user_type == 0){
                MainProfile mainProfile = new MainProfile("No", pass,
                        Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(), 1);
                myRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).setValue(mainProfile);
                startActivity(new Intent(AuthenticatorActivity.this, Centerpage.class));
                finish();
            }
            else{
                PopupLoadingScreen popupLoadingScreen = new PopupLoadingScreen(AuthenticatorActivity.this, "Loading.....");
                popupLoadingScreen.show();
                myRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("password").
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                popupLoadingScreen.dismiss();
                                if(!snapshot.exists()) return;
                                String val = Objects.requireNonNull(snapshot.getValue()).toString();
                                if(val.equals(pass)){
                                    startActivity(new Intent(AuthenticatorActivity.this, Centerpage.class));
                                    finish();
                                }
                                else{
                                    textInputLayout.setError("Password Not Matched!");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                popupLoadingScreen.dismiss();
                            }
                        });
            }

        });

        back_btn.setOnClickListener(v -> {
            MainProfile mainProfile = new MainProfile("No", "No",
                    Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(), 0);
            myRef.child(mAuth.getCurrentUser().getUid()).setValue(mainProfile);
            startActivity(new Intent(AuthenticatorActivity.this, Centerpage.class));
        });


        biometricPrompt = new BiometricPrompt(AuthenticatorActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AuthenticatorActivity.this, Centerpage.class));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Cofirm your Fingerprint Now")
                .setNegativeButtonText("Use Password Instead")
                .build();

        fingerprint.setOnClickListener(view -> biometricPrompt.authenticate(promptInfo));

    }

    @Override
    public void onBackPressed() {

    }
}