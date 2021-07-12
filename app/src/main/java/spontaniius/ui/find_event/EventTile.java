package spontaniius.ui.find_event;

import android.content.Context;

import java.util.function.Function;

public class EventTile {
    private int mImageResource;
    private String description;
    private String title;

    private String distance;
    private String timeStarted="";
    private String location;
    private String eventId;
    private Context context;


    public EventTile(int imageResource, String text1, String text2, String text3, String text4, String eventlocation, String eventid, Context context) {
        mImageResource = imageResource;
        title = text1;
        description = text2;
        distance=text3;
        timeStarted=text4;
        location=eventlocation;
        eventId=eventid;
    }

    public EventTile(int imageResource, String text1, String text2, String text3, String text4, String eventlocation, String eventid, Function outsideFunction, Context context) {
        mImageResource = imageResource;
        title = text1;
        description = text2;
        distance=text3;
        timeStarted=text4;
        location=eventlocation;
        eventId=eventid;

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
    public String getEventId() {
        return eventId;
    }
    public Context getContext() {
        return context;
    }


}