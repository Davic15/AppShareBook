package com.example.group6.appsharebook;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.group6.appsharebook.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class BookProfileEdit extends AppCompatActivity {

    private final int  CAMERA_REQUEST_CODE = 1, GALLERY_REQUEST_CODE=2;

    EditText name, isbn, author, edition,condition;
    Button choosePicture, save;
    ImageView bookImage;
    String bookPath;
    String contentURI;
    StorageReference myStorage;
    String ImageID;


    @Override
    protected void onCreate(Bundle savedIstanceState){
        super.onCreate(savedIstanceState);
        setContentView(R.layout.bookprofileedit);

        name = findViewById(R.id.BookNameEdit);
        isbn = findViewById(R.id.BookISBNEdit);
        author = findViewById(R.id.BookAuthorEdit);
        edition = findViewById(R.id.BookEditionEdit);
        condition = findViewById(R.id.BookConditionEdit);

        choosePicture = findViewById(R.id.choose_image);
        save = findViewById(R.id.save_book_profile);

        bookImage = findViewById(R.id.imageBook);

        myStorage = FirebaseStorage.getInstance().getReference();


        choosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });
    }

    public void choosePicture(){


        AlertDialog.Builder picDialog = new AlertDialog.Builder(this);

        picDialog.setTitle("Select Action:");
        String[] actions = {
                "Select from gallery",
                "Take a picture"
        };

        picDialog.setItems(actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        chooseFromGallery();
                        break;


                    case 1:
                        takePhoto();
                        break;
                }


            }
        });

        picDialog.show();


    }

    public void send(View view){
        Intent returnIntent = new Intent();

        String name1 = name.getText().toString();
        returnIntent.putExtra("name", name1);
        String isbn1 = isbn.getText().toString();
        returnIntent.putExtra("isbn",isbn1);
        String author1 = author.getText().toString();
        returnIntent.putExtra("author", author1);
        String edition1 = edition.getText().toString();
        String condition1 = condition.getText().toString();
        returnIntent.putExtra("conditions",condition1);
        returnIntent.putExtra("edition",edition1);
        returnIntent.putExtra("imagePath",ImageID);
        String bookPath1 = bookPath;
        returnIntent.putExtra("bookPath", bookPath1);
        setResult(RESULT_OK, returnIntent);
        finish();

    }


    private void takePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private void chooseFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (data != null) {
                contentURI = data.getData().toString();
                Uri uri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    saveToInternalStorage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ImageID = UUID.randomUUID().toString();

                StorageReference childPath = myStorage.child("Bookpics").child(ImageID);


                childPath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(BookProfileEdit.this, "Upload done", Toast.LENGTH_SHORT).show();
                        Uri myuri = taskSnapshot.getDownloadUrl();
                        Picasso.get().load(myuri).into(bookImage);

                    }
                });
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (data != null) {
                //contentURI = data.getData().toString();
                Uri uri = data.getData();

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] cameraPic = baos.toByteArray();
                saveToInternalStorage(bitmap);

                ImageID = UUID.randomUUID().toString();


                StorageReference childPath = myStorage.child("Bookpics").child(ImageID);


                childPath.putBytes(cameraPic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(BookProfileEdit.this, "Upload done", Toast.LENGTH_SHORT).show();
                        Uri myuri = taskSnapshot.getDownloadUrl();
                        Picasso.get().load(myuri).into(bookImage);

                    }
                });

            }
        }
    }





    private String saveToInternalStorage(Bitmap bitmapImage){

        //create the path where save the image
        String name="bookImage.jpg";
        Integer i = 0;
        String tmp2="bookImage.jpg";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("bookProfileImageDir", Context.MODE_PRIVATE); // Dir: /data/user/0/com.example.group6.appsharedbook/app_userProfileImageDir where user profile image is saved
        File bookProfileImagePath = new File(directory,name);

        bookPath = tmp2;
        //save the image
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(bookProfileImagePath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos); // Use the compress method on the BitMap object to write image to the OutputStream
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                fos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch(NullPointerException e1){
                e1.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }


}
