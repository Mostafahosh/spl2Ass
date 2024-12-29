package bgu.spl.mics.application.jsonTojavFiles;

import bgu.spl.mics.application.objects.LiDarWorkerTracker;

import java.util.List;

public class LidarWorkers {
    private List<LiDarWorkerTracker> LidarConfigurations;
    private String lidars_data_path;

    public List<LiDarWorkerTracker> getLidarConfigurations() {
        return LidarConfigurations;
    }

    public void setLidarConfigurations(List<LiDarWorkerTracker> lidarConfigurations) {
        LidarConfigurations = lidarConfigurations;
    }

    public String getLidars_data_path() {
        return lidars_data_path;
    }

    public void setLidars_data_path(String lidarsDataPath) {
        this.lidars_data_path = lidarsDataPath;
    }
}
