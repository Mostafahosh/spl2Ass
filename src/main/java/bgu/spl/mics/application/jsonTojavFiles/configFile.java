package bgu.spl.mics.application.jsonTojavFiles;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

import java.util.List;

public class configFile {
    private Cameras cameras;
    private  LidarWorkers lidarWorkers;
    private String poseJsonFile;
    private int tickTime;
    private int duration;

//    public configFile(){
//        tickTime=0;
//        duration=0;
//    }

    public Cameras getCameras() {return cameras;}
    public LidarWorkers getLidarWorkers() {return lidarWorkers;}
    public String getPoseJsonFile() {return poseJsonFile;}
    public int getTickTime() {return tickTime;}
    public int getDuration() {return duration;}

    public void setCameras(Cameras cameras) {this.cameras=cameras;}
    public void setLidarWorkers(LidarWorkers lidarWorkers) {this.lidarWorkers=lidarWorkers;}
    public void setPoseJsonFile(String poseJsonFile) {
        this.poseJsonFile = poseJsonFile;
    }

    public void setTickTime(int tickTime) {this.tickTime = tickTime;}

    public void setDuration(int duration) {
        this.duration = duration;
    }

}
