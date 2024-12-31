package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */

//represents a Lidar//
public class LiDarWorkerTracker {

    private int id;
    private int frequency;
    private STATUS status;
    private List<TrackedObject> lastTrackedObjects;

    LiDarWorkerTracker(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        status = STATUS.UP;
        lastTrackedObjects = Collections.synchronizedList(new ArrayList<>());
    }



    public int getId() {return id;}
    public void add(TrackedObject obj){lastTrackedObjects.add(obj);}
    public STATUS getStatus() {return status;}
    public void setStatus(STATUS status) {this.status = status;}
    public int getFrequency() {return frequency;}
    public void clearList() {lastTrackedObjects.clear();}
    public void remove(TrackedObject obj) {lastTrackedObjects.remove(obj);}
    public void remove(int index) {lastTrackedObjects.remove(index);}
    public List<TrackedObject> getList() {
        return lastTrackedObjects;
    }

    public boolean isObjectDetected(String id){
        for (TrackedObject obj : lastTrackedObjects){
            if (obj.getId() == id){return true;}
        }
        return false;
    }

    public void toStringL(){
        System.out.println("lidar: " + id);
        }





    }


