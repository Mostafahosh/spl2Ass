package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private int time;
    private List<DetectedObject> detectedObjects;

    public StampedDetectedObjects(int time) {
        this.time = time;
        this.detectedObjects = Collections.synchronizedList(new ArrayList<>());
    }

    public int getTime() {return time;}
    public void addDetectedObject(DetectedObject obj) {detectedObjects.add(obj);}
    public DetectedObject getDetectedObject(int id) {return detectedObjects.get(id);}

    public List<DetectedObject> getDetectedObjects() {
        return detectedObjects;
    }

    public void Print(){
        System.out.println("detected objects at time " + time + " and objects: ");
        printList();
    }

    public void printList() {
        for (DetectedObject object : detectedObjects) {
            System.out.println(object);
        }
    }


}
