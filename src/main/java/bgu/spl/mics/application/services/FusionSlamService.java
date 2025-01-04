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
import java.util.Iterator;
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

    private int duration;

    ///////////////

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam , int time  , int duration) {
        super("FusionSlam");
        this.fusionSlam =fusionSlam;
        this.tick = time;

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
            if (!GlobalCrashed.getInstance().getCrahs()) {

                fusionSlam.addTrackedObjectEvent(event);
            }});


//                List<TrackedObject> trackedObjects = event.getTrackedObjects();
//            //System.out.println("TrackedObjects size :"+trackedObjects.size());
//            for (TrackedObject obj : trackedObjects) {
//                //System.out.println("Tracked obj time = " + obj.getTime());
//
//                System.out.println("TrackedObject is: " + obj.getId() + " with time: " + obj.getTime());
//                System.out.println("ListPoses.Size() = " + fusionSlam.getPoses().size());
//
//
//                Pose pose = fusionSlam.getPose(obj.getTime());
//                while (pose == null){
//                    if (GlobalTime.getInstance().getGlobalTime() == duration){
//                        System.out.println("fusionSlam is Terminated");
//                        return;
//                    }
//
//                    //Thread.sleep(50);
//                     pose = fusionSlam.getPose(obj.getTime());
//                }
//                //System.out.println("the pose of the robot at time: " + event.getTime() + " & pose: " + pose.toString());
//
//                List<CloudPoint> globalPointsList = new ArrayList<>();
//
//                for (CloudPoint localPoint : obj.getCoordinates()) {
//                    double x = localPoint.getX();
//                    double y = localPoint.getY();
//                    CloudPoint globalPoint = fusionSlam.mathCalc(x, y, pose);
//                    globalPointsList.add(globalPoint);
//                }
//
//                    //case1 - object was never detected before - checks if he is a landMark
//                    if (!FusionSlam.getInstance().isObjectAvailable(obj)){
//                        LandMark landMark = new LandMark(obj.getId(), obj.getDescription(), globalPointsList);
//                        fusionSlam.addLandMark(landMark);
//                        StatisticalFolder.getInstance().incrementNumberOfLandmarks();
//
//                        //System.out.println("landMArk " + landMark.getId() + " created for the first Time! with Coordinates: " + "(List<LandMArk> size = " + fusionSlam.getLandMarks().size() + ")");
//                        if (landMark.getList().size() > 0){
//                        //System.out.println("landMark " + landMark.getId() +  " = " + landMark.getList().get(0));
//                        }
//
//                    }
//
//
//            else{ //case2 - object was detected before - we do average
//                LandMark landMark = FusionSlam.getInstance().getLandMArk(obj.getId());
//                List<CloudPoint> gPoints = landMark.getList();
//
//                for (int i = 0 ; i < gPoints.size() ; i +=1){
//
//                    double newX = averageX(gPoints.get(i).getX() , globalPointsList.get(i).getX());
//                    double newY = averageY(gPoints.get(i).getY() , globalPointsList.get(i).getY());
//
//                    gPoints.get(i).setX(newX);
//                    gPoints.get(i).setY(newY);
//                }
//                System.out.println("landMArk " + landMark.getId() + " was createdBefore , it's new Coordinates is: " + "(List<LandMArk> size = " + fusionSlam.getLandMarks().size() + ")");
//                landMark.printLandMArkCoordinates();
//            }
//
//
//            }
//
//        }
//            else {
//                Statistics s = new Statistics();
//                s.setStatisticalFolder(StatisticalFolder.getInstance());
//                s.setLandMarks(fusionSlam.getLandMarks());
//                convertJavaCrash.getInstance().setStatistics(s);
//                terminate();}});



        subscribeEvent(PoseEvent.class, event -> {
                    if (!GlobalCrashed.getInstance().getCrahs()) {


                        Pose pose = event.getPose();
                        fusionSlam.addPose(pose);

                        List<TrackedObjectsEvent> lst = fusionSlam.getTrackedEventsList();

                        Iterator<TrackedObjectsEvent> iterator = lst.iterator();


                        while (iterator.hasNext()) {

                            TrackedObjectsEvent t = iterator.next(); // Get the next event

                            System.out.println("Tracked.time() = " + t.getTime() + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                            int time = t.getTime();

                            if (fusionSlam.isAvailablePose(time)) {
                                List<TrackedObject> trackedObjects = t.getTrackedObjects();
                                for (TrackedObject obj : trackedObjects) {
                                    Pose poseObj = fusionSlam.getPose(obj.getTime());
                                    List<CloudPoint> globalPointsList = new ArrayList<>();

                                    for (CloudPoint localPoint : obj.getCoordinates()) {
                                        double x = localPoint.getX();
                                        double y = localPoint.getY();
                                        CloudPoint globalPoint = fusionSlam.mathCalc(x, y, poseObj);
                                        globalPointsList.add(globalPoint);
                                    }

                                    //case1 - object was never detected before - checks if he is a landMark
                                    if (!FusionSlam.getInstance().isObjectAvailable(obj)){
                                        LandMark landMark = new LandMark(obj.getId(), obj.getDescription(), globalPointsList);
                                        fusionSlam.addLandMark(landMark);
                                        StatisticalFolder.getInstance().incrementNumberOfLandmarks();

                                        //System.out.println("landMArk " + landMark.getId() + " created for the first Time! with Coordinates: " + "(List<LandMArk> size = " + fusionSlam.getLandMarks().size() + ")");
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

                                    }
                                    complete(event, true);
                                }

                                // Safely remove the event from the list after processing
                                iterator.remove();

                            }



                        }



            } else {
                Statistics s = new Statistics();
                s.setStatisticalFolder(StatisticalFolder.getInstance());
                s.setLandMarks(fusionSlam.getLandMarks());
                convertJavaCrash.getInstance().setStatistics(s);
                terminate();
            }
        });


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





//subscribeEvent(PoseEvent.class, event ->{
//Pose pose = event.getPose();
//            fusionSlam.addPose(pose);
//complete(event,true);
//
//            }
//                    );


