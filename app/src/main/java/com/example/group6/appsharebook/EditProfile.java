package com.example.group6.appsharebook;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class EditProfile extends AppCompatActivity {
    private int GALLERY = 1, CAMERA_S = 2;
    private static final String IMAGE_DIRECTORY = "/group6";
    ImageView image23,image1;
    EditText name1,surname1,email1,userBio1;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    public static final int RequestPermissionCode = 7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);
        name1 = findViewById(R.id.userNameEditText);
        image1 = findViewById(R.id.imageView);
        surname1 = findViewById(R.id.userSurnameEditText);
        email1 = findViewById(R.id.userEmailEditText);
        userBio1 = findViewById(R.id.userBioEditText);

        image23 = findViewById(R.id.imageView);

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }

        });
    }

    public void sendData (View v) {


        Intent returnIntent = new Intent();
        Bitmap image = image1.getDrawingCache();
        returnIntent.putExtra("image",image);
        String name = name1.getText().toString();
        returnIntent.putExtra("name",name);
        String surname = surname1.getText().toString();
        returnIntent.putExtra("surname",surname);
        String email = email1.getText().toString();
        returnIntent.putExtra("email", email);
        String userBio = userBio1.getText().toString();
        returnIntent.putExtra("userBio",userBio);


        setResult(RESULT_OK, returnIntent);
        finish();


    }

    private void openGallery(){
        //check if the permission are granted or not
        checkPer();

        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        //pictureDialog.setTitle("Select Action");
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
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(EditProfile.this, getResources().getString(R.string.image_saved), Toast.LENGTH_SHORT).show();
                    image1.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EditProfile.this, getResources().getString(R.string.image_fail), Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA_S) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            image1.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(EditProfile.this, getResources().getString(R.string.image_saved), Toast.LENGTH_SHORT).show();
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

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", getResources().getString(R.string.file_save) + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void checkPer(){
        if(CheckingPermissionIsEnabledOrNot())
        {
            Toast.makeText(EditProfile.this, getResources().getString(R.string.all_permission_granted), Toast.LENGTH_LONG).show();
        }
        // If, If permission is not enabled then else condition will execute.
        else {
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

}
