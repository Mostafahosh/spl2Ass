package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.JavaToJson.convertJavaCrash;
import bgu.spl.mics.application.Messages.Broadcasts.CameraDoneBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.CrashedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.DoneBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TerminatedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Messages.Events.TrackedObjectsEvent;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.Messages.Events.DetectObjectEvent;

import javax.sound.midi.Soundbank;
import java.util.ArrayList;
import java.util.Iterator;
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
    private CyclicBarrier barrier;
    private int duration;
    private List<TrackedObject> lastObj;
    List<TrackedObjectsEvent> lst;
    private int counter;
    ////////////////

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker , int tick  , int duration ,int counter) {
        super("Lidar" + LiDarWorkerTracker.getId());
        this.lidar = LiDarWorkerTracker;
        time = 0;
        this.tick=tick;
        this.duration = duration;
        lastObj = new ArrayList<>();
        lst = new ArrayList<>();
        this.counter=counter;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        System.out.println("lidar" + lidar.getId() + " is initialized with frequency = " + lidar.getFrequency());

        subscribeBroadcast(CameraDoneBroadcast.class, callback -> {
            counter--;
        });

        subscribeEvent(DetectObjectEvent.class , event -> {
            lidar.clearList();

            //insures no other service is crashed
            if (!GlobalCrashed.getInstance().getCrahs()) {

            List<TrackedObject> l = new ArrayList<>();


                //getting the DetectedObjects needed to handle
                StampedDetectedObjects stampedObjects = event.getObj();
                List<DetectedObject> list = stampedObjects.getDetectedObjects();
                int time = stampedObjects.getTime();



                for (DetectedObject o : list) {

                    //check is lidar crashed at this time
                    StampedCloudPoints point_crash = LiDarDataBase.getObject("ERROR", event.getObj().getTime());
                    if (point_crash != null) {
                        System.out.println("LIDAR_ERROR");
                        convertJavaCrash.getInstance().setError("Lidar disconnected");
                        convertJavaCrash.getInstance().setFaultySensor("lidar" + lidar.getId());
                        convertJavaCrash.getInstance().setLastLiDarWorkerTrackersFrame(lastObj);
                        GlobalCrashed.getInstance().setCrash();
                        sendBroadcast(new CrashedBroadcast());
                        terminate();
                        return;
                    }


                    //bring the object cloudPoints from the lidarDataBase according to his id
                    StampedCloudPoints points = LiDarDataBase.getObject(o.getId(), event.getObj().getTime());

                    //create tracked object for the detected object
                    TrackedObject trackedObject = new TrackedObject(o.getId(), points.getTime(), o.getDescription());

                    List<CloudPoint> cloudPoints = points.getCloudPoints(); //points of the object
                    for (CloudPoint p : cloudPoints) {
                        trackedObject.addCoordinate(p);
                    }

                    l.add(trackedObject);
                    lidar.getList().add(trackedObject);
                    FusionSlam.getInstance().addTrackedObj(trackedObject);
                }

                TrackedObjectsEvent tob = new TrackedObjectsEvent(l , time);
                lst.add(tob);


                    //}
/// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                //cleaning the list to hold only last tracked objects in the correct time
//                lastObj.clear();
//
//                for(DetectedObject o:list) {
//                    //bring the object cloudPoints from the lidarDataBase according to his id
//                    StampedCloudPoints points = LiDarDataBase.getObject(o.getId() , event.getObj().getTime());
//
//                    //create tracked object for the detected object
//                    TrackedObject trackedObject = new TrackedObject(o.getId(), points.getTime() , o.getDescription());
//                    List<CloudPoint> cloudPoints = points.getCloudPoints(); //points of the object
//                    for (CloudPoint p : cloudPoints) {
//                        trackedObject.addCoordinate(p);
//                        //trackedObject_CamFreq.addCoordinate(p);
//                    }
//                    lastObj.add(trackedObject);
//                    lidar.add(trackedObject);
//
//                    //for each lidar that detected an object , fusion slam should know that object was detected
//                   //because we need the pose when the camera detected the event not including her frequency
//                    FusionSlam.getInstance().addTrackedObj(trackedObject);
//                }
//                complete(event,true);
/// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //bring the object cloudPoints from the lidarDataBase according to his id
                  // StampedCloudPoints points = LiDarDataBase.getObject(o.getId() , event.getObj().getTime());


                    //cleaning the list to hold only last tracked objects in the correct time (-1 for not delete objects on the same time)
//                    if (lastObj.size() > 0 && lastObj.get(0).getTime() < GlobalTime.getInstance().getGlobalTime() - 1){
//                        lastObj.clear();
//                        lidar.clearList();
//                    }

                    //create tracked object for the detected object
                    //TrackedObject trackedObject = new TrackedObject(o.getId(), points.getTime() , o.getDescription());
                    //TrackedObject trackedObject_CamFreq = new TrackedObject(o.getId(), points.getTime() + event.getCameraFreq() , o.getDescription());

//                    List<CloudPoint> cloudPoints = points.getCloudPoints(); //points of the object
//                    for (CloudPoint p : cloudPoints) {
//                        trackedObject.addCoordinate(p);
                        //trackedObject_CamFreq.addCoordinate(p);
                    //}

                    //because we need to include cam freq when sending a lidar TrackedObjectEvent
                    //lidar.add(trackedObject);
                    //lastObj.add(trackedObject);

                    //for each lidar that detected an object , fusion slam should know that object was detected
                    //because we need the pose when the camera detected the event not including her frequency
                    //FusionSlam.getInstance().addTrackedObj(trackedObject);
                //}

                complete(event,true);

        }
            else {
                convertJavaCrash.getInstance().setLastLiDarWorkerTrackersFrame(lastObj);
        terminate();}});



        subscribeBroadcast(TickBroadcast.class, callback -> {

            //insures no other service is crashed
            if (!GlobalCrashed.getInstance().getCrahs()) {

                Iterator<TrackedObjectsEvent> iterator = lst.iterator(); // Use an iterator for safe removal
                while (iterator.hasNext()) {
                    TrackedObjectsEvent t = iterator.next(); // Get the next element
                    List<TrackedObject> lstOfTrackedObjects = t.getTrackedObjects();
                    int sizeOfList = lstOfTrackedObjects.size();

                    if (sizeOfList > 0 && lstOfTrackedObjects.get(0).getTime() + lidar.getFrequency() <= GlobalTime.getInstance().getGlobalTime()) {
                        sendEvent(t); // Perform your action
                        StatisticalFolder.getInstance().incrementNumberOfTrackedObjects(sizeOfList);
                        System.out.println("TrackedObjectEvent were sent with objects: ");
                        t.print();
                        iterator.remove(); // Safely remove the current element from the list
                    }
                    lastObj=t.getTrackedObjects();
                }
                if(counter==0){
                    terminate();
                    sendBroadcast(new DoneBroadcast());
                }
            }

//                for (TrackedObjectsEvent t : lst){
//
//                    List<TrackedObject> lstOfTrackedObjects = t.getTrackedObjects();
//                    int sizeOfList = lstOfTrackedObjects.size();
//
//                    if (sizeOfList > 0 && lstOfTrackedObjects.get(0).getTime() + lidar.getFrequency() <= GlobalTime.getInstance().getGlobalTime()){
//                        sendEvent(t);
//                        delete = t;
//                        System.out.println("TrackedObjectEvent were sent with objects: ");
//                        t.print();
//                    }
//                }

                //search for any DetectedObject with timeDetect + lidarFrequency == currentTime to send TrackedObjectEvent
//                List<TrackedObject> trackedObjectList = lidar.getList();
//                List<TrackedObject> sendObjects = new ArrayList<>();
//
//                //should we remove the object for efficiency ?
//                //System.out.println("TrackedObjLidarList of Size = " + trackedObjectList.size());
//                for (TrackedObject o : trackedObjectList) {
//
//
//                     if (o.getTime() + lidar.getFrequency() <= GlobalTime.getInstance().getGlobalTime()) {
//                        //System.out.println(o.getId()+ " wasTacked in time: " + GlobalTime.getInstance().getGlobalTime());
//                        StatisticalFolder.getInstance().incrementNumberOfTrackedObjects();
//                        //lidar.getList().remove(o);
//
//
//                         sendObjects.add(o);
//                     }
//                }
//                System.out.println(this.getName() + " sends TrackedObjectEvent at time: " + GlobalTime.getInstance().getGlobalTime() + " with these objects: " + "(sendObjects.Size() = " + sendObjects.size() +")");
//
//
//                //lastObject = sendObjects;
//                convertJavaCrash.getInstance().setLastLiDarWorkerTrackersFrame(lastObj);
//
//
//
//                sendEvent(new TrackedObjectsEvent(sendObjects, GlobalTime.getInstance().getGlobalTime() - lidar.getFrequency()));

            else {
                convertJavaCrash.getInstance().setLastLiDarWorkerTrackersFrame(lastObj);
            terminate();}
        });



        subscribeBroadcast(TerminatedBroadcast.class, callback ->{
            terminate();
                }
        );



        subscribeBroadcast(CrashedBroadcast.class, callback ->{
            convertJavaCrash.getInstance().setLastLiDarWorkerTrackersFrame(lastObj);
            terminate();}

        );
    }


}
