package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.JavaToJson.convertJava;
import bgu.spl.mics.application.jsonToJava.Configuration;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        // TODO: Parse configuration file.
        // TODO: Initialize system components and services.
        // TODO: Start the simulation.

        System.out.println("---------------------------------lidarDataBase--------------------------------");
        LiDarDataBase l =  LiDarDataBase.getInstance("example input/lidar_data.json");
        l.getInstance("example input/lidar_data.json");


        System.out.println("-----------------------------Cameras------------------------------------");
        Map<String, List<StampedDetectedObjects>> mapCamera = parseCameraData("example input/camera_data.json");
        List<StampedDetectedObjects> c1 = mapCamera.get("camera1");
        for (StampedDetectedObjects obj : c1 ){obj.printList();}


        System.out.println("-----------------------------Poses------------------------------------");
        GPSIMU gi = new GPSIMU(0);
        parsePoseData("example input/pose_data.json" , gi);


        System.out.println("-------------------------------configFile----------------------------------");
        List<Thread> threads = parseConfigData("example input/configuration_file.json" , mapCamera);
        for (Thread thread : threads){
            thread.start();
        }

///////////////////////////////////print_tests/////////////////////////////////////
//        LiDarDataBase l = LiDarDataBase.getInstance();
//        l.getInstance("example input/lidar_data.json");
//        System.out.println(l.getStampedPoints().size());

//        FusionSlam saher = new FusionSlam();
//        Pose pose = new Pose(5.5 , 7.2 ,148.91 ,0);
//        saher.mathCalc(-2.5,-3.3,pose);
//
//
//
//    Camera cameratst = new Camera(3,32);
//    cameratst.ParseCameraData();

//        List<Pose> lst = new ArrayList<>();
//        GPSIMU GI = new GPSIMU(6);
//        GI.parseJson();


        //test fot java to json//
//        List<CloudPoint> points1 = new ArrayList<>();
//        CloudPoint p1 = new CloudPoint(3.4 , 5.9);
//        CloudPoint p2 = new CloudPoint(1.9 , 9.6);
//        points1.add(p1);
//        points1.add(p2);
//
//
//        List<CloudPoint> points2 = new ArrayList<>();
//        CloudPoint p3 = new CloudPoint(7.1 , 5.3);
//        CloudPoint p4 = new CloudPoint(15.4 , 8.0);
//        points2.add(p3);
//        points2.add(p4);
//
//        convertJava cj = new convertJava();
//
//        LandMark m1 = new LandMark("table" , "red_table" , points1);
//        LandMark m2 = new LandMark("door" , "brown_door" , points2);
//
//        Map<String,LandMark> map=new HashMap<>();
//        map.put("table" , m1);
//        map.put(m2.getId() , m2);
//
//        cj.setLandMarks(map);
//
//        StatisticalFolder.getInstance();
//        StatisticalFolder.getInstance().setSystemRuntime(30);
//        StatisticalFolder.getInstance().setNumberOfDetectedObjects(7);
//        StatisticalFolder.getInstance().setNumberOfLandmarks(9);
//        StatisticalFolder.getInstance().setNumberOfTrackedObjects(7);
//
//        cj.setSystemRuntime(StatisticalFolder.getInstance().getSystemRuntime());
//        cj.setNumDetectedObjects(StatisticalFolder.getInstance().getNumberOfDetectedObjects());
//        cj.setNumTrackedObjects(StatisticalFolder.getInstance().getNumberOfTrackedObjects());
//        cj.setLandMarks(StatisticalFolder.getInstance().getNumberOfLandmarks());
//
//        convertToJson(cj);
    }


public static List<Thread> parseConfigData(String filepath , Map<String, List<StampedDetectedObjects>> map){
    List<Thread> threads = new LinkedList<>();

    Gson gson = new Gson();
    Configuration configuration=null;
    try (FileReader reader = new FileReader(filepath)) {
        // Define the type for the list of employees
        Type configFileType = new TypeToken<Configuration>() {}.getType();

        // Deserialize JSON to list of employees
        configuration = gson.fromJson(reader, configFileType);

        //latch initialize
        int numOfBarriers = configuration.getCameras().getCamerasConfigurations().size() + configuration.getLidarWorkers().getLidarConfigurations().size() + 2;
        CountDownLatch latch = new CountDownLatch(numOfBarriers);
        System.out.println("number of Barriers is: " + numOfBarriers);

        /////////print tests////////
//        System.out.println("print test cameras");
//        configuration.getCameras().toStringCamera();
//        configuration.toString_lidar();
//        System.out.println("configTickTimeStart: " + configuration.getTickTime());

        //creat threads for each object in the simulation correspondingly
        List<Camera> cameras = configuration.getCameras().getCamerasConfigurations();
        for (Camera c : cameras){
            System.out.println("camera key by configFile: " +c.getCamera_key());
            List<StampedDetectedObjects> lst = map.get(c.getCamera_key());
            c.setList(lst);
            //printListS(lst);


            //System.out.println("the size of the list is: " + c.numOfObjects());
            CameraService camService = new CameraService(c , configuration.getTickTime() , latch);
            //printListS(camService.getCamera().getList());

            threads.add(new Thread(camService));
        }



        List<LiDarWorkerTracker> lidars = configuration.getLidarWorkers().getLidarConfigurations();
        for (LiDarWorkerTracker l : lidars){
            LiDarWorkerTracker lidar = new LiDarWorkerTracker(l.getId() , l.getFrequency());

            MicroService lidarService = new LiDarService(lidar , configuration.getTickTime() , latch);
            //lidarService.start(); should start the thread , give him os time
            threads.add(new Thread(lidarService));
        }

        //should be one pose service that send the pose of the robot at each tick
        GPSIMU GI = new GPSIMU(configuration.getTickTime());
        MicroService poseService = new PoseService(GI , configuration.getTickTime() , latch);
        //poseService.start(); should start the thread , give him os time
        threads.add(new Thread(poseService));



        //start a TimeService that take startTime and duration
        MicroService timeService = new TimeService(configuration.getTickTime(), configuration.getDuration() , latch);
        System.out.println("conf tick: " + configuration.getTickTime() + " - " + configuration.getDuration());

        //timeService.start(); should start the thread , give him os time
        Thread t = new Thread(timeService);
        threads.add(t);
        //t.start();


        //fusionSlam
        MicroService fusionService = new FusionSlamService(FusionSlam.getInstance() , configuration.getTickTime() , latch);
        threads.add(new Thread(fusionService));

//        for (Thread thread : threads){
//            System.out.println("thread: " + thread.getName() + " started");
//            thread.start()
//             ;}
    }
    catch (IOException e) {
        e.printStackTrace();

    }
    return threads;
}


