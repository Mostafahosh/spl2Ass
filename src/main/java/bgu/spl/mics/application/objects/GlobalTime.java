package bgu.spl.mics.application.objects;

import bgu.spl.mics.MessageBusImpl;

public class GlobalTime {
    private static GlobalTime instance;
    private int globalTime = 0;
    public static GlobalTime getInstance(){
        if(instance==null){
            instance=new GlobalTime();
        }
        return instance;
    }

    public void increaseGlobaltime(int add) {
        globalTime += add;
    }
    public int getGlobalTime(){return globalTime;}
}
