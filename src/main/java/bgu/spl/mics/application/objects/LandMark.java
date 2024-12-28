package bgu.spl.mics.application.objects;
import java.util.List;


/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private String id;
    private String description;
    private List<CloudPoint> list;

    LandMark(String id, String description, List<CloudPoint> list) {
        this.id = id;
        this.description = description;
        this.list = list;
    }

    public String getId() {return id;}
    public String getDescription() {return description;}
    public List<CloudPoint> getList() {return list;}


}
