package bgu.spl.mics.application.objects;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private static StatisticalFolder instance;
    public static StatisticalFolder getInstance() {
        if (instance == null) {
            instance = new StatisticalFolder();
        }
        return instance;
    }
    private int systemRuntime;
    private int numberOfDetectedObjects;
    private int numberOfTrackedObjects;
    private int numberOfLandmarks;

    public StatisticalFolder(){
        systemRuntime = 0;
        numberOfDetectedObjects = 0;
        numberOfTrackedObjects = 0;
        numberOfLandmarks = 0;
    }

    public int getSystemRuntime() {return systemRuntime;}
    public int getNumberOfDetectedObjects() {return numberOfDetectedObjects;}

    public int getNumberOfTrackedObjects() {
        return numberOfTrackedObjects;
    }
    public int getNumberOfLandmarks() {return numberOfLandmarks;}

    //incrementMethods
    public void incrementSystemRuntime() {systemRuntime++;}
    public void incrementNumberOfDetectedObjects(int i) {numberOfDetectedObjects+=i;}
    public void incrementNumberOfLandmarks(int i) {numberOfLandmarks+=i;}
}
