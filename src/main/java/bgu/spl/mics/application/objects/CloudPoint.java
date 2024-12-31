package bgu.spl.mics.application.objects;

/**
 * CloudPoint represents a specific point in a 3D space as detected by the LiDAR.
 * These points are used to generate a point cloud representing objects in the environment.
 */
public class CloudPoint {

    private double x;
    private double y;
    private double z;

    CloudPoint(double x, double y) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {return x;}
    public double getY() {return y;}
    public double getZ(){return z;}
    public void setX(double x) {this.x = x;}
    public void setY(double y) {this.y = y;}
    public void setZ(double z){this.z = z;}


    public String toString() {
        return "{" + "x=" + x + ", y=" + y + '}';
    }
}
