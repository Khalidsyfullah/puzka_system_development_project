package com.akapps.puzka;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.securepreferences.SecurePreferences;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GoogleSignIn extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    public static final int RC_SIGN_IN = 1;
    Button skip_btn;
    SharedPreferences sharedPreferences;
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Material2);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);
        signInButton = findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        skip_btn = findViewById(R.id.button38);
        sharedPreferences = new SecurePreferences(this);
        skip_btn.setVisibility(View.GONE);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("554139525958-i5oom9m02iu9dp7csuffuac9bn9ipk35.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        if(mAuth.getCurrentUser() != null){
            checkStatus();
        }
        else {
            skip_btn.setVisibility(View.VISIBLE);
        }

        skip_btn.setOnClickListener(v -> {
            startActivity(new Intent(GoogleSignIn.this, Centerpage.class));
            finish();
        });
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(GoogleSignIn.this, "Sign in Successful",Toast.LENGTH_LONG).show();
                        checkStatus();
                    } else {
                        Toast.makeText(GoogleSignIn.this, "App Sign in Failed!", Toast.LENGTH_LONG).show();
                    }
                });

    }

    void checkStatus()
    {
        PopupLoadingScreen popupLoadingScreen = new PopupLoadingScreen(GoogleSignIn.this, "Loading....");
        popupLoadingScreen.show();
        myRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(!snapshot.exists()){
                            sharedPreferences.edit().putInt("newUserFound", 0).apply();
                        }
                        else{
                            MainProfile mainProfile = snapshot.getValue(MainProfile.class);
                            if(mainProfile == null){
                                sharedPreferences.edit().putInt("newUserFound", 0).apply();
                            }
                            else {
                                if(mainProfile.getIsEnabled() == 1){
                                    sharedPreferences.edit().putInt("newUserFound", 1).apply();
                                }
                                else{
                                    popupLoadingScreen.dismiss();
                                    startActivity(new Intent(GoogleSignIn.this, Centerpage.class));
                                    finish();
                                    return;
                                }
                            }
                        }
                        popupLoadingScreen.dismiss();
                        startActivity(new Intent(GoogleSignIn.this, AuthenticatorActivity.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        popupLoadingScreen.dismiss();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(GoogleSignIn.this, "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

}