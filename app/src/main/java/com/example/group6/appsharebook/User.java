package com.example.group6.appsharebook;

import android.net.Uri;

public class User {
    public String name;
    public String surname;
    public String email;
    public String userBio;


    public User () {};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserBio (String bio) { this.userBio = bio; }

    public String getUserBio () { return userBio; };

    public User (String name, String surname, String email,String userBio){
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.userBio = userBio;
    }
}
