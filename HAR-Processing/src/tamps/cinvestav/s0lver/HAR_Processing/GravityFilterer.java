package tamps.cinvestav.s0lver.HAR_Processing;

import tamps.cinvestav.s0lver.HAR_Entities.entities.AccelerometerReading;

public class GravityFilterer {
    private static final float ALPHA = (float) 0.8;
    private float[] currentGravity;

    public GravityFilterer() {
        currentGravity = new float[3];
    }

    public void filterGravity(AccelerometerReading reading) {
        // Isolate the force of gravity
        currentGravity[0] = ALPHA * currentGravity[0] + (1 - ALPHA) * reading.getX();
        currentGravity[1] = ALPHA * currentGravity[1] + (1 - ALPHA) * reading.getY();
        currentGravity[2] = ALPHA * currentGravity[2] + (1 - ALPHA) * reading.getZ();

        // Remove the gravity contribution with the high-pass filter
        reading.setX(reading.getX() - currentGravity[0]);
        reading.setY(reading.getY() - currentGravity[1]);
        reading.setZ(reading.getZ() - currentGravity[2]);
    }
}
