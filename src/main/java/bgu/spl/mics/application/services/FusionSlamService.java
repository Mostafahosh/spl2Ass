package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.Broadcasts.CrashedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TerminatedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Messages.Events.DetectObjectEvent;
import bgu.spl.mics.application.Messages.Events.PoseEvent;
import bgu.spl.mics.application.Messages.Events.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.*;

import java.util.ArrayList;
import java.util.List;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private FusionSlam fusionSlam;
    private int tick;
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlam");
        this.fusionSlam =fusionSlam;
        tick=1;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, callback ->{

                tick++;
                }

        );

        subscribeEvent(TrackedObjectsEvent.class, callback ->{
                List<TrackedObject> trackedObjects = callback.getTrackedObjects();
                Pose pose = fusionSlam.getPose(tick);
                for(TrackedObject o:trackedObjects){

                    List<CloudPoint> globalPointsList = new ArrayList<>();

                    for (CloudPoint localpoint : o.getCoordinates()){
                        double x = localpoint.getX();
                        double y = localpoint.getY();


                        CloudPoint globalPoint = fusionSlam.mathCalc(x,y,pose);
                        globalPointsList.add(globalPoint);

                    }

                    LandMark landMark = new LandMark(o.getId() , o.getDescription() , globalPointsList);
                    fusionSlam.addLandMark(landMark);
                }
            }
        );

        subscribeEvent(PoseEvent.class, callback ->{
            //should be syyncronized
            Pose pose = callback.getPose();
            fusionSlam.addPose(pose);
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
