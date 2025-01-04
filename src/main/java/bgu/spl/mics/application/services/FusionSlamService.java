package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.JavaToJson.convertJavaCrash;
import bgu.spl.mics.application.JavaToJson.Statistics;
import bgu.spl.mics.application.Messages.Broadcasts.CrashedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TerminatedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Messages.Events.PoseEvent;
import bgu.spl.mics.application.Messages.Events.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

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
    private CountDownLatch latch;
    private CyclicBarrier barrier;
    private int duration;

    ///////////////

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam , int time , CountDownLatch latch ,CyclicBarrier barrier , int duration) {
        super("FusionSlam");
        this.fusionSlam =fusionSlam;
        this.tick = time;
        this.latch = latch;
        this.barrier = barrier;
        this.duration = duration;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        System.out.println("FusionSlam is initialized");


        subscribeEvent(TrackedObjectsEvent.class, event -> {

            //insures no other service is crashed
            if (!GlobalCrashed.getInstance().getCrahs()){



                List<TrackedObject> trackedObjects = event.getTrackedObjects();
            //System.out.println("TrackedObjects size :"+trackedObjects.size());
            for (TrackedObject obj : trackedObjects) {

                Pose pose = fusionSlam.getPose(event.getTime());
                //System.out.println("the pose of the robot at time: " + event.getTime() + " & pose: " + pose.toString());

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
                        StatisticalFolder.getInstance().incrementNumberOfLandmarks();

                        //System.out.println("landMArk " + landMark.getId() + " created for the first Time! with Coordinates: " + "(List<LandMArk> size = " + fusionSlam.getLandMarks().size() + ")");
                        if (landMark.getList().size() > 0){
                        //System.out.println("landMark " + landMark.getId() +  " = " + landMark.getList().get(0));
                        }

                    }


            else{ //case2 - object was detected before - we do average
                LandMark landMark = FusionSlam.getInstance().getLandMArk(obj.getId());
                List<CloudPoint> gPoints = landMark.getList();

                for (int i = 0 ; i < gPoints.size() ; i +=1){

                    double newX = averageX(gPoints.get(i).getX() , globalPointsList.get(i).getX());
                    double newY = averageY(gPoints.get(i).getY() , globalPointsList.get(i).getY());

                    gPoints.get(i).setX(newX);
                    gPoints.get(i).setY(newY);
                }
                System.out.println("landMArk " + landMark.getId() + " was createdBefore , it's new Coordinates is: " + "(List<LandMArk> size = " + fusionSlam.getLandMarks().size() + ")");
                landMark.printLandMArkCoordinates();
            }


            }

//            complete(event,true);
//            try {
//                System.out.println(this.getName() + " is waiting at the barrier...");
//                barrier.await();
//                System.out.println("Fusion Done waiting!");
//            }
//            catch (Exception e) {
//                System.out.println(this.getName() + " has passed the barrier!");
//            }
        }
            else {
                Statistics s = new Statistics();
                s.setStatisticalFolder(StatisticalFolder.getInstance());
                s.setLandMarks(fusionSlam.getLandMarks());
                convertJavaCrash.getInstance().setStatistics(s);
                terminate();}});



        subscribeEvent(PoseEvent.class, event ->{
            //should be synchronized
            Pose pose = event.getPose();
            fusionSlam.addPose(pose);
            complete(event,true);
            //barrier.reset();
            }
        );

        subscribeBroadcast(TerminatedBroadcast.class, callback ->{
            terminate();
                }
        );

        subscribeBroadcast(CrashedBroadcast.class, callback ->{
            Statistics s = new Statistics();
            s.setStatisticalFolder(StatisticalFolder.getInstance());
            s.setLandMarks(fusionSlam.getLandMarks());
            convertJavaCrash.getInstance().setStatistics(s);
            terminate();


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
