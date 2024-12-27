package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.FusionSlamService;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    // Singleton instance holder
    private static class FusionSlamHolder {
        private static FusionSlam instance=new FusionSlam();
    }
    private LandMark[] landmarks;
    private List<Pose> poses;

    private FusionSlam(){
        landmarks=new LandMark[1];//how many??
        poses=new ArrayList<>();
    }
    public static FusionSlam getInstance(){
        return FusionSlamHolder.instance;
    }

}
