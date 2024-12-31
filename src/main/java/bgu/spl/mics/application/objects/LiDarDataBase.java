package bgu.spl.mics.application.objects;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {


    private static LiDarDataBase instance;
    private static List<StampedCloudPoints> list;

    //singleton DesignPattern
//    public static LiDarDataBase getInstance() {
//        if (instance == null) {
//            instance = new LiDarDataBase();
//        }
//        return instance;
//    }

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) {
        if (instance == null) {
            instance = new LiDarDataBase();
            list = Collections.synchronizedList(new ArrayList<>());
            try (FileReader reader = new FileReader(filePath)){

                Gson gson = new Gson();
                Type lst = new TypeToken<List<LidarJson>>() {}.getType();

                // Parse the JSON string into a list of LidarJsonEntry objects
                System.out.println("im before list<Lidar>");
                ArrayList<LidarJson> lidarJsonEntries = gson.fromJson(reader, lst);
                System.out.println("im after list<Lidar>");


                for (LidarJson entry : lidarJsonEntries) {
                    StampedCloudPoints stampedPoint = new StampedCloudPoints(entry.getTime(), entry.getId());
                    System.out.println("Time: " + entry.getTime());
                    System.out.println("ID: " + entry.getId());
                    System.out.println("Cloud Points: ");
                    for (List<Double> point : entry.getCloudPoints()) {
                        System.out.println("  Point: " + point);
                        System.out.println("x = " + point.get(0));
                        System.out.println("y = " + point.get(1));
                        CloudPoint cloudPoint = new CloudPoint(point.get(0), point.get(1));

                        System.out.println("the cloundPoint is: " + cloudPoint);
                        stampedPoint.addCloudPoint(cloudPoint);
                        System.out.println("______________________");
                    }
                        list.add(stampedPoint);

                }
            } catch (Exception e) {
                System.out.println("im in extension");

            }
        }
        return instance;
    }

    public static StampedCloudPoints getObject(String id) {
        for (StampedCloudPoints obj : list) {
            if (obj.getId().equals(id)) {
                return obj;
            }
        }
        return null;
    }

    public List<StampedCloudPoints> getStampedPoints(){return list;}
}








