package tamps.cinvestav.s0lver.HAR_platform.sensorsaccess.datanalysis;

import tamps.cinvestav.s0lver.HAR_platform.AccelerometerReading;

public class GravityFilterer {
    private static final float ALPHA = (float) 0.8;

    public static void filterGravity(AccelerometerReading reading) {
        float[] gravity = new float[3];
        // Isolate the force of gravity
        gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * reading.getX();
        gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * reading.getY();
        gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * reading.getZ();

        // Remove the gravity contribution with the high-pass filter
        reading.setX(reading.getX() - gravity[0]);
        reading.setY(reading.getY() - gravity[1]);
        reading.setZ(reading.getZ() - gravity[2]);
    }
}
