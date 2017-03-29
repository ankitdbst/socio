package com.example.ankit.socio.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public final class User {
    public String uid;
    public String fullName;
    public String email;
    public String gender;
    public String photoURL;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String fullName, String email, String gender, String photoURL) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
        this.photoURL = photoURL;
    }
}
