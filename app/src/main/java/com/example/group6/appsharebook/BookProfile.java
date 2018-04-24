package com.example.group6.appsharebook;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class BookProfile  extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private final int GALLERY=1, CAMERA_S =2, REQUEST_CODE_BARCODE=3, REQUEST_CODE_FOR_INFO=4;


    boolean flagResultFromQuery; //true = getByQuerying

    StorageReference myStorage;

    private DatabaseReference mDatabase;


    Button scanBarCode, editInfo, save;
    TextView name, ISBN, author, edition, conditions;
    String bookPath;
    ImageView cover;
    String userID;

    int  pageNumber;

    String nomeString, isbnString, editionString, conditionsString, languageString;

    Set<String> authorsString = new TreeSet<>();

    public static final int RequestPermissionCode = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bookprofile);
        flagResultFromQuery=false;

        myStorage = FirebaseStorage.getInstance().getReference();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        scanBarCode = (Button)findViewById(R.id.button_barscan);
        editInfo = (Button) findViewById(R.id.button_insertInfo);
        save = findViewById(R.id.saveButton);

        name=findViewById(R.id.BookName);
        ISBN=findViewById(R.id.BookISBN);
        author = findViewById(R.id.BookAuthor);
        edition = findViewById(R.id.BookEdition);
        conditions = findViewById(R.id.BookCondition);

        cover = findViewById(R.id.immaginelibro);




        ActivityCompat.requestPermissions(BookProfile.this, new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE,
                        INTERNET,
                        ACCESS_NETWORK_STATE
                }, RequestPermissionCode);



        /*-------------------------------------------*/

        //Check if a Loader is running, if it is, reconnect to it
        if(getSupportLoaderManager().getLoader(0)!=null){
            getSupportLoaderManager().initLoader(0,null,this);
        }

        /*--------------------------------------------*/


        SharedPreferences sp = getPreferences(MODE_PRIVATE);

        String nameTemp =sp.getString("name", nomeString);
        if(name.getText().toString().equals("")){
            name.setText(nameTemp);
        }

        String[] authorsTemp=null;

        authorsString = sp.getStringSet("authors", authorsString);

        isbnString = sp.getString("isbn", isbnString);
        if(ISBN.getText().toString().equals("")){
            ISBN.setText(isbnString);
        }

        if(!authorsString.isEmpty()){
            if(author.getText().toString().equals("")){
                Iterator<String> it = authorsString.iterator();
                String tmp = it.next();
                author.setText(tmp);
            }
        }


        String editionTemp = sp.getString("edition", editionString);
        if(edition.getText().toString().equals("")){
            edition.setText(editionTemp);
        }

        String conditionsTemp = sp.getString("conditions", conditionsString);
        if(conditions.getText().toString().equals("")){
            conditions.setText(conditionsTemp);
        }

        bookPath = sp.getString("bookPath", bookPath);
        loadImageFromStorage();


        scanBarCode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                switch (view.getId()){
                    case R.id.button_barscan:
                        Intent intent = new Intent(view.getContext(), CameraCodeScanner.class);
                        startActivityForResult(intent, REQUEST_CODE_BARCODE);
                        break;
                }


            }
        });

        editInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getBaseContext(), BookProfileEdit.class);
                startActivityForResult(intent, REQUEST_CODE_FOR_INFO);
            }
        });




    }

    private void setViewFields(String titolo, String autore, String edizione, String lingua, String condizioni ){
        if(titolo.equals("")){
            name.setText("Not Found");
        }else{
            name.setText(titolo);
        }
        if(autore.equals("")){
            name.setText("Not Found");
        }
        else{
            author.setText(autore);
        }
        if(edizione.equals("")){
            name.setText("Not Found");
        }
        else{
            edition.setText(edizione);
        }
        if(condizioni.equals("")){
            name.setText("Not Found");
        }
        else{
            conditions.setText(condizioni);
        }
    }

    public void send(View view){
        Intent returnIntent = new Intent();


        returnIntent.putExtra("name", nomeString);
        returnIntent.putExtra("isbn",isbnString);
        int i=0;
        for (String s: authorsString
             ) {
            String aut = "author"+i;
            i++;
            returnIntent.putExtra(aut, s);
        }

        returnIntent.putExtra("edition",editionString);
        String bookPath1 = bookPath;
        returnIntent.putExtra("bookPath", bookPath1);
        setResult(RESULT_OK, returnIntent);
        finish();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode ,resultCode, data);

        if( (requestCode == REQUEST_CODE_BARCODE) && (resultCode == RESULT_OK) ){
            String isbn = data.getStringExtra("ISBN");
            if(!isbn.isEmpty()){
                ISBN.setText(isbn);
            }
            //retrieve book info by ISBN query
            flagResultFromQuery=true;
            searchBookInfoByISBN(isbn);
        }

        if(requestCode == REQUEST_CODE_FOR_INFO && (resultCode == RESULT_OK)){
            nomeString = data.getStringExtra("name");
            if( nomeString != null){
                name.setText(nomeString);
            }

            String conditionsString = data.getStringExtra("conditions");

            isbnString = data.getStringExtra("isbn");
            if( isbnString != null){
                ISBN.setText(isbnString);
            }

            String tmp = data.getStringExtra("author");
            String authorString = authorsString.iterator().next();

                author.setText(authorString);


            String edition1 = data.getStringExtra("edition");
            if( edition1 != null){
                edition.setText(edition1);
            }

            String bookID = data.getExtras().getString("imagePath");

            Book newBook = new Book (nomeString,bookID,bookID,authorString,conditionsString,languageString,editionString);

            mDatabase.child("Books").child(bookID).setValue(newBook);

            bookPath = data.getStringExtra("bookPath");

            // loadImageFromStorage();

        }

    }

    public void openGallery(){
        //check if the permission are granted or not

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            return;
        }

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

    private void loadImageFromStorage(){

      if(bookPath != null){
          ContextWrapper cw = new ContextWrapper(getApplicationContext());
          File directory = cw.getDir("bookProfileImageDir", Context.MODE_PRIVATE); // Dir: /data/user/0/com.example.group6.appsharebook/app_userProfileImageDir where user profile image is saved
          File bookProfileImagePath = new File(directory, bookPath);

          if(bookProfileImagePath.exists()) {

              FileInputStream fis = null;
              try {
                  fis = new FileInputStream(bookProfileImagePath);
                  Bitmap b = BitmapFactory.decodeStream(fis);
                  cover.setImageBitmap(b);

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

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadExternal = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteExternal = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (CameraPermission && ReadExternal && WriteExternal) {

                        Toast.makeText(BookProfile.this, getResources().getString(R.string.permission_granted), Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(BookProfile.this,getResources().getString(R.string.permission_denied),Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }




    /*----------------------------------------------------------*/ //new functions for loader (connect to the online database for retrieving informations)

    /**
     * The LoaderManager calls this method when the loader is created.
     *
     * @param id ID integer to id   entify the instance of the loader.
     * @param args The bundle that contains the search parameter.
     * @return Returns a new BookLoader containing the search term.
     */
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new BookLoader(this, args.getString("queryString"));
    }

    /**
     * Called when the data has been loaded. Gets the desired information from
     * the JSON and updates the Views.
     *
     * @param loader The loader that has finished.
     * @param data The JSON response from the Books API.
     */
    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        try {
            // Convert the response into a JSON object.
            JSONObject jsonObject = new JSONObject(data);
            // Get the JSONArray of book items.
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            // Initialize results fields.
            String title = null;
            String author = null;
            Integer count = 0;

            // Get the item information.
            JSONObject book = itemsArray.getJSONObject(0);
            JSONObject volumeInfo = book.getJSONObject("volumeInfo");


            //TODO


            // Try to get the author, the number of pages and the title from the item.
            nomeString = volumeInfo.getString("title");
            authorsString.clear();
            try{
                for(int i= 0; i<volumeInfo.getJSONArray("authors").length(); i++){
                    authorsString.add(volumeInfo.getJSONArray("authors").getString(i));
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                pageNumber = volumeInfo.getInt("pageCount");
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                editionString = volumeInfo.getString("publishedDate");
            }catch (Exception e){
                e.printStackTrace();
            }
            try{
                conditionsString = volumeInfo.getString("conditions");
            }catch (Exception e){
                e.printStackTrace();
            }


            Iterator<String> it = authorsString.iterator();
            if(authorsString.isEmpty()){
                setViewFields(nomeString, "", "", "","");
            }
            else{

                setViewFields(nomeString, it.next(), editionString, languageString, conditionsString);
            }


            if (title == null && author == null && count == 0){
                // If none are found, update the UI to show failed results.

            } else {

            }

        } catch (Exception e){
            // If onPostExecute does not receive a proper JSON string, update the UI to show failed results.

            e.printStackTrace();
        }
    }

    /**
     * In this case there are no variables to clean up when the loader is reset.
     *
     * @param loader The loader that was reset.
     */
    @Override
    public void onLoaderReset(Loader<String> loader) {}


    /**
     * Gets called when the user pushes the "Press" button
     *
     *
     */
    public void searchBookInfoByISBN(String queryISBN) {
        // Get the search string from the input field.


        /*
        // Hide the keyboard when the button is pushed.
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        */

        // Check the status of the network connection.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If the network is active and the search field is not empty,
        // add the search term to the arguments Bundle and start the loader.
        if (networkInfo != null && networkInfo.isConnected() && queryISBN.length()!=0) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryISBN);
            getSupportLoaderManager().restartLoader(0, queryBundle, this);
        }
        // Otherwise update the TextView to tell the user there is no connection or no search term.
        else {
            if (queryISBN.length() == 0) {
              //handle situation when isbn has wrong size
            } else {
                //handle situation internet not available
            }
        }
    }

}
