package com.example.group6.appsharebook;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ShowProfile extends AppCompatActivity {
    android.support.v7.widget.Toolbar tb;
    TextView nameView,surnameView,emailView,bioView;
    String name,surname,email,bio;
    ImageView imageView;

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showprofile);
        tb = findViewById(R.id.toolbar);
        tb.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tb);

        nameView = findViewById(R.id.textViewUserName);
        surnameView = findViewById(R.id.textViewUserSurname);
        emailView = findViewById(R.id.textViewUserEmail);
        bioView = findViewById(R.id.textViewUserBio);

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        String name1 = settings.getString("name", name);
        nameView.setText(name1);
        String surname1 = settings.getString("surname",surname);
        surnameView.setText(surname1);
        String email1 = settings.getString("email",email);
        emailView.setText(email1);
        String bio1 = settings.getString("userBio",bio);
        bioView.setText(bio1);

        loadImageFromStorage();
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
                if(!name.isEmpty())
                    nameView.setText(name);
                String surname = data.getStringExtra("surname");
                if(!surname.isEmpty())
                    surnameView.setText(surname);
                String email = data.getStringExtra("email");
                if(!email.isEmpty())
                    emailView.setText(email);
                String bio = data.getStringExtra("userBio");
                if(!bio.isEmpty())
                    bioView.setText(bio);

                loadImageFromStorage();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name",nameView.getText().toString());
        editor.putString("surname",surnameView.getText().toString());
        editor.putString("email",emailView.getText().toString());
        editor.putString("userBio",bioView.getText().toString());
        editor.commit();
    }

    private void loadImageFromStorage(){

        //Retrieve the user profile image
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("userProfileImageDir", Context.MODE_PRIVATE); // Dir: /data/user/0/com.example.group6.appsharebook/app_userProfileImageDir where user profile image is saved
        File userProfileImagePath = new File(directory,"userProfileImage.jpg");

        if(userProfileImagePath.exists()) {

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(userProfileImagePath);
                Bitmap b = BitmapFactory.decodeStream(fis);
                imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(getRoundedCornerBitmap(b));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}