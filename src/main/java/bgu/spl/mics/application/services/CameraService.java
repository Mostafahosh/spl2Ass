package bgu.spl.mics.application.services;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.JavaToJson.convertJavaCrash;
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
    /// //fields/////
    private Camera camera;
    private int tick;
    int time = 0;
    private CountDownLatch latch;
    private CyclicBarrier barrier;
    private int duration;
    int counter = 0;
    StampedDetectedObjects lastObj;
    ////////////////

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera, int tick, CountDownLatch latch , CyclicBarrier barrier , int duration) {
        super("camera " + camera.get_id());
        this.camera = camera;
        this.tick = tick;
        this.latch = latch;
        this.barrier = barrier;
        this.duration = duration;

    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        System.out.println("camera" + camera.get_id() + " is initialized with frequency = " + camera.get_frequency());

        subscribeBroadcast(TickBroadcast.class, callback -> {


            //if no other service is crashed
            if (!GlobalCrashed.getInstance().getCrahs()){
            List<StampedDetectedObjects> lst = camera.getList();

            //sends detected event at tick + camera frequency
                for (StampedDetectedObjects stampObj : lst) {
                    if (GlobalTime.getInstance().getGlobalTime() == stampObj.getTime() + camera.get_frequency()) {


                        //insure no error in the camera
                        List<DetectedObject> objectsList = stampObj.getDetectedObjects();
                        for (DetectedObject o : objectsList){

                            //in case of an ERROR
                            if (o.getId().equals("ERROR")){
                                convertJavaCrash.getInstance().setError("Camera disconnected");
                                convertJavaCrash.getInstance().setFaultySensor(camera.getCamera_key());
                                convertJavaCrash.getInstance().setLastCamerasFrame(lastObj);
                                GlobalCrashed.getInstance().setCrash(); //tell other sensors there is a crash!
                                sendBroadcast(new CrashedBroadcast());
                                terminate();
                                return; //get out of the functions
                            }
                        }

                        //no ERROR in the camera
                        counter += 1;
                        DetectObjectEvent event = new DetectObjectEvent(stampObj , camera.get_frequency());

                        //update the StatisticalFolder by num of DetectedObjects
                         StatisticalFolder.getInstance().incrementNumberOfDetectedObjects(stampObj.getSize());

                        //maintain the last object the camera detected
                        lastObj = stampObj;

                        sendEvent(event);
                        System.out.println(this.getName() + " sends DetectObjectEvent at time: " + GlobalTime.getInstance().getGlobalTime() + " with stamped objects: ");
                        stampObj.printList();
                        break;
                    }
                }
            }
            //if other sensor crashed - update last object detected by the camera
            else { //maybe not necessary - underStand why not going here or yes indeed interring the else
                System.out.println("im in the else CameraCrash");
                convertJavaCrash.getInstance().setLastCamerasFrame(lastObj);
                terminate();}
            }
        );

        subscribeBroadcast(TerminatedBroadcast.class, callback -> {
            System.out.println("im in the CrashedTick camera Crash");
            terminate();
                }
        );

        subscribeBroadcast(CrashedBroadcast.class, callback -> {
            //if other sensor crashed - update last object detected by the camera
            convertJavaCrash.getInstance().setLastCamerasFrame(lastObj);
            terminate();
        });


    }
}
