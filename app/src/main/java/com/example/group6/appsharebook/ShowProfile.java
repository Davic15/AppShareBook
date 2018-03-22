package com.example.group6.appsharebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ShowProfile extends AppCompatActivity {
    android.support.v7.widget.Toolbar tb;
    TextView nameview,surnameView,emailView,bioView;
    String name,surname,email,bio;

    Image image1;

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showprofile);
        tb = findViewById(R.id.toolbar);
        tb.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tb);
        nameview = findViewById(R.id.textViewUserName);
        surnameView = findViewById(R.id.textViewUserSurname);
        emailView = findViewById(R.id.textViewUserEmail);
        bioView = findViewById(R.id.textViewUserBio);


        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        String name1 = settings.getString("name", name);
        nameview.setText(name1);
        String surname1 = settings.getString("surname",surname);
        surnameView.setText(surname1);
        String email1 = settings.getString("email",email);
        emailView.setText(email1);
        String bio1 = settings.getString("userBio",bio);
        bioView.setText(bio1);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.my_menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menu){
        int id=menu.getItemId();

        if(id == R.id.edit){
            Intent intent=new Intent(this, EditProfile.class);
            this.startActivityForResult(intent,SECOND_ACTIVITY_REQUEST_CODE);

        }
        return super.onOptionsItemSelected(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                String name = data.getStringExtra("name");
                nameview.setText(name);
                String surname = data.getStringExtra("surname");
                surnameView.setText(surname);
                String email = data.getStringExtra("email");
                emailView.setText(email);
                String bio = data.getStringExtra("userBio");
                bioView.setText(bio);


            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings = getPreferences(
                MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name",nameview.getText().toString());
        editor.putString("surname",surnameView.getText().toString());
        editor.putString("email",emailView.getText().toString());
        editor.putString("userBio",bioView.getText().toString());
        editor.commit();
    }
}
