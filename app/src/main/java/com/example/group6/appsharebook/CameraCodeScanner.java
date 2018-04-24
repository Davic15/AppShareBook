package com.example.group6.appsharebook;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.io.InputStream;

public class CameraCodeScanner extends AppCompatActivity {
        private static final int RESULT_LOAD_IMAGE = 1;
        private static final int CAMERA_PERMISSION_CODE = 1001;
        TextView textView;
        SurfaceView surfaceView;
        Button button;
        Button save;
        CameraSource cameraSource;
        BarcodeDetector barcodeDetector;
        boolean flag;
        String ISBN;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_PERMISSION_CODE:{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                }
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;


        }
    }

    @Override
    protected void onCreate(Bundle bundle){
            super.onCreate(bundle);
            setContentView(R.layout.cameracodescanner);
            flag=true;
            ISBN = null;
            textView = (TextView) findViewById(R.id.TextViewScanCode);
            button = (Button) findViewById(R.id.pickScanCode);
            save = (Button) findViewById(R.id.Save_isbn);
            surfaceView =  findViewById(R.id.scanCodeImage);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED ){
            return;
        }


        barcodeDetector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        if(!barcodeDetector.isOperational()){
            textView.setText("Error in setting up");
            return;
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load image
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE); //

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ISBN != null){
                    Intent intent = new Intent(getBaseContext(), BookProfile.class);
                    intent.putExtra("ISBN", ISBN);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

        final CameraSource cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .build();

        //add event: barcode detected
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != 0){
                    ActivityCompat.requestPermissions(CameraCodeScanner.this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                }
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                if(flag){

                    final SparseArray<Barcode> code = detections.getDetectedItems();

                    if(code.size() != 0){
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(500);
                                textView.setText(code.valueAt(0).displayValue);
                                ISBN = code.valueAt(0).displayValue;
                                cameraSource.takePicture(null, null);
                                flag=false;

                            }
                        });
                    }
                }
                }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();

            try{
                InputStream ims = getContentResolver().openInputStream(selectedImage);
                Bitmap bitmap=null;
                while(bitmap==null){
                    bitmap = BitmapFactory.decodeStream(ims);
                }

                processBarCode(bitmap);

            }catch (Exception e1) {
                e1.printStackTrace();
            }


        }

    }
    //processing the barCode
    private void processBarCode(Bitmap myBitMap){
            Frame frame = new Frame.Builder().setBitmap(myBitMap).build();
            SparseArray<Barcode> mybarcodes = barcodeDetector.detect(frame);
            Barcode mycode = null;
            try {
                mycode = mybarcodes.valueAt(0);

                textView.setText(mycode.displayValue);
                ISBN = mycode.displayValue;

            }catch (IndexOutOfBoundsException e){
                textView.setText("Unable");
            }



    }
}
