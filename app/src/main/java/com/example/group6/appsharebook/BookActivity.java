package com.example.group6.appsharebook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;


public class BookActivity  extends AppCompatActivity{
    private TextView tvtitle, tvdescription, tvcategory;
    private ImageView img;
    private StorageReference myStorage;
    String bookID;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        myStorage = FirebaseStorage.getInstance().getReference();

        tvtitle = (TextView)findViewById(R.id.txttitle);
        tvdescription = (TextView) findViewById(R.id.txtDesc);
        tvcategory = (TextView) findViewById(R.id.txtCat);
        img = (ImageView) findViewById(R.id.bookthumbnail);




       // img.setImageResource(image);
    }


    @Override
    protected void onStart() {
        super.onStart();

        //Getting data
        Intent intent = getIntent();
        String Title = intent.getExtras().getString("Title");
        String Description = intent.getExtras().getString("Description");
        String image = intent.getExtras().getString("Thumbnail");
        String category = intent.getExtras().getString("Category");
        bookID = intent.getExtras().getString("ID") + ".jpg";

        //setting values
        tvtitle.setText(Title);
        tvdescription.setText(Description);
        tvcategory.setText(category);

        StorageReference newStorage = myStorage.child("Bookpics").child(bookID);
        newStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String myUri = uri.toString();
                Picasso.get().load(uri).into(img);
            }
        });



    }

}

