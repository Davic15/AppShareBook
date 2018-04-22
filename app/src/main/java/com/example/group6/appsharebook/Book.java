package com.example.group6.appsharebook;

import android.net.Uri;

import com.google.firebase.storage.StorageReference;

import java.net.URL;

public class Book {

    private String Title;
    private String Category;
    private String Description;
    //private int Thumbnail;
    private String Thumbnail;
/*
    public Book (String title, String category, String description, int thumbnail){
        Title = title;
        Category = category;
        Description = description;
        Thumbnail = thumbnail;
    }
*/
    public Book(String title, String category, String description, String thumbnail) {
        Title = title;
        Category = category;
        Description = description;
        Thumbnail = thumbnail;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }


}
