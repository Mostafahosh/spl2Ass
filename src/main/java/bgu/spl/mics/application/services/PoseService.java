package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.Broadcasts.TerminatedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Messages.Events.PoseEvent;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.GlobalTime;
import bgu.spl.mics.application.objects.Pose;

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
////////////////


    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu , int tick , CountDownLatch latch) {
        super("poseAtTime: " + gpsimu.getCurrentTick());
        this.gpsimu = gpsimu;
        this.tick = tick; //simulation not necessary starts from tick 1
        this.latch = latch;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, callback ->{

            Pose pose;
            pose = gpsimu.findPose(GlobalTime.getInstance().getGlobalTime());
            PoseEvent event = new PoseEvent(pose);
            sendEvent(event);
            //tick++; //should here raise the tick / or in a general while loop in each iteration raise the tick ?
           latch.countDown();
        });

        //should subscribe to crashBroadCast ?

        subscribeBroadcast(TerminatedBroadcast.class, callback ->{
            System.out.println("it's time for " + this.getName() + " Service to subscribe to TerminateBroadCast");
            terminate();
        });


    }
}
