package bgu.spl.mics.application.objects;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */

public class GPSIMU {
    private int currentTick;
    private STATUS status;
    private List<Pose> list;

    public GPSIMU(int currentTick) {
        this.currentTick = currentTick;
        this.status = STATUS.UP;
        this.list = Collections.synchronizedList(new ArrayList<>());
    }

    public int getCurrentTick() {return currentTick;}
    public void add(Pose obj){list.add(obj);}
    public STATUS getStatus() {return status;}
    public void setStatus(STATUS status) {this.status = status;}


    public Pose findPose (int time){
        Pose pose;
        for (int i = 0; i < list.size(); i++) {
            if(time == list.get(i).getTime()){
                pose = list.get(i);
                return pose;
            }
        }
        return null; //should never return null
    }

    public void parseJson(){
                Gson gson = new Gson();

        String filePath = "example input/pose_data.json"; // Replace with the actual file path
        try (FileReader reader = new FileReader(filePath)){
            Type lst = new TypeToken<ArrayList<Pose>>(){}.getType();
            ArrayList<Pose> poses = gson.fromJson(reader, lst);

            for (Pose pose : poses) {
                System.out.println(pose);
                list.add(pose);
            }

        }    catch (IOException e) {
            e.printStackTrace();
        }
    }

}
