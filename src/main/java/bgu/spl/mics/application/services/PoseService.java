package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.JavaToJson.convertJavaCrash;
import bgu.spl.mics.application.Messages.Broadcasts.CrashedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TerminatedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Messages.Events.PoseEvent;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.GlobalCrashed;
import bgu.spl.mics.application.objects.GlobalTime;
import bgu.spl.mics.application.objects.Pose;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
/////fields/////
   private GPSIMU gpsimu;
   private int tick;
   private CountDownLatch latch;
   private CyclicBarrier barrier;
   private int duration;
   int counter = 0;
   List<Pose> poses;
////////////////


    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu , int tick , CountDownLatch latch ,CyclicBarrier barrier , int duration) {
        super("GPSIMU");
        this.gpsimu = gpsimu;
        this.tick = tick; //simulation not necessary starts from tick 1
        this.latch = latch;
        this.barrier = barrier;
        this.duration = duration;
        this.poses = new ArrayList<>();

    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        System.out.println("GPSIMU is initialized");
        System.out.println("the Size of List<Pose> = " + gpsimu.getList().size());
        //gpsimu.print();

        subscribeBroadcast(TickBroadcast.class, callback ->{

            //insures no other service is crashed
            if (!GlobalCrashed.getInstance().getCrahs()){

                if (GlobalTime.getInstance().getGlobalTime() <= gpsimu.getLastTime()){
            Pose pose;
            pose = gpsimu.findPose(GlobalTime.getInstance().getGlobalTime());
            poses.add(pose);
            PoseEvent event = new PoseEvent(pose);
            counter +=1;

            sendEvent(event);
            //System.out.println("the Robot Position at time: " + GlobalTime.getInstance().getGlobalTime() + " is: " + pose.toString());

            //tick++; //should here raise the tick / or in a general while loop in each iteration raise the tick ?
           //latch.countDown();
//            try {
//                System.out.println(this.getName() + " is waiting at the barrier...");
//                barrier.await();
//                System.out.println(this.getName() + " has passed the barrier!");
//            }
//            catch (Exception e) {
//                System.err.println(this.getName() + " encountered an error with the barrier: " + e.getMessage());
//            }

        }

        try {
            // If it's the last tick, decrement the latch
            //System.out.println("Time in pose = " + GlobalTime.getInstance().getGlobalTime());
            if (GlobalTime.getInstance().getGlobalTime() == duration) {
                //System.out.println(this.getName() + " is completing the last tick...");
                latch.countDown();
            }
        }catch(Exception e){
            System.err.println("TimeService encountered an issue: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        }
            else {
                convertJavaCrash.getInstance().setPoses(poses);
        terminate();}
        });

        //should subscribe to crashBroadCast ?

        subscribeBroadcast(TerminatedBroadcast.class, callback ->{
            terminate();
        });


        subscribeBroadcast(CrashedBroadcast.class, callback ->{
            convertJavaCrash.getInstance().setPoses(poses);
            terminate();}

        );

        }
}
