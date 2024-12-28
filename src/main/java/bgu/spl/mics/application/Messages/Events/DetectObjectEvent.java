package bgu.spl.mics.application.Messages.Events;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

import java.util.List;

public class DetectObjectEvent implements Event<DetectedObject> {

    private StampedDetectedObjects obj;
    private String id;
    private List<DetectedObject> list;
    private int time;

    public DetectObjectEvent(StampedDetectedObjects obj , int time) {
        this.obj = obj;
        this.list = obj.getDetectedObjects();
        this.time = time;
    }

public int getTime() {return 5;}
    public List<DetectedObject> getList() {return list;}
    public String getId() {return id;}
    public StampedDetectedObjects getObj() {return obj;}


//    public DetectedObject getObj(String id , String description , int time) {
//
//    }

}