//    public static void crashedCameraFunc(C){
//        convertJava CJ = resultFunction();
//        List<Pose> poses = FusionSlam.getInstance().getPoses();
//
//    }





    public static void convertToJson(convertJava input) {
     convertJava convertjava = input;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    try(FileWriter writer = new FileWriter("output_file.json")){

        gson.toJson(convertjava , writer);
        System.out.println("\"Employees have been written to new_employees.json");
    }
    catch (IOException e) {
        e.printStackTrace();
        }
    }

    public static convertJava resultFunction(){
        convertJava res = new convertJava();
        Map<String , LandMark> map = new HashMap<>();

        List<LandMark> lst = FusionSlam.getInstance().getLandMarks();
        for (LandMark lm : lst){
            map.put(lm.getId() , lm);
        }
        res.setLandMarks(map);

        res.setNumDetectedObjects(StatisticalFolder.getInstance().getNumberOfDetectedObjects());
        res.setLandMarks(StatisticalFolder.getInstance().getNumberOfLandmarks());
        res.setSystemRuntime(StatisticalFolder.getInstance().getSystemRuntime());
        res.setNumTrackedObjects(StatisticalFolder.getInstance().getNumberOfTrackedObjects());

        return res;
    }

    public static Map<String, List<StampedDetectedObjects>> parseCameraData(String filepath){
        Gson gson = new Gson();
        String filePath = filepath;

        try (FileReader reader = new FileReader(filePath)) {
            // Parse the raw JSON into a Map of JsonElement
            Type rawType = new TypeToken<Map<String, JsonElement>>() {}.getType();
            Map<String, JsonElement> rawCameras = gson.fromJson(reader, rawType);

            // Process and map each camera to a list of StampedDetectedObjects
            Map<String, List<StampedDetectedObjects>> cameras = new java.util.HashMap<>();
            for (Map.Entry<String, JsonElement> entry : rawCameras.entrySet()) {
                String camera = entry.getKey();
                JsonElement element = entry.getValue();

                List<StampedDetectedObjects> flattenedList = new ArrayList<>();
                if (element.isJsonArray()) {
                    JsonArray array = element.getAsJsonArray();

                    for (JsonElement innerElement : array) {
                        if (innerElement.isJsonObject()) {
                            // Handle a flat list of objects
                            StampedDetectedObjects obj = gson.fromJson(innerElement, StampedDetectedObjects.class);
                            flattenedList.add(obj);
                        } else if (innerElement.isJsonArray()) {
                            // Handle nested lists
                            for (JsonElement nestedElement : innerElement.getAsJsonArray()) {
                                StampedDetectedObjects obj = gson.fromJson(nestedElement, StampedDetectedObjects.class);
                                flattenedList.add(obj);
                            }
                        }
                    }
                }
                cameras.put(camera, flattenedList);
            }
            return cameras;

            // Print the details
//            for (Map.Entry<String, List<StampedDetectedObjects>> entry : cameras.entrySet()) {
//                System.out.println("Camera: " + entry.getKey());
//                for (StampedDetectedObjects obj : entry.getValue()) {
//                    System.out.println(obj);
//                }
//            }
//            int count = 1;
//            for (Map.Entry<String, List<StampedDetectedObjects>> entry : cameras.entrySet()) {
//                System.out.println("Camera: " + entry.getKey().getClass());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static void parsePoseData(String filepath , GPSIMU GI){
        Gson gson = new Gson();

        String filePath = filepath; // Replace with the actual file path
        try (FileReader reader = new FileReader(filePath)){
            Type lst = new TypeToken<ArrayList<Pose>>(){}.getType();
            ArrayList<Pose> poses = gson.fromJson(reader, lst);

            for (Pose pose : poses) {
                System.out.println(pose);
                GI.getList().add(pose);
            }

        }    catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void printListS(List<StampedDetectedObjects> lst){
        for (StampedDetectedObjects s : lst){
            s.printList();
        }
    }
}
