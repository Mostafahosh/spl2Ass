package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Messages.Broadcasts.TerminatedBroadcast;
import bgu.spl.mics.application.Messages.Broadcasts.TickBroadcast;
import bgu.spl.mics.application.objects.GlobalTime;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
    /////fields/////
    private  int tickTime;
    private  int duration;
    private CountDownLatch latch;
    ////////////////


    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration , CountDownLatch latch ) {
        super("tick " + TickTime);
        this.tickTime = TickTime;
        this.duration = Duration;
        this.latch = latch;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        System.out.println("im ready to send ticks");
        GlobalTime globalTime = GlobalTime.getInstance();
        //int clock = 0 ; //*Milleseconds ?
        while(globalTime.getGlobalTime() <= duration){
            System.out.println("i've send BroadCastTick at time: " + GlobalTime.getInstance().getGlobalTime());
            TickBroadcast t = new TickBroadcast();
            globalTime.increaseGlobaltime(tickTime);
            sendBroadcast(t);
            try {
                System.out.println("TimeService waiting for all microservices to finish...");
                latch.await(); // Wait for other microservices to signal completion
            } catch (InterruptedException e) {
                System.err.println("TimeService was interrupted while waiting for latch");
                Thread.currentThread().interrupt();
            }
        }



        System.out.println("All ticks completed. Sending TerminatedBroadcast.");
        sendBroadcast(new TerminatedBroadcast());
        terminate();


//        for (int currentTick = 1; currentTick <= duration; currentTick++) {
//            System.out.println("Sending TickBroadcast at time: " + currentTick);
//
//            TickBroadcast tickBroadcast = new TickBroadcast();
//            sendBroadcast(tickBroadcast);
//
//            try {
//                // Sleep for the duration of one tick (converted to milliseconds)
//                Thread.sleep(tickTime * 1000);
//            } catch (InterruptedException e) {
//                System.err.println("TimeService was interrupted");
//                Thread.currentThread().interrupt();
//                break;
//            }
//        }

//        int clock = tickTime;
//        while(clock<=this.duration*tickTime){
//            sendBroadcast(new TickBroadcast());
//            clock=clock+tickTime;
//        }
//        terminate();
//        sendBroadcast(new TerminatedBroadcast());
    }
}
