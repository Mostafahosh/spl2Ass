package bgu.spl.mics.application.jsonTojavFiles;

import bgu.spl.mics.application.objects.Camera;

import java.util.List;

public class Cameras {
    private List<Camera> CamerasConfigurations;
    private String camera_datas_path;

    public List<Camera> getCamerasConfigurations() {
        return CamerasConfigurations;
    }

    public void setCamerasConfigurations(List<Camera> camerasConfigurations) {
        CamerasConfigurations = camerasConfigurations;
    }

    public String getCamera_datas_path() {
        return camera_datas_path;
    }

    public void setCamera_datas_path(String cameraDatasPath) {
        this.camera_datas_path = cameraDatasPath;
    }
}
