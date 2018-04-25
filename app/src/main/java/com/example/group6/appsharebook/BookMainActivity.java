package com.example.group6.appsharebook;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_book_recyclerview);
        // getImage();
        // String imageUri = myuri.toString();


        myStorage = FirebaseStorage.getInstance().getReference();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Books").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                lstBook = new ArrayList<Book>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    newBook = child.getValue(Book.class);
                    bookID = child.getKey();
                    String thumb = newBook.getThumbnail();
                    if (thumb != null) {
                        StorageReference newStorage = myStorage.child("Bookpics").child(bookID);
                        newStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String myUri = uri.toString();
                                name = newBook.getTitle();
                                Book myBook = new Book();
                                myBook.setAuthor(null);
                                myBook.setConditions(null);
                                myBook.setLanguage(null);
                                myBook.setUser(null);
                                myBook.setEdition(null);
                                myBook.setTitle(name);
                                myBook.setID(bookID);
                                myBook.setThumbnail(myUri);
                                lstBook.add(myBook);
                                RecyclerView myrv = (RecyclerView) findViewById(R.id.recyclerview_id);
                                RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(BookMainActivity.this, lstBook);
                                //gridLayoutManager is an implementation of Recyclerview that lays out items in a grid.
                                //could be modified with linerlayoutmanager, etc
                                myrv.setLayoutManager(new GridLayoutManager(BookMainActivity.this, 3));
                                myrv.setAdapter(myAdapter);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });

                    } else {
                        name = newBook.getTitle();
                        Book myBook = new Book();
                        myBook.setAuthor(null);
                        myBook.setConditions(null);
                        myBook.setLanguage(null);
                        myBook.setUser(null);
                        myBook.setEdition(null);
                        myBook.setTitle(name);
                        myBook.setID(bookID);
                        lstBook.add(myBook);
                        RecyclerView myrv = (RecyclerView) findViewById(R.id.recyclerview_id);
                        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(BookMainActivity.this, lstBook);
                        //gridLayoutManager is an implementation of Recyclerview that lays out items in a grid.
                        //could be modified with linerlayoutmanager, etc
                        myrv.setLayoutManager(new GridLayoutManager(BookMainActivity.this, 3));
                        myrv.setAdapter(myAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }
}





