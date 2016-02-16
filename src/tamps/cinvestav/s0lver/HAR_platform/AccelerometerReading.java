package tamps.cinvestav.s0lver.HAR_platform;

import java.util.Date;

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

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
