package bgu.spl.mics.application.jsonToJava;

import bgu.spl.mics.application.objects.LiDarWorkerTracker;

import java.util.ArrayList;
import java.util.List;

public class LidarWorkers {
    private List<LiDarWorkerTracker> LidarConfigurations;
    private String lidars_data_path;

    public LidarWorkers(){
        LidarConfigurations =new ArrayList<>();
        lidars_data_path ="";
    }

    // Getters and setters
    public List<LiDarWorkerTracker> getLidarConfigurations() { return LidarConfigurations; }
    public void setLidarConfigurations(List<LiDarWorkerTracker> lidarConfigurations) { this.LidarConfigurations = lidarConfigurations; }

    public String getLidars_data_path() { return lidars_data_path; }
    public void setLidars_data_path(String lidars_data_path) { this.lidars_data_path = lidars_data_path; }
}
