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
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.net.Uri;

public class ShowProfile extends AppCompatActivity {
    android.support.v7.widget.Toolbar tb;
    TextView nameView,surnameView,emailView,bioView;
    String name,surname,email,bio;
    ImageView imageView;
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference myStorage;
    private DatabaseReference UsersDatabase;
    String userID;


    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showprofile);
        tb = findViewById(R.id.toolbar);
        tb.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tb);

        myStorage = FirebaseStorage.getInstance().getReference();

        nameView = findViewById(R.id.textViewUserName);
        surnameView = findViewById(R.id.textViewUserSurname);
        emailView = findViewById(R.id.textViewUserEmail);
        bioView = findViewById(R.id.textViewUserBio);
        imageView = findViewById(R.id.imageView);

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        //String name1 = settings.getString("name", name);
        //nameView.setText(name1);
        //String surname1 = settings.getString("surname",surname);
        //surnameView.setText(surname1);
        //String email1 = settings.getString("email",email);
        //emailView.setText(email1);
        //String bio1 = settings.getString("userBio",bio);
        //bioView.setText(bio1);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //String emailUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();


        mDatabase.child("Users")
                .orderByKey()
                .equalTo(userID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot child :dataSnapshot.getChildren()) {
                            String loggedUsername = child.getValue(User.class).getName();
                            nameView.setText(loggedUsername);
                            String loggedUserSurname = child.getValue(User.class).getSurname();
                            surnameView.setText(loggedUserSurname);
                            String loggedUserEmail = child.getValue(User.class).getEmail();
                            emailView.setText(loggedUserEmail);
                            String loggedUserBio = child.getValue(User.class).getUserBio();
                            bioView.setText(loggedUserBio);
                            // String uri = child.getValue(User.class).getUri();

                        }


                    }

                    @Override
                    public void onCancelled (DatabaseError databaseError){
                        throw databaseError.toException();
                    }

                });




       // loadImageFromStorage();

    }

    @Override
    protected void onStart() {
        super.onStart();

        StorageReference newStorage = myStorage.child("ProfilePics").child(userID);
        newStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageView);
            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.my_menu, menu);


        int positionOfMenuItem = 3;
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString("Go to books");
        s.setSpan(new ForegroundColorSpan(Color.BLUE), 0, s.length(), 0);
        item.setTitle(s);


        int positionOfMenuItem1 = 2;
        MenuItem item1 = menu.getItem(positionOfMenuItem1);
        SpannableString s1 = new SpannableString("Add Book");
        s1.setSpan(new ForegroundColorSpan(Color.BLUE), 0, s1.length(), 0);
        item1.setTitle(s1);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menu){
        int id=menu.getItemId();

        if(id == R.id.edit){
            Intent intent=new Intent(this, EditProfile.class);
            this.startActivityForResult(intent,SECOND_ACTIVITY_REQUEST_CODE);
        }
        if(id==R.id.add){
            //switch to add book activity
            Intent intent=new Intent(this, BookProfile.class);
            this.startActivity(intent);
            finish();
        }
        if(id==R.id.logout){
            //do the logout
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(this, MainPage.class);
            this.startActivity(intent);
            finish();
        }
        if(id == R.id.books){
            Intent intent = new Intent(this, BookMainActivity.class);
            this.startActivity(intent);
            finish();
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
                //uri = data.getStringExtra("Uri");


                //loadImageFromStorage();
                User user = new User (name,surname,email,bio);
                mDatabase.child("Users").child(userID).setValue(user);
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

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        float cx = 0, cy = 0, r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            cx = bitmap.getWidth() / 2;
            cy = bitmap.getHeight() / 2;
            r = bitmap.getHeight() / 2;
        }
        else {
            cx = bitmap.getHeight() / 2;
            cy = bitmap.getWidth() / 2;
            r = bitmap.getWidth() / 2;
        }

        canvas.drawCircle(cx, cy, r, paint); //cx: x-coordinate of the center of the circle, cy: y-coordinate of the center, r: radius of the circle, paint: paint used to draw the circle
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


}