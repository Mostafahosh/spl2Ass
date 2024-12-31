package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.FusionSlamService;

import java.util.ArrayList;
import java.util.Collections;
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
    private List<LandMark> landmarks;
    private List<Pose> poses;
    private List<TrackedObject> trackedObjects;

    public FusionSlam(){
        landmarks=Collections.synchronizedList(new ArrayList<>());
        poses= Collections.synchronizedList(new ArrayList<>());
    }
    public static FusionSlam getInstance(){
        return FusionSlamHolder.instance;
    }
    public void addLandMark(LandMark landMark){landmarks.add(landMark);}
    public void addPose(Pose pose){poses.add(pose);}
    public List<LandMark> getLandMarks(){return landmarks;}
    public List<Pose> getPoses(){return poses;}
    public List<TrackedObject> getObjects(){return trackedObjects;}
    public boolean isObjectAvailable(TrackedObject instance){
        for (LandMark obj : landmarks){
            if (obj.getId().equals(instance.getId())){return true;}
        }
        return false;
    }

    public LandMark getLandMArk(String id){
        for (LandMark LM : landmarks){
            if (LM.getId().equals(id)){return LM;}
        }
        return null;
    }
    public void addTrackedObj(TrackedObject obj){trackedObjects.add(obj);}
    public Pose getPose(int tick){for (Pose pose : poses) {if(pose.getTime() == tick){return pose;}}return null;}


    public CloudPoint mathCalc(double x , double y , Pose pose){
        double xLocal = x;
        double yLocal = y;
        double deltaDegree = pose.getYaw();
        double xGlobal;
        double yGlobal;
        double xRobot = pose.getX();
        double yRobot = pose.getY();
        double xRotated;
        double yRotated;

        double deltaRad = deltaDegree * (float) Math.PI / 180;
        System.out.println("deltaRad = " + deltaRad);
        System.out.println("____");

        double cos = Math.cos(deltaRad);
        System.out.println("cos = " + cos);
        double sin = Math.sin(deltaRad);
        System.out.println("sin = " + sin);

        xRotated = (cos*xLocal) - (sin*yLocal);
        yRotated = (sin*xLocal) + (cos*yLocal);

        xGlobal = xRotated + xRobot;
        System.out.println("xGlobal = " + xGlobal);
        yGlobal = yRotated + yRobot;
        System.out.println("yGlobal = " + yGlobal);

        CloudPoint globalPoint = new CloudPoint(xGlobal, yGlobal);
        return globalPoint;



    }


}
