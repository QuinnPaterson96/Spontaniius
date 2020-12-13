package com.example.spontaniius.ui.find_event;

import android.content.Context;

public class EventTile {
    private int mImageResource;
    private String description;
    private String title;

    private String distance;
    private String timeStarted="";
    private String location;
    private Context context;


    public EventTile(int imageResource, String text1, String text2, String text3, String text4, String eventlocation, Context context) {
        mImageResource = imageResource;
        title = text1;
        description = text2;
        distance=text3;
        timeStarted=text4;
        location=eventlocation;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDistance() {
        return distance;
    }

    public String getTime_started() {
        return timeStarted;
    }
    public String getLocation() {
        return location;
    }
    public Context getContext() {
        return context;
    }

}