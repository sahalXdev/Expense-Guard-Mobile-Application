package com.example.expenseguard;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expenseguard.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth firebaseAuth;
//    ProgressDialog progressDialog = new ProgressDialog(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();


        binding.emailForSignIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Patterns.EMAIL_ADDRESS.matcher(binding.emailForSignIn.getText().toString()).matches()){
                    binding.btnLogin.setEnabled(true);
                }else {
                    binding.btnLogin.setEnabled(false);
                    binding.emailForSignIn.setError("Invalid Email !");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()!=null){
                    try {
                        startActivity(new Intent(MainActivity.this,DashBoardActivity.class));
                    }catch (Exception e){

                    }
                }
            }
        });

        binding.goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // progressDialog.show();
                Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                try {
                    startActivity(intent);
                }catch (Exception e){

                }

            }
        });


        binding.chbSpsd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    // For Show Password
                    binding.passwordForSignIn.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    // For Hide Password
                    binding.passwordForSignIn.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = binding.emailForSignIn.getText().toString().trim();
                String password = binding.passwordForSignIn.getText().toString().trim();

                if (email.length()<=0 || password.length()<=0){

                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(email,password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                               // progressDialog.cancel();
                                Toast.makeText(MainActivity.this, "Loggined Successfully", Toast.LENGTH_SHORT).show();
                                try {
                                    startActivity(new Intent(MainActivity.this,DashBoardActivity.class));

                                }catch (Exception e){

                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                               // progressDialog.cancel();
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        binding.tvforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String remail = binding.emailForSignIn.getText().toString();

                firebaseAuth.sendPasswordResetEmail(remail)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Toast.makeText(MainActivity.this, "Email Sented", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //progressDialog.cancel();
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}