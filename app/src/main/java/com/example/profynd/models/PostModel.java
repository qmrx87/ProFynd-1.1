package com.example.profynd.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PostModel implements Serializable, Parcelable {
    private String publisher,Username, title,Formation_img ,body,postid,publisherPic,location;
    private Timestamp Date;
    private int Available_places,Price, reportsCount;
    private ArrayList<String> tags, demands;



    public PostModel(String publisher, String username, String title, String body,int price, String postid,String location, Timestamp date, String publisherPic, String formation_img, int placesCount, ArrayList<String> demands, ArrayList<String> tags) {
        this.publisher = publisher;
        Username = username;
        this.title = title;
        this.body = body;
        this.postid = postid;
        this.location=location;
        this.Price=price;
        Date = date;
        this.publisherPic = publisherPic;
        this.Available_places = placesCount;
        this.Formation_img = formation_img;
        this.demands=demands;
        this.tags = tags;
    }

    public PostModel (String postid){
        this.postid=postid;
    }


    public PostModel( String publisher, String username, String title, String body, String postid, Timestamp date, String publisherPic, String answerBy, int demandsCount, int answersCount, int reportsCount, ArrayList<String> tags, ArrayList<String> demands) {

        this.publisher = publisher;
        Username = username;
        this.title = title;
        this.body = body;
        this.postid = postid;
        Date = date;
        this.publisherPic = publisherPic;
        this.reportsCount = reportsCount;
        this.tags = tags;
        this.demands = demands;
    }

    public String ConvertDate() {
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy • HH:mm");
        return sfd.format(getDate().toDate());
    }

    protected PostModel(Parcel in) {

        publisher = in.readString();
        Username = in.readString();
        title = in.readString();
        body = in.readString();
        postid = in.readString();
        publisherPic = in.readString();
        Date = in.readParcelable(Timestamp.class.getClassLoader());
        reportsCount = in.readInt();
        tags = in.createStringArrayList();
        demands = in.createStringArrayList();
    }

    public static final Creator<PostModel> CREATOR = new Creator<PostModel>() {
        @Override
        public PostModel createFromParcel(Parcel in) {
            return new PostModel(in);
        }

        @Override
        public PostModel[] newArray(int size) {
            return new PostModel[size];
        }
    };

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void change(String text){
        title = text;
    }

    public String getPublisherPic() {
        return publisherPic;
    }

    public void setPublisherPic(String publisherPic) {
        this.publisherPic = publisherPic;
    }

    public PostModel() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getUsername() {
        return Username;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getPostid() {
        return postid;
    }

    public Timestamp getDate() {
        return Date;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(Timestamp date) {
        Date = date;
    }

    public int getReportsCount() {
        return reportsCount;
    }

    public void setReportsCount(int reportsCount) {
        this.reportsCount = reportsCount;
    }

    public ArrayList<String> getDemands() {
        return demands;
    }

    public void setDemands(ArrayList<String> demands) {
        this.demands = demands;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getFormation_img() {
        return Formation_img;
    }

    public void setFormation_img(String formation_img) {
        Formation_img = formation_img;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(publisher);
        parcel.writeString(Username);
        parcel.writeString(title);
        parcel.writeString(body);
        parcel.writeString(postid);
        parcel.writeString(publisherPic);
        parcel.writeParcelable(Date, i);
        parcel.writeInt(reportsCount);
        parcel.writeStringList(tags);
        parcel.writeStringList(demands);
    }
}
