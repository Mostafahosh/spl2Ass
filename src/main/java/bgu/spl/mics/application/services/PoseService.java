package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.Broadcasts.TerminatedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.Messages.Events.PoseEvent;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {

    GPSIMU gpsimu;
    int tick;
    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("poseAtTime: " + gpsimu.getCurrentTick());
        this.gpsimu = gpsimu;
        this.tick = 1;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, callback ->{
            Pose pose;
            pose = gpsimu.findPose(tick);
            PoseEvent event = new PoseEvent(pose);
            sendEvent(event);
            tick++;
        });




        subscribeBroadcast(TerminatedBroadcast.class, callback ->{
            terminate();
        });
    }
}
