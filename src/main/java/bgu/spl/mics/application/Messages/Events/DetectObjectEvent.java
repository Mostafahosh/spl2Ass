package bgu.spl.mics.application.Messages.Events;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarDataBase;

public class DetectObjectEvent implements Event<DetectedObject> {

    private DetectedObject obj;
    private String id;
    private String description;
    private int time;

    public DetectObjectEvent(DetectedObject obj , int time) {
        this.obj = obj;
        this.id = obj.getId();
        this.description = obj.getDescription();
        this.time = time;
    }

//    public DetectedObject getObj(String id , String description , int time) {
//
//    }

}
