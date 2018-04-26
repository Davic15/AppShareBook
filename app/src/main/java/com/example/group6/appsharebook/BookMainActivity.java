package com.example.group6.appsharebook;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
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

import java.util.ArrayList;
import java.util.List;

public class BookMainActivity extends AppCompatActivity {
    List<Book> lstBook;
    String name,URL,bookID;
    Uri myuri;
    StorageReference myStorage;
    DatabaseReference mDatabase;
    private Book newBook;

    android.support.v7.widget.Toolbar tbar;
    private static final int ADD_BOOK_ACTIVITY_CODE=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_book_recyclerview);
        // getImage();
        // String imageUri = myuri.toString();
        tbar=findViewById(R.id.toolbar_recycle);
        tbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tbar);

        myStorage = FirebaseStorage.getInstance().getReference();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Books").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                lstBook = new ArrayList<Book>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Book newBook = child.getValue(Book.class);
                    String bookId = child.getKey();
                    String thumb = newBook.getThumbnail();
                    final Book myBook = new Book();
                    myBook.setAuthor(null);
                    myBook.setConditions(null);
                    myBook.setLanguage(null);
                    String user = newBook.getUser();
                    myBook.setUser(user);
                    myBook.setEdition(newBook.getEdition());
                    String name = newBook.getTitle();
                    String bookID = newBook.getID();
                    myBook.setTitle(name);
                    myBook.setID(bookID);
                    if (thumb!=null) {
                    StorageReference newStorage = myStorage.child("Bookpics").child(bookId);
                        newStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String myUri = uri.toString();
                                myBook.setThumbnail(myUri);
                                lstBook.add(myBook);
                                RecyclerView myrv = (RecyclerView) findViewById(R.id.recyclerview_id);
                                RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(BookMainActivity.this, lstBook);
                                //gridLayoutManager is an implementation of Recyclerview that lays out items in a grid.
                                //could be modified with linerlayoutmanager, etc
                                myrv.setLayoutManager(new GridLayoutManager(BookMainActivity.this, 3));
                                myrv.setAdapter(myAdapter);
                            }
                        });

                    } else {
                        StorageReference newStorage = myStorage.child("No-image-available.jpg");
                        newStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String myUri = uri.toString();
                                myBook.setThumbnail(myUri);
                                lstBook.add(myBook);
                                RecyclerView myrv = (RecyclerView) findViewById(R.id.recyclerview_id);
                                RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(BookMainActivity.this, lstBook);
                                //gridLayoutManager is an implementation of Recyclerview that lays out items in a grid.
                                //could be modified with linerlayoutmanager, etc
                                myrv.setLayoutManager(new GridLayoutManager(BookMainActivity.this, 3));
                                myrv.setAdapter(myAdapter);
                            }
                        });
                    }
                    }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.recycle_menu, menu);

        int positionOfMenuItem = 2; // or whatever...
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString("Edit Profile");
        s.setSpan(new ForegroundColorSpan(Color.BLUE), 0, s.length(), 0);
        item.setTitle(s);

        return true;
    }


    public boolean onOptionsItemSelected(MenuItem menu){
        int id=menu.getItemId();

        if(id==R.id.add){
            Intent intent=new Intent(this, BookProfile.class);
            this.startActivity(intent);
            finish();
        }
        if(id==R.id.logout){
            //do the logout
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainPage.class);
            startActivity(intent);
            finish();
        }

        if(id==R.id.profile){
            Intent intent = new Intent(this, ShowProfile.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(menu);
    }

}





