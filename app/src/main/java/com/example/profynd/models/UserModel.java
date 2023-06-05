package com.example.profynd.models;

import java.io.Serializable;
import java.util.ArrayList;

public class UserModel implements Serializable  {
    private String Email;
    private ArrayList<String> feed;
    private String location;
    private String Mobile;
    private String Name;
    private String profilePictureUrl;
    private int reputation;
    private String type;
    private String Uid;
    private String Username;
    private String bio;
    private boolean isAdmin;

    public UserModel() {
        // Empty constructor required for Firebase
    }

    public UserModel(String email, ArrayList<String> feed, String location, String mobile, String name, String profilePictureUrl, int reputation, String type, String uid, String username,String bio, boolean isAdmin) {
        this.Email = email;
        this.feed = feed;
        this.location = location;
        this.Mobile = mobile;
        this.Name = name;
        this.profilePictureUrl = profilePictureUrl;
        this.reputation = reputation;
        this.type = type;
        this.Uid = uid;
        this.Username = username;
        this.bio=bio;
        this.isAdmin = isAdmin;
    }
    public UserModel(String name, String username, String profilePictureUrl, String uid, String bio) {
        Name = name;
        Username = username;
        this.profilePictureUrl = profilePictureUrl;
        this.Uid = uid;
        this.bio = bio;
    }
    // Getters and setters
    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public ArrayList<String> getFeed() {
        return feed;
    }

    public void setFeed(ArrayList<String> feed) {
        this.feed = feed;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        this.Mobile = mobile;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return Uid;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setUid(String uid) {
        this.Uid = uid;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}