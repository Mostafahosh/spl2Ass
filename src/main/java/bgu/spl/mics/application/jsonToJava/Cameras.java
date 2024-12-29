package bgu.spl.mics.application.jsonToJava;

import bgu.spl.mics.application.objects.Camera;

import java.util.ArrayList;
import java.util.List;

public class Cameras {
    private List<Camera> CamerasConfigurations;
    private String camera_datas_path;
    public Cameras(){
        CamerasConfigurations=new ArrayList<>();
        camera_datas_path="";
    }
    // Getters and setters
    public List<Camera> getCamerasConfigurations() { return CamerasConfigurations; }
    public void setCamerasConfigurations(List<Camera> camerasConfigurations) { this.CamerasConfigurations = camerasConfigurations; }

    public String getCamera_datas_path() { return camera_datas_path; }
    public void setCamera_datas_path(String camera_datas_path) { this.camera_datas_path = camera_datas_path; }
}
