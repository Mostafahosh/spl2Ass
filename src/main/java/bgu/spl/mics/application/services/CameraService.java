package bgu.spl.mics.application.services;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.Broadcasts.CrashedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TerminatedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Messages.Events.DetectObjectEvent;
import bgu.spl.mics.application.objects.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;


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
    int time = 0;
    private CountDownLatch latch;
    ////////////////

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera , int tick , CountDownLatch latch) {
        super("camera " + camera.get_id());
        this.camera = camera;
        this.tick = tick;
        this.latch = latch;

    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {

    subscribeBroadcast(TickBroadcast.class, callback -> {
        //System.out.println("im camera1 initialized");
//        camera.get(1).Print();
//        camera.get(2).Print();


        //should return all objects detected at time tick - should build this from the jsonFile
        //time += tick;
        System.out.println("the time is: " + GlobalTime.getInstance().getGlobalTime());
        StampedDetectedObjects stampObj = camera.get(GlobalTime.getInstance().getGlobalTime());

        if (!(stampObj == null)) {


            boolean s = stampObj == null;
            System.out.println("what is stampedList? -> " + s);

            DetectObjectEvent event = new DetectObjectEvent(stampObj, GlobalTime.getInstance().getGlobalTime());
            System.out.println("im " + this.getName() + " sends DetectObjectEvent");
            System.out.println("myStampedObject is: " + stampObj);
            sendEvent(event);


        }

        latch.countDown();



    });

    subscribeBroadcast(TerminatedBroadcast.class, callback ->{

                terminate();
    }
    );

    subscribeBroadcast(CrashedBroadcast.class, callback ->{
        //error
    });


    }

    public Camera getCamera(){return camera;}

}
