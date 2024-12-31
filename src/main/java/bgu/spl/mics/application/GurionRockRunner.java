package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.jsonToJava.Configuration;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

        parseConfigData();
        LiDarDataBase.getInstance("example input/pose_data.json");
        System.out.println(LiDarDataBase.getInstance() == null);

        // TODO: Parse configuration file.
        // TODO: Initialize system components and services.
        // TODO: Start the simulation.


///////////////////////////////////print_tests/////////////////////////////////////
//        LiDarDataBase l = new LiDarDataBase();
//        LiDarDataBase.getInstance(tst);

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
/////////////////////////////////////////////////////////////////////////////////////




//        String dataPathLidar=configuration.getLidarWorkers().getLidars_data_path();
//        String dataPathCamera=configuration.getCameras().getCamera_datas_path();

//        // TODO: Initialize system components and services.
//        List<Camera> cameras=configuration.getCameras().getCamerasConfigurations();
//        for(Camera c: cameras){
//            c.add(camerajasongetSDObj(c.get_id()));
//        }
//
//        List<LiDarWorkerTracker> lidars=configuration.getLidarWorkers().getLidarConfigurations();
//        for(LiDarWorkerTracker lidar: lidars){
//            System.out.println(lidar.getStatus());
//        }
//
//        List<TrackedObject> trackedObjects=null;
//        try (FileReader reader = new FileReader("example_input_2/lidar_data.json")) {
//            // Define the type for the list of employees
//            Type type = new TypeToken<List<TrackedObject>>()
//            {}.getType();
//            // Deserialize JSON to list of employees
//            trackedObjects = gson.fromJson(reader,
//                    type);
//            // Use the employee data
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        List<TrackedObject> trackedObjectsList= trackedObjects;
//        for(TrackedObject o: trackedObjectsList){
//            System.out.println(o.getTime());
//        }
        // TODO: Start the simulation.



    }
public static void parseConfigData(){
    Gson gson = new Gson();
    Configuration configuration=null;
    try (FileReader reader = new FileReader("example_input_2/configuration_file.json")) {
        // Define the type for the list of employees
        Type configFileType = new TypeToken<Configuration>()
        {}.getType();
        // Deserialize JSON to list of employees
        configuration = gson.fromJson(reader,
                configFileType);

        //print tests
        configuration.getCameras().toStringCamera();
        configuration.toString_lidar();
        System.out.println("configTickTimeStart: " + configuration.getTickTime());

        //creat threads for each object in the simulation correspondingly
        List<Camera> cameras = configuration.getCameras().getCamerasConfigurations();
        for (Camera c : cameras){
            MicroService camService = new CameraService(c , configuration.getTickTime());
            //camService.start(); should start the thread , give him os time
        }

        List<LiDarWorkerTracker> lidars = configuration.getLidarWorkers().getLidarConfigurations();
        for (LiDarWorkerTracker l : lidars){
            MicroService lidarService = new LiDarService(l , configuration.getTickTime());
            //lidarService.start(); should start the thread , give him os time
        }

        //should be one pose service that send the pose of the robot at each tick
        GPSIMU GI = new GPSIMU(configuration.getTickTime());
        MicroService poseService = new PoseService(GI , configuration.getTickTime());
        //poseService.start(); should start the thread , give him os time


        //start a TimeService that take startTime and duration
        MicroService timeService = new TimeService(configuration.getTickTime(), configuration.getDuration());
        //timeService.start(); should start the thread , give him os time

        //fusionSlam
        MicroService fusionService = new FusionSlamService(FusionSlam.getInstance() , configuration.getTickTime());

    }
    catch (IOException e) {
        e.printStackTrace();

    }
}






}
