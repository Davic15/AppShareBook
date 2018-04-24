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
    String name,URL;
    Uri myuri;
    StorageReference myStorage = FirebaseStorage.getInstance().getReference();
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_book_recyclerview);
        // getImage();
        // String imageUri = myuri.toString();


        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Books").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                lstBook = new ArrayList<Book>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Book newBook = child.getValue(Book.class);
                    Book myBook = new Book();
                    String name = newBook.getTitle();
                    String bookID = child.getKey();
                    String URL = newBook.getThumbnail();
                    myBook.setTitle(name);
                    myBook.setID(bookID);
                    myBook.setThumbnail(URL);
                    myBook.setCategory("horror");
                    myBook.setDescription("ok");
                    lstBook.add(myBook);
                    //lstBook.add(new Book("Bigger better ideas","Categorie Book","Description book", "https://firebasestorage.googleapis.com/v0/b/sharebooks-acb77.appspot.com/o/1.jpg?alt=media&token=a5844cfc-14f2-46f5-b0e1-978d70958e9e",bookID));

                    RecyclerView myrv = (RecyclerView) findViewById(R.id.recyclerview_id);
                    RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(BookMainActivity.this,lstBook);
                    //gridLayoutManager is an implementation of Recyclerview that lays out items in a grid.
                    //could be modified with linerlayoutmanager, etc
                    myrv.setLayoutManager(new GridLayoutManager(BookMainActivity.this,3));
                    myrv.setAdapter(myAdapter);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });


       // Toast.makeText(BookMainActivity.this, myuri.toString(), Toast.LENGTH_LONG).show();


        //lstBook.add(new Book("Bigger better ideas","Categorie Book","Description book", URL));

       // lstBook.add(new Book("Bigger better ideas","Categorie Book","Description book", "https://firebasestorage.googleapis.com/v0/b/sharebooks-acb77.appspot.com/o/1.jpg?alt=media&token=a5844cfc-14f2-46f5-b0e1-978d70958e9e"));

        // lstBook.add(new Book("Bigger better ideas","Categorie Book","Description book", "https://firebasestorage.googleapis.com/v0/b/sharebooks-acb77.appspot.com/o/1.jpg?alt=media&token=a5844cfc-14f2-46f5-b0e1-978d70958e9e"));


        //here i am filling with data and pictures the layout
        //now is static
        /*lstBook = new ArrayList<>();
        lstBook.add(new Book("Bigger better ideas","Categorie Book","Description book",R.drawable.p_one));
        lstBook.add(new Book("Portada para Libros","Categorie Book","Description book",R.drawable.p_two));
        lstBook.add(new Book("Kiosko","Categorie Book","Description book",R.drawable.p_three));
        lstBook.add(new Book("Alicia","Categorie Book","Description book",R.drawable.p_four));
        lstBook.add(new Book("My Lucky Little Friday","Categorie Book","Description book",R.drawable.p_five));
        lstBook.add(new Book("The investigation","Categorie Book","Description book",R.drawable.p_six));
        lstBook.add(new Book("Bigger better ideas","Categorie Book","Description book",R.drawable.p_one));
        lstBook.add(new Book("Portada para Libros","Categorie Book","Description book",R.drawable.p_two));
        lstBook.add(new Book("Kiosko","Categorie Book","Description book",R.drawable.p_three));
        lstBook.add(new Book("Alicia","Categorie Book","Description book",R.drawable.p_four));
        lstBook.add(new Book("My Lucky Little Friday","Categorie Book","Description book",R.drawable.p_five));
        lstBook.add(new Book("The investigation","Categorie Book","Description book",R.drawable.p_six));
        lstBook.add(new Book("Bigger better ideas","Categorie Book","Description book",R.drawable.p_one));
        lstBook.add(new Book("Portada para Libros","Categorie Book","Description book",R.drawable.p_two));
        lstBook.add(new Book("Kiosko","Categorie Book","Description book",R.drawable.p_three));
        */



    }
/*
    @Override
    protected void onStart() {
        super.onStart();


    }
/*
    public void getImage(){
        StorageReference newStorage = myStorage.child("picture").child("pic_uno.jpg");
        newStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                myuri = uri;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e ("ERROR","fail");
            }
        });


    }
*/
}

