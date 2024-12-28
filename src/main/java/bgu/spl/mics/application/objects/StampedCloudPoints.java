package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private int time;
    private List<CloudPoint> cloudPoints;
    private String id;

    public StampedCloudPoints(int time , String id) {
        this.time = time;
        this.id = id;
        this.cloudPoints = Collections.synchronizedList(new ArrayList<>());
    }


    public int getTime() {return time;}
    public String getId() {return id;}
    public List<CloudPoint> getCloudPoints() {return cloudPoints;}
    public void addCloudPoint(CloudPoint point) {cloudPoints.add(point);}
    public CloudPoint getCloudPoint(int id) {return cloudPoints.get(id);}
    public String toString(){
        return "obj detected at time " + time + " with id " + id + " and cloud points " + cloudPoints;
    }
}
