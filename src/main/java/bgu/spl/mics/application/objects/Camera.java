package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.Messages.Events.DetectObjectEvent;

import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.Semaphore;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    int id;
    int frequency;
    STATUS status;
    List<StampedDetectedObjects> list;


    public Camera(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        status = STATUS.UP;
        list = Collections.synchronizedList(new ArrayList<>());
    }

    public int get_id() {
        return id;
    }

    public int get_frequency() {
        return frequency;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public void clearList() {
        list.clear();
    }

    public void add(StampedDetectedObjects obj) {
        list.add(obj);
    }

    public StampedDetectedObjects get(int time) {
        for (StampedDetectedObjects  obj : list){
            if (obj.getTime() == time){return obj;}
        }
        return null;
    }

    public List<StampedDetectedObjects> getList(){return list;}

    public int numOfObjects() {
        return list.size();
    }

    public String toString() {
        return "Camera id: " + id;
    }


    public void ParseCameraData() {
        //mine
//        Gson gson = new Gson();
//
//        String filePath = "example input/camera_data.json"; // Replace with the actual file path
//        try (FileReader reader = new FileReader(filePath)){
//
//            Type lst = new TypeToken<CameraJson>(){}.getType();
//            System.out.println("lst= " + lst.toString());
//
//
//            CameraJson cameraJsonList = gson.fromJson(reader, CameraJson.class);
//            System.out.println(cameraJsonList);
//            List<StampedDetectedObjects> stampedlst = cameraJsonList.getCamera1();
//            System.out.println(cameraJsonList.getCamera1().get(0));
//            //System.out.println(stampedlst);
////            for (StampedDetectedObjects obj : stampedlst) {
////                System.out.println("stampedlst= " + obj.getTime());
////            }
//
//
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }


//correct for example input
//    Gson gson = new Gson();
//    String filePath = "example input/camera_data.json";
//
//    try (FileReader reader = new FileReader(filePath)) {
//        // Define the type for Map<String, List<StampedDetectedObjects>>
//        Type mapType = new TypeToken<Map<String, List<StampedDetectedObjects>>>() {}.getType();
//        Map<String, List<StampedDetectedObjects>> cameras = gson.fromJson(reader, mapType);
//
//        // Iterate through the map and print the details
//        for (Map.Entry<String, List<StampedDetectedObjects>> entry : cameras.entrySet()) {
//            System.out.println("Camera: " + entry.getKey());
//            for (StampedDetectedObjects obj : entry.getValue()) {
//                System.out.println(obj);
//            }
//        }
//    }


        //correct for example_input_2
//    Gson gson = new Gson();
//    String filePath = "example input/camera_data.json";
//
//    try (FileReader reader = new FileReader(filePath)) {
//        // Define the type for Map<String, List<List<StampedDetectedObjects>>>
//        Type mapType = new TypeToken<Map<String, List<List<StampedDetectedObjects>>>>() {}.getType();
//
//        // Deserialize the JSON into the nested map
//        Map<String, List<List<StampedDetectedObjects>>> nestedCameras = gson.fromJson(reader, mapType);
//
//        // Create a flattened map
//        Map<String, List<StampedDetectedObjects>> flattenedCameras = new HashMap<>();
//
//        // Flatten the nested lists
//        for (Map.Entry<String, List<List<StampedDetectedObjects>>> entry : nestedCameras.entrySet()) {
//            List<StampedDetectedObjects> flattenedList = new ArrayList<>();
//            for (List<StampedDetectedObjects> innerList : entry.getValue()) {
//                flattenedList.addAll(innerList);
//            }
//            flattenedCameras.put(entry.getKey(), flattenedList);
//        }
//
//        // Print the flattened result
//        for (Map.Entry<String, List<StampedDetectedObjects>> entry : flattenedCameras.entrySet()) {
//            System.out.println("Camera: " + entry.getKey());
//            for (StampedDetectedObjects obj : entry.getValue()) {
//                System.out.println(obj);
//            }
//        }
//
//    }


//    String filePath = "path/to/your/json/file.json";
//
//    // Create a custom Gson instance with the deserializer
//    Gson gson = new GsonBuilder()
//            .registerTypeAdapter(new TypeToken<Map<String, List<StampedDetectedObjects>>>() {}.getType(),
//                    new CameraDeserializer())
//            .create();
//
//    try (FileReader reader = new FileReader(filePath)) {
//        // Define the target type
//        Type mapType = new TypeToken<Map<String, List<StampedDetectedObjects>>>() {}.getType();
//
//        // Deserialize the JSON
//        Map<String, List<StampedDetectedObjects>> cameras = gson.fromJson(reader, mapType);
//
//        // Print the details
//        for (Map.Entry<String, List<StampedDetectedObjects>> entry : cameras.entrySet()) {
//            System.out.println("Camera: " + entry.getKey());
//            for (StampedDetectedObjects obj : entry.getValue()) {
//                System.out.println(obj);
//            }
//        }
//    }
//
//
//    catch (IOException e) {
//        e.printStackTrace();
//    }
//}


//static class CameraDeserializer implements JsonDeserializer<Map<String, List<StampedDetectedObjects>>> {
//    @Override
//    public Map<String, List<StampedDetectedObjects>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//        // Create a Map to hold the results
//        Map<String, List<StampedDetectedObjects>> result = new Gson().fromJson(json, new TypeToken<Map<String, JsonElement>>() {}.getType());
//
//        // Process each camera entry
//        for (Map.Entry<String, JsonElement> entry : result.entrySet()) {
//            JsonElement value = entry.getValue();
//
//            List<StampedDetectedObjects> flattenedList = new ArrayList<>();
//            if (value.isJsonArray()) {
//                JsonArray array = value.getAsJsonArray();
//
//                // Check if the array contains objects or nested lists
//                for (JsonElement element : array) {
//                    if (element.isJsonObject()) {
//                        // Directly add the StampedDetectedObjects
//                        StampedDetectedObjects obj = context.deserialize(element, StampedDetectedObjects.class);
//                        flattenedList.add(obj);
//                    } else if (element.isJsonArray()) {
//                        // Flatten the nested list
//                        for (JsonElement innerElement : element.getAsJsonArray()) {
//                            StampedDetectedObjects obj = context.deserialize(innerElement, StampedDetectedObjects.class);
//                            flattenedList.add(obj);
//                        }
//                    }
//                }
//            }
//            // Replace the raw JsonElement with the flattened list
//            result.put(entry.getKey(), flattenedList);
//        }
//
//        return result;
//    }
//}


//    public List<DetectObjectEvent> detectObjects(int time) {
//        List<DetectObjectEvent> events = new ArrayList<>();
//        int timeAndFreq = time + frequency;
//        for (int i = 0; i < list.size(); i++) {
//            if (timeAndFreq == list.get(i).getTime()) {
//                for (DetectedObject obj : list.get(i).getDetectedObjects()) {
//                    DetectObjectEvent objEvent = new DetectObjectEvent(obj, time);
//                    events.add(objEvent);
//                }
//            }
//        }
//        return events;
//    }


        Gson gson = new Gson();
        String filePath = "example_input_with_error/camera_data.json";

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

            // Print the details
            for (Map.Entry<String, List<StampedDetectedObjects>> entry : cameras.entrySet()) {
                System.out.println("Camera: " + entry.getKey());
                for (StampedDetectedObjects obj : entry.getValue()) {
                    System.out.println(obj);
                }
            }
        }
            catch (IOException e) {
        e.printStackTrace();
    }
    }
}