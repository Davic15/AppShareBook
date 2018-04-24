package com.example.group6.appsharebook;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

// import com.firebase.client.Firebase;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class EditProfile extends AppCompatActivity {
    private int GALLERY = 1, CAMERA_S = 2;
    ImageView image1;
    EditText name1, surname1, email1, userBio1;
    String contentURI;
    StorageReference myStorage;


    public static final int RequestPermissionCode = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Firebase.setAndroidContext(this);
        setContentView(R.layout.editprofile);
        name1 = findViewById(R.id.userNameEditText);
        image1 = findViewById(R.id.imageView);
        surname1 = findViewById(R.id.userSurnameEditText);
        email1 = findViewById(R.id.userEmailEditText);
        userBio1 = findViewById(R.id.userBioEditText);

        myStorage = FirebaseStorage.getInstance().getReference();


        //loadImageFromStorage();

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }

        });
    }

    public void sendData(View v) {

        Intent returnIntent = new Intent();
        String name = name1.getText().toString();
        returnIntent.putExtra("name", name);
        String surname = surname1.getText().toString();
        returnIntent.putExtra("surname", surname);
        String email = email1.getText().toString();
        returnIntent.putExtra("email", email);
        String userBio = userBio1.getText().toString();
        returnIntent.putExtra("userBio", userBio);
        returnIntent.putExtra("Uri", contentURI);


        setResult(RESULT_OK, returnIntent);
        finish();


    }

    private void openGallery() {
        //check if the permission are granted or not
        checkPer();

        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
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
                        takePhotoFromCamera();
                        break;
                }
            }
        });
        pictureDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                contentURI = data.getData().toString();
                Uri uri = data.getData();


                    StorageReference childPath = myStorage.child("ProfilePics").child(FirebaseAuth.getInstance().getCurrentUser().getUid());



                    childPath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(EditProfile.this, "Upload done", Toast.LENGTH_SHORT).show();
                            Uri myuri = taskSnapshot.getDownloadUrl();
                            Picasso.get().load(myuri).into(image1);

                        }
                    });
                }





            //    try {
            //        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            //         saveToInternalStorage(bitmap);
            //        Toast.makeText(EditProfile.this, getResources().getString(R.string.image_saved), Toast.LENGTH_SHORT).show();
            //        image1.setImageBitmap( getRoundedCornerBitmap(bitmap)); //show the image into the interface

            //    } catch (IOException e) {
            //        e.printStackTrace();
            //        Toast.makeText(EditProfile.this, getResources().getString(R.string.image_fail), Toast.LENGTH_SHORT).show();
           //     }
           // }

        } else if (requestCode == CAMERA_S) {
            //Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            //image1.setImageBitmap( getRoundedCornerBitmap(thumbnail));
            //saveToInternalStorage(thumbnail);
            if (data != null) {
                contentURI = data.getData().toString();
                Uri uri = data.getData();


                StorageReference childPath = myStorage.child("ProfilePics").child(FirebaseAuth.getInstance().getCurrentUser().getUid());



                childPath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(EditProfile.this, getResources().getString(R.string.image_saved), Toast.LENGTH_SHORT).show();

                        Uri myuri = taskSnapshot.getDownloadUrl();
                        Picasso.get().load(myuri).into(image1);

                    }
                });
            }
        }
    }

    public void choosePhotoFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    public void takePhotoFromCamera(){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_S);
    }
/*
    private String saveToInternalStorage(Bitmap bitmapImage){

        //create the path where save the image
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("userProfileImageDir", Context.MODE_PRIVATE); // Dir: /data/user/0/com.example.group6.appsharedbook/app_userProfileImageDir where user profile image is saved
        File userProfileImagePath = new File(directory,"userProfileImage.jpg");

        //save the image
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(userProfileImagePath);
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
        }
        return directory.getAbsolutePath();
    }
*/
    private void checkPer(){

        //If permission is not enabled
        if(!CheckingPermissionIsEnabledOrNot()) {

            //Calling method to enable permission.
            RequestMultiplePermission();
        }
    }

    //Permission function starts from here
    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(EditProfile.this, new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                }, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadExternal = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteExternal = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (CameraPermission && ReadExternal && WriteExternal) {

                        Toast.makeText(EditProfile.this, getResources().getString(R.string.permission_granted), Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(EditProfile.this,getResources().getString(R.string.permission_denied),Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    // Checking permission is enabled or not using function starts from here.
    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

/*
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
                image1 = findViewById(R.id.imageView);
                image1.setImageBitmap( getRoundedCornerBitmap(b));
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
*/
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
