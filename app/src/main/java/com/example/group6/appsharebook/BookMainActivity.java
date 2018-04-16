package com.example.group6.appsharebook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BookMainActivity extends AppCompatActivity {
    List<Book> lstBook;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_book_recyclerview);

        //here i am filling with data and pictures the layout
        //now is static
        lstBook = new ArrayList<>();
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

        RecyclerView myrv = (RecyclerView) findViewById(R.id.recyclerview_id);
        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(this,lstBook);
        //gridLayoutManager is an implementation of Recyclerview that lays out items in a grid.
        //could be modified with linerlayoutmanager, etc
        myrv.setLayoutManager(new GridLayoutManager(this,3));
        myrv.setAdapter(myAdapter);

    }
}
