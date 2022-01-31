package com.example.gargianimalcare;


public class Image_Model {
    private String ComplainID;
    private String mName;
    private String mImageUrl;

    public Image_Model() {
        //empty constructor needed
    }
    public Image_Model(String c, String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        ComplainID=c;
        mName = name;
        mImageUrl = imageUrl;
    }


    public String getComplainID() {
        return ComplainID;
    }

    public void setComplainID(String complainID) {
        ComplainID = complainID;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }



}