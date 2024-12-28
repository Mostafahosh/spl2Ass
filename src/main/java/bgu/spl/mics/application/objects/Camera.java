package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.Messages.Events.DetectObjectEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    int id;
    int frequency;
    STATUS status;
    List<StampedDetectedObjects> list;


    public Camera(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        status = STATUS.UP;
        list = Collections.synchronizedList(new ArrayList<>());
    }

    public int get_id() {
        return id;
    }

    public int get_frequency() {
        return frequency;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public void clearList() {
        list.clear();
    }

    public void add(StampedDetectedObjects obj) {
        list.add(obj);
    }

    public StampedDetectedObjects get(int index) {
        return list.get(index);
    }

    public int numOfObjects() {
        return list.size();
    }



//    public List<DetectObjectEvent> detectObjects(int time) {
//        List<DetectObjectEvent> events = new ArrayList<>();
//        int timeAndFreq = time + frequency;
//        for (int i = 0; i < list.size(); i++) {
//            if (timeAndFreq == list.get(i).getTime()) {
//                for (DetectedObject obj : list.get(i).getDetectedObjects()) {
//                    DetectObjectEvent objEvent = new DetectObjectEvent(obj, time);
//                    events.add(objEvent);
//                }
//            }
//        }
//        return events;
//    }
}