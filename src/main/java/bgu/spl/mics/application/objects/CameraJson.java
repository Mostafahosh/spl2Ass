package bgu.spl.mics.application.objects;

import java.util.List;


public class CameraJson {
    //private List<StampedDetectedObjects> list;
//    private int time;


//    public int getTime() {
//        return time;
//    }
//    public void setTime(int time) {
//        this.time = time;
//    }

//    public DetectedObject getObj(int index) {return list.get(index);}
//    public List<DetectedObject> getList() {
//        return list;
//    }
//
//    public String toString(){
//        return "detected objects at time "  + " and objects " + stringlist();
//    }
////
//    public String stringlist(){
//        String str = "";
//        for (StampedDetectedObjects obj : list) {
//            str += obj.toString() + "\n";
//        }
//        return str;
//    }
  //public List<StampedDetectedObjects> getList() {return list;}
//
//    public void p(){
//        for (StampedDetectedObjects obj : list) {
//            System.out.println(obj.toString());
//        }
//    }
//
//
private List<StampedDetectedObjects> camera1; // Field name matches "camera1" in JSON

    public List<StampedDetectedObjects> getCamera1() {
        return camera1;
    }

    public void setCamera1(List<StampedDetectedObjects> camera1) {
        this.camera1 = camera1;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Detected objects:\n");
        if (camera1 != null) {
            for (StampedDetectedObjects obj : camera1) {
                result.append(obj.toString()).append("\n");
            }
        }
        return result.toString();
    }




}
