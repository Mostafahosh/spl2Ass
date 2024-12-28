package bgu.spl.mics.application.Messages.Events;
import bgu.spl.mics.Event;

import bgu.spl.mics.application.objects.Pose;

public class PoseEvent implements Event<Pose> {
    Pose pose;

    public PoseEvent(Pose pose) {
        this.pose = pose;
    }
    public Pose getPose() {return pose;}

}
