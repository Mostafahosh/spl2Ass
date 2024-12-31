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

import java.util.ArrayList;
import java.util.List;


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
    private int tick;
    ////////////////

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker , int time) {
        super("Lidar" + LiDarWorkerTracker.getId());
        this.lidar = LiDarWorkerTracker;
        tick = time; //time not necessary starts from 1
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        //handling DetectObjectEvent
        subscribeEvent(DetectObjectEvent.class , callback -> {

            //case1 - object was never detected before
            if (lidar.isObjectDetected(callback.getId())) {
                //this two lines should have the same tickTime
                StampedDetectedObjects objects = callback.getObj(tick);
                List<DetectedObject> list = objects.getDetectedObjects();

                //lidar.clearList();

                for (DetectedObject o : list) {

                    //returns a stampedDetectedObject matching the ID object
                    //points & detected objects "is the same"
                    StampedCloudPoints points = LiDarDataBase.getObject(o.getId());

                    TrackedObject trackedObject = new TrackedObject(points.getId(), points.getTime(), o.getDescription());

                    List<CloudPoint> cloudPoints = points.getCloudPoints(); //points of the object
                    for (CloudPoint p : cloudPoints) {
                        trackedObject.addCoordinate(p);
                    }
                    lidar.add(trackedObject);

                    //for each lidar that detected an object , fusion slam should know that object was detected
                    FusionSlam.getInstance().addTrackedObj(trackedObject);
                }
            }
            //case2 - object was detected before - we do average
            //else {} - should not be here , should be when doing landMark in the fusionSlam
        });



        subscribeBroadcast(TickBroadcast.class, callback ->{

                    List<TrackedObject> trackedObjectList = lidar.getList();
                    List<TrackedObject> sendObjects = new ArrayList<>();

                    //should we remove the object for efficiency ?
                    for(TrackedObject o : trackedObjectList) {
                        if(o.getTime() + lidar.getFrequency() == tick){
                            sendObjects.add(o);}
                    }
                    sendEvent(new TrackedObjectsEvent(sendObjects));
                    tick++; //again is thick++ needs to be here , and how we maintain sync of all times ?!
                }
        );

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
