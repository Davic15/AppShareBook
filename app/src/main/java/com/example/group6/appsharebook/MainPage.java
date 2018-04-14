package com.example.group6.appsharebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainPage extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void clickRegister (View v) {
        Intent intent = new Intent(this, Registration.class);
        this.startActivity(intent);
    }

    public void clickLogin (View v){
        Button btn = (Button)findViewById(R.id.login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText passwd = (EditText) findViewById(R.id.userPassword);
                EditText userwd = (EditText) findViewById(R.id.loginUsername);
                String pass11 = passwd.getText().toString();
                String user11 = userwd.getText().toString();
                String id = mDatabase.push().getKey();
                User user = new User (pass11, user11);
                mDatabase.child("user").child(id).setValue(user);

            }
        });


    }

}
