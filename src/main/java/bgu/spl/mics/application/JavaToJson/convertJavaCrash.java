package bgu.spl.mics.application.JavaToJson;

import bgu.spl.mics.application.objects.*;

import java.util.List;

public  class convertJavaCrash {

    private static convertJavaCrash instance;

    public static convertJavaCrash getInstance(){
        if(instance==null){
            instance=new convertJavaCrash();
        }
        return instance;
    }



    private String error;
    private String faultySensor;
    private  StampedDetectedObjects lastCamerasFrame;
    private List<TrackedObject> lastLiDarWorkerTrackersFrame;
    private List<Pose> poses;
    private Statistics statistics;


    public String getError() {
        return error;
    }

    public String getFaultySensor() {
        return faultySensor;
    }

    public StampedDetectedObjects getLastCamerasFrame() {
        return lastCamerasFrame;
    }

    public List<TrackedObject> getLastLiDarWorkerTrackersFrame() {
        return lastLiDarWorkerTrackersFrame;
    }

    public List<Pose> getPoses() {
        return poses;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setFaultySensor(String faultySensor) {
        this.faultySensor = faultySensor;
    }

    public void setLastCamerasFrame( StampedDetectedObjects lastCamerasFrame) {
        this.lastCamerasFrame = lastCamerasFrame;
    }

    public void setLastLiDarWorkerTrackersFrame(List<TrackedObject> lastLiDarWorkerTrackersFrame) {
        this.lastLiDarWorkerTrackersFrame = lastLiDarWorkerTrackersFrame;
    }

    public void setPoses(List<Pose> poses) {
        this.poses = poses;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}
