package com.example.spontaniius.ui.find_event;

public class EventTile {
    private int mImageResource;
    private String description;
    private String distance_and_time_started;

    public EventTile(int imageResource, String text1, String text2) {
        mImageResource = imageResource;
        description = text1;
        distance_and_time_started = text2;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getDescription() {
        return description;
    }

    public String getDistance_and_time_started() {
        return distance_and_time_started;
    }
}