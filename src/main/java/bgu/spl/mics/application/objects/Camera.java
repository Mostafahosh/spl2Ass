package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public int get_id(){return id;}
    public void add(StampedDetectedObjects obj){list.add(obj);}
}
