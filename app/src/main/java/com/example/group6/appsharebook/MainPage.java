package com.example.group6.appsharebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
    }

    void clickRegister (View v) {
        Intent intent = new Intent(this, Registration.class);
        this.startActivity(intent);
    }

}
