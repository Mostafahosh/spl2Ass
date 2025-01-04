package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.JavaToJson.convertJava;
import bgu.spl.mics.application.Messages.Broadcasts.TerminatedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.objects.GlobalCrashed;
import bgu.spl.mics.application.objects.GlobalTime;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import static bgu.spl.mics.application.GurionRockRunner.*;


/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
    /////fields/////
    private int tickTime;
    private int duration;
    private CountDownLatch latch;
    private CyclicBarrier barrier;
    ////////////////


    /**
     * Constructor for TimeService.
     *
     * @param TickTime The duration of each tick in milliseconds.
     * @param Duration The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration, CountDownLatch latch, CyclicBarrier barrier) {
        super("TimeService");
        this.tickTime = TickTime;
        this.duration = Duration;
        this.latch = latch;
        this.barrier = barrier;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        System.out.println("TimeService is initialized");
        GlobalTime globalTime = GlobalTime.getInstance();

        try {
            while (globalTime.getGlobalTime() < duration && !GlobalCrashed.getInstance().getCrahs()) { //if sensor crashed stop the run
                TickBroadcast t = new TickBroadcast();
                globalTime.increaseGlobaltime(tickTime);
                sendBroadcast(t);
                //System.out.println("BroadCastTick was sent at time: " + GlobalTime.getInstance().getGlobalTime());

                //System.out.println(this.getName() + " is waiting at the barrier...");
                Thread.sleep(tickTime * 1000); //Ticks are in seconds
                //barrier.await();
                //System.out.println(this.getName() + " has passed the barrier!");



            }
        } catch (Exception e) {
            System.err.println("TimeService was interrupted during sleep");
            Thread.currentThread().interrupt(); // Restore the interrupt status
        }



        //if a sensor caused a Crashed
        if (GlobalCrashed.getInstance().getCrahs()){
            System.out.println("sensor is crashed - TimeService stopped it's run!");
            crashedToJson();
            terminate();
            //sendBroadcast(new TerminatedBroadcast());
        }

        //no sensor crashed during the simulation
        else {
            convertJava c = resultFunction();
            convertToJson(c);

            ////////////////not necessary because terminate we be last in the Q for each Service (all TickBroadCast will be in front of it)//////////////////////////
//            try {
//                if (GlobalTime.getInstance().getGlobalTime() == duration) {
//                    System.out.println("TimeService waiting for all services to complete the last tick...");
//                    latch.await(); // Wait until all services call countDown
//                    System.out.println("TimeService: All services completed the last tick!");
//                }
//            } catch (InterruptedException e) {
//                System.err.println("TimeService encountered an issue: " + e.getMessage());
//                Thread.currentThread().interrupt();
//            }
//            /////////////////////////////////////////

            System.out.println("All ticks completed. Sending TerminatedBroadcast.");
            sendBroadcast(new TerminatedBroadcast());
            System.out.println("Terminated broadCast was Sent!!!!!!!!!!!!!!!!!!!!!!!!");
            terminate();
        }


    }
}
