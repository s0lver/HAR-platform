package tamps.cinvestav.s0lver.HAR_platform.har.entities;

import java.util.Date;

/***
 * Represents a record of an accelerometer sample
 */
public class AccelerometerReading {
    private double x, y, z;

    private Date timestamp;

    public AccelerometerReading(double x, double y, double z, long timestamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = new Date(timestamp);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z + "," + timestamp.getTime();
    }

}
