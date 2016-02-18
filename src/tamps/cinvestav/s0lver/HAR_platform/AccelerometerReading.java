package tamps.cinvestav.s0lver.HAR_platform;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AccelerometerReading {
    private float x, y, z;

    private Date timestamp;

    public AccelerometerReading(float x, float y, float z, long timestamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = new Date(timestamp);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
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
