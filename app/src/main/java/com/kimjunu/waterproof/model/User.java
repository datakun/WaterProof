package com.kimjunu.waterproof.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    public String uid;
    public String username;
    public String photoURL;
    public long score;
    public int archiveTime;
    public int archiveDepth;
    public int archiveScore;
    public int coin;
    public Map<String, Integer> inventory = new HashMap<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String username, String photoURL) {
        this.uid = uid;
        this.username = username;
        this.photoURL = photoURL;
        this.score = (long) (Math.random() * 10000);
    }
}