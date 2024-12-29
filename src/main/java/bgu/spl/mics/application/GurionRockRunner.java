package bgu.spl.mics.application;

import bgu.spl.mics.application.jsonTojavFiles.configFile;
import bgu.spl.mics.application.objects.*;
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
        System.out.println("Hello World!");

        // TODO: Parse configuration file.
//        Gson gson = new Gson();
//        try (FileReader reader = new FileReader("example input/configuration_file.json")) {
//            // Define the type for the list of employees
//            Type type = new TypeToken<configFile>()
//            {}.getType();
//            // Deserialize JSON to list of employees
//            configFile file = gson.fromJson(reader, configFile.class);
//             //Use the employee data
//            for (Camera c: file.getCameras().getCamerasConfigurations()) {
//                System.out.println( c.get_frequency());
//            }
//            System.out.println(file.getTickTime());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        JsonParser parser = new JsonParser();
//        try (FileReader fileReader = new FileReader("example input/cof")) {

        // TODO: Initialize system components and services.
        // TODO: Start the simulation.

//        String tst = "[\n" +
//                "    {\"time\": 2, \"id\": \"Wall_1\", \"cloudPoints\": [[0.1176, 3.6969, 0.104], [0.11362, 3.6039, 0.104]]},\n" +
//                "    {\"time\": 4, \"id\": \"Wall_3\", \"cloudPoints\": [[3.0451, -0.38171, 0.104], [3.0637, -0.17392, 0.104]]},\n" +
//                "    {\"time\": 4, \"id\": \"Chair_Base_1\", \"cloudPoints\": [[1.9834, -1.0048, 0.104], [1.7235, -0.71784, 0.104]]},\n" +
//                "    {\"time\": 6, \"id\": \"Wall_4\", \"cloudPoints\": [[-2.5367, -3.3341, 0.104], [1.7926, -3.6804, 0.104]]},\n" +
//                "    {\"time\": 6, \"id\": \"Circular_Base_1\", \"cloudPoints\": [[0.73042, -1.1781, 0.104], [0.49003, -1.1433, 0.104]]},\n" +
//                "    {\"time\": 7, \"id\": \"Door\", \"cloudPoints\": [[0.5, -2.1, 0.104], [0.8, -2.3, 0.104]]},\n" +
//                "    {\"time\": 8, \"id\": \"Wall_5\", \"cloudPoints\": [[-3.6427, -1.071, 0.104], [-3.7119, -1.0673, 0.104]]},\n" +
//                "    {\"time\": 10, \"id\": \"Wall_1\", \"cloudPoints\": [[0.5, 3.9, 0.104], [0.2, 3.7, 0.104]]},\n" +
//                "    {\"time\": 12, \"id\": \"Wall_3\", \"cloudPoints\": [[3.1, -0.4, 0.104], [3.2, -0.2, 0.104]]},\n" +
//                "    {\"time\": 14, \"id\": \"Wall_5\", \"cloudPoints\": [[-3.6, -1.0, 0.104], [-3.7, -1.1, 0.104]]},\n" +
//                "    {\"time\": 16, \"id\": \"Wall_4\", \"cloudPoints\": [[-2.5, -3.3, 0.104], [1.8, -3.6, 0.104]]},\n" +
//                "    {\"time\": 18, \"id\": \"Chair_Base_1\", \"cloudPoints\": [[1.9, -1.0, 0.104], [1.7, -0.7, 0.104]]},\n" +
//                "    {\"time\": 20, \"id\": \"Circular_Base_1\", \"cloudPoints\": [[0.6, -0.9, 0.104], [0.3, -1.2, 0.104]]}\n" +
//                "]";
//
//
//        LiDarDataBase l = new LiDarDataBase();
//        LiDarDataBase.getInstance(tst);

        FusionSlam saher = new FusionSlam();
        Pose pose = new Pose(5.5 , 7.2 ,148.91 ,0);
        saher.mathCalc(-2.5,-3.3,pose);



    Camera cameratst = new Camera(3,32);
    cameratst.ParseCameraData();

//        List<Pose> lst = new ArrayList<>();
//        GPSIMU GI = new GPSIMU(6);
//        GI.parseJson();

    }





}
