package com.example.group6.appsharebook;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class BookProfile extends AppCompatActivity {
    private int GALLERY=1, CAMERA_S=2;
    Button scanBarCode, takeBookPic, save;
    EditText name, ISBN, author, edition, conditions;

    ImageView barCode, bookCover;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookprofileedit);
        scanBarCode = findViewById(R.id.button_barscan);
        takeBookPic = findViewById(R.id.button_bookimage);
        save = findViewById(R.id.saveButton);

        name=findViewById(R.id.BookNameEdit);
        ISBN=findViewById(R.id.BookISBNEdit);
        author = findViewById(R.id.BookAuthorEdit);
        edition = findViewById(R.id.BookEditionEdit);
        conditions = findViewById(R.id.BookConditionEdit);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //store informations to the database (FireBase)
            }
        });




        scanBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_S);
            }
        });
        takeBookPic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_S);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode ,resultCode, data);



        if(requestCode == CAMERA_S){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            //convert image and send it to the database


        }

    }


    private void openGallery(){
        //check if the permission are granted or not
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getResources().getString(R.string.select_action));
        String[] pictureDialogItems = {
                getResources().getString(R.string.select_photo_gallery),
                getResources().getString(R.string.select_photo_camera)};
        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        choosePhotoFromGallery();
                        break;
                    case 1:
                        openCamera();
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void openCamera(){

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_S);
    }





}
