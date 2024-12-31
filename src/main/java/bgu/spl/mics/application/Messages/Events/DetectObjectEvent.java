package bgu.spl.mics.application.Messages.Events;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.util.List;

public class DetectObjectEvent implements Event<DetectedObject> {

    /////fields/////
    private StampedDetectedObjects obj;
    private String id;
    private int time; //maybe not necessary
    /////////////////

    public DetectObjectEvent(StampedDetectedObjects obj , int time) {
        this.obj = obj;
        this.time = time;

    }

public int getTime() {return time;}

    public String getId() {return id;}
    public StampedDetectedObjects getObj(int time) {return obj;}
    //input time just to make it more clear when calling the function



}
