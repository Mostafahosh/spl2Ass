package bgu.spl.mics.application.services;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.Broadcasts.CrashedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TerminatedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Messages.Events.DetectObjectEvent;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;

import java.util.List;


/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    /////fields/////
    private Camera camera;
    private int tick;
    ////////////////

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera , int tick) {
        super("camera " + camera.get_id());
        this.camera = camera;
        this.tick = tick;

    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
    subscribeBroadcast(TickBroadcast.class, callback -> {

        //should return all objects detected at time tick - should build this from the jsonFile
        StampedDetectedObjects stampObj = camera.get(tick);
        DetectObjectEvent event = new DetectObjectEvent(stampObj , tick);
        sendEvent(event);
    });

    subscribeBroadcast(TerminatedBroadcast.class, callback ->{
        terminate();
    }
    );

    subscribeBroadcast(CrashedBroadcast.class, callback ->{
        //error
    }
    );


    }

}
