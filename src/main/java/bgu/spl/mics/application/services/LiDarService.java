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

    LiDarWorkerTracker lidar;
    private int tick;

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("Lidar" + LiDarWorkerTracker.getId());
        this.lidar = LiDarWorkerTracker;
        tick=1;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeEvent(DetectObjectEvent.class , callback ->{
            StampedDetectedObjects objects = callback.getObj();
            List<DetectedObject> list = objects.getDetectedObjects();
            lidar.clearList();
            for (DetectedObject o : list) {
                StampedCloudPoints points = LiDarDataBase.getObject(o.getId());

                TrackedObject trackedObject = new TrackedObject(points.getId(), points.getTime(), o.getDescription());
                List<CloudPoint> cloudPoints = points.getCloudPoints();
                for (CloudPoint p : cloudPoints) {
                    trackedObject.addCoordinate(p);
                }
                lidar.add(trackedObject);
            }
        });


        subscribeBroadcast(TickBroadcast.class, callback ->{
                    List<TrackedObject> trackedObjectList=lidar.getList();
                    List<TrackedObject> trackedObjectList2=new ArrayList<>();
                    for(TrackedObject o:trackedObjectList)
                    {
                        if(o.getTime()+lidar.getFrequency()==tick){
                            trackedObjectList2.add(o);
                        }

                    }
                    sendEvent(new TrackedObjectsEvent(trackedObjectList2));
                    tick++;

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
