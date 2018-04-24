package com.example.group6.appsharebook;

import android.net.Uri;

import com.google.firebase.storage.StorageReference;

import java.net.URL;

public class Book {

    private String Title;
    //private int Thumbnail;
    private String ID;
    private String Thumbnail;
    private String Author;
    private String Conditions;
    private String Language;
    private String Edition;

    public Book() {};
/*
    public Book (String title, String category, String description, int thumbnail){
        Title = title;
        Category = category;
        Description = description;
        Thumbnail = thumbnail;
    }
*/
    public Book(String title, String thumbnail,String id,String author,String conditions,String language,String edition) {
        Title = title;
        Thumbnail = thumbnail;

        ID = id;
        Author = author;
        Conditions = conditions;
        Language = language;
        Edition = edition;
    }

    public String getTitle() {
        return Title;
    }

    public String getID () { return ID; }

    public void setID (String id) {
        ID = id;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }


    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getConditions() {
        return Conditions;
    }

    public void setConditions(String conditions) {
        Conditions = conditions;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }

    public String getEdition() {
        return Edition;
    }

    public void setEdition(String edition) {
        Edition = edition;
    }
}
