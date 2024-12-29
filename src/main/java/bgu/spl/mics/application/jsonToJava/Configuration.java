package bgu.spl.mics.application.jsonToJava;

public class Configuration {
    private Cameras Cameras;
    private LidarWorkers LiDarWorkers;
    private String poseJsonFile;
    private int TickTime;
    private int Duration;

    // Getters and setters
    public Cameras getCameras() { return Cameras; }
    public void setCameras(Cameras cameras) { this.Cameras = cameras; }

    public LidarWorkers getLidarWorkers() { return LiDarWorkers; }
    public void setLidarWorkers(LidarWorkers lidarWorkers) { this.LiDarWorkers = lidarWorkers; }

    public String getPoseJsonFile() { return poseJsonFile; }
    public void setPoseJsonFile(String poseJsonFile) { this.poseJsonFile = poseJsonFile; }

    public int getTickTime() { return TickTime; }
    public void setTickTime(int tickTime) { this.TickTime = tickTime; }

    public int getDuration() { return Duration; }
    public void setDuration(int duration) { this.Duration = duration; }
}
