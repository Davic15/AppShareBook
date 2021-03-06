package com.example.group6.appsharebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registration extends AppCompatActivity  {

    private Button buttonRegister;
    private EditText userEmail;
    private EditText userPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser()!= null) {
            finish();
            startActivity(new Intent(this,ShowProfile.class));
        }

        progressDialog = new ProgressDialog(this);

        buttonRegister = findViewById(R.id.registrationButton);
        userEmail = findViewById(R.id.registerUsername);
        userPassword = findViewById(R.id.registerPassword);

        //buttonRegister.setOnClickListener(this);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    private void registerUser() {
        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            // email is empty;
            Toast.makeText(this,"Please enter Email",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            // password is empty;
            Toast.makeText(this,"Please enter Password",Toast.LENGTH_SHORT).show();
            return;

        }

        progressDialog.setMessage("Registering User...");

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // user is registered
                            Toast.makeText( Registration.this, "User registered",Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(),ShowProfile.class));
                        } else {
                            Toast.makeText( Registration.this, "User not registered",Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }

    public void onClick (View view) {
        if (view == buttonRegister) {
            registerUser();
        }
    }
}
