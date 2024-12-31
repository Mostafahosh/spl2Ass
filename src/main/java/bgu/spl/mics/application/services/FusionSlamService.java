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
    /////fields/////
    private FusionSlam fusionSlam;
    private int tick;
    ///////////////

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam , int time) {
        super("FusionSlam");
        this.fusionSlam =fusionSlam;
        this.tick = time;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, callback ->{
                //should wait until all data of all services is available
                tick++;
                }
        );


        subscribeEvent(TrackedObjectsEvent.class, callback -> {
            List<TrackedObject> trackedObjects = callback.getTrackedObjects();

            for (TrackedObject obj : trackedObjects) {

                Pose pose = fusionSlam.getPose(tick);

                List<CloudPoint> globalPointsList = new ArrayList<>();

                for (CloudPoint localPoint : obj.getCoordinates()) {
                    double x = localPoint.getX();
                    double y = localPoint.getY();
                    CloudPoint globalPoint = fusionSlam.mathCalc(x, y, pose);
                    globalPointsList.add(globalPoint);
                }

                    //case1 - object was never detected before - checks if he is a landMark
                    if (!FusionSlam.getInstance().isObjectAvailable(obj)){
                        LandMark landMark = new LandMark(obj.getId(), obj.getDescription(), globalPointsList);
                        fusionSlam.addLandMark(landMark);
                    }


            else{ //case2 - object was detected before - we do average
                LandMark landMark = FusionSlam.getInstance().getLandMArk(obj.getId());
                List<CloudPoint> gPoints = landMark.getList();

                for (int i = 0 ; i < gPoints.size() ; i +=1){

                    double newX = averageX(gPoints.get(i).getX() , globalPointsList.get(i).getX());
                    double newY = averageY(gPoints.get(i).getY() , globalPointsList.get(i).getY());

                    gPoints.get(i).setX(newX);
                    gPoints.get(i).setY(newY);
                }}
            }
        });



        subscribeEvent(PoseEvent.class, callback ->{
            //should be synchronized
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

    public double averageX(double oldP , double newP){
        return (oldP + newP)/2;
    }

    public double averageY(double oldP , double newP){
        return (oldP + newP)/2;
    }
}
