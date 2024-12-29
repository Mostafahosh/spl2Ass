package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private String id;
    private int time;
    private String description;
    private List<CloudPoint> coordinates;

    public TrackedObject(String id, int time, String description) {
        this.id = id;
        this.time = time;
        this.description = description;
        this.coordinates = Collections.synchronizedList(new ArrayList<>());
    }

    public String getId() {return id;}
    public int getTime() {return time;}
    public String getDescription() {return description;}

    public CloudPoint getCoordinate(int x , int y) {
        for (CloudPoint point : coordinates) {
            if (point.getX() == x && point.getY() == y) {
                return point;
            }
        }
        return null;
    }

    public void addCoordinate(CloudPoint point) {coordinates.add(point);}
    public List<CloudPoint> getCoordinates() {return coordinates;}
}
