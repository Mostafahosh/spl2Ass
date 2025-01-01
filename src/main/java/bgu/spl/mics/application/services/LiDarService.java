package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.Broadcasts.CrashedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TerminatedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Messages.Events.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.Messages.Events.DetectObjectEvent;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;


/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {

    /////fields/////
    LiDarWorkerTracker lidar;
    private int time;
    private int tick;
    private CountDownLatch latch;
    ////////////////

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker , int tick , CountDownLatch latch) {
        super("Lidar" + LiDarWorkerTracker.getId());
        this.lidar = LiDarWorkerTracker;
        time = 0;
        this.tick=tick;
        this.latch = latch;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        //handling DetectObjectEvent
        subscribeEvent(DetectObjectEvent.class , event -> {

            //case1 - object was never detected before
//           if (lidar.isObjectDetected(event.getId())) {

                //this two lines should have the same tickTime
                StampedDetectedObjects objects = event.getObj(GlobalTime.getInstance().getGlobalTime());

                System.out.println("im " + this.getName() + "with stamped: ");
                objects.printList();

                List<DetectedObject> list = objects.getDetectedObjects();

                //the last objects the lidar tracked
                lidar.clearList();

                for (DetectedObject o : list) {

                    //returns a stampedDetectedObject matching the ID object
                    //points & detected objects "is the same"
                    StampedCloudPoints points = LiDarDataBase.getObject(o.getId());

                    TrackedObject trackedObject = new TrackedObject(o.getId(), GlobalTime.getInstance().getGlobalTime(), o.getDescription());

                    List<CloudPoint> cloudPoints = points.getCloudPoints(); //points of the object
                    for (CloudPoint p : cloudPoints) {
                        trackedObject.addCoordinate(p);
                    }
                    lidar.add(trackedObject);

                    //for each lidar that detected an object , fusion slam should know that object was detected
                    FusionSlam.getInstance().addTrackedObj(trackedObject);
                }
                complete(event,true);
//            }
            //case2 - object was detected before - we do average
            //else {} - should not be here , should be when doing landMark in the fusionSlam
        });



        subscribeBroadcast(TickBroadcast.class, callback -> {
                    //time += tick; //again is thick++ needs to be here , and how we maintain sync of all times ?!

                    List<TrackedObject> trackedObjectList = lidar.getList();

                        System.out.println("myListSize: " + trackedObjectList.size());
                        List<TrackedObject> sendObjects = new ArrayList<>();



            //should we remove the object for efficiency ?
            for (TrackedObject o : trackedObjectList) {
                System.out.println("o.getTime: " + o.getTime());
                System.out.println("time = " + GlobalTime.getInstance().getGlobalTime());
                if (o.getTime() + lidar.getFrequency() == GlobalTime.getInstance().getGlobalTime()) {
                                sendObjects.add(o);
                                System.out.println("");
                }
            }
            sendEvent(new TrackedObjectsEvent(sendObjects));
//            try {
//                latch.await();
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
            latch.countDown();
        });

        subscribeBroadcast(TerminatedBroadcast.class, callback ->{
            System.out.println("it's time for " + this.getName() + " Service to subscribe to TerminateBroadCast");
            terminate();
                }
        );

        subscribeBroadcast(CrashedBroadcast.class, callback ->{
                    //error
                }
        );
    }


}
