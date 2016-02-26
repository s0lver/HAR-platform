package tamps.cinvestav.s0lver.HAR_platform.modules;

import tamps.cinvestav.s0lver.HAR_platform.entities.AccelerometerReading;

import java.util.ArrayList;

/***
 * Filters out the gravity from a set of accelerometer readings
 */
public class GravityFilterer {
    private static final float ALPHA = (float) 0.8;
    private float[] currentGravity;
    private ArrayList<AccelerometerReading> accelerometerReadings;

    public GravityFilterer(ArrayList<AccelerometerReading> accelerometerReadings) {
        this.accelerometerReadings = accelerometerReadings;
        currentGravity = new float[3];
    }

    /***
     * Filters the gravity of the readings.
     * If LINEAR_ACCELERATION sensor is employed as the source of data, then this method should not be employed.
     */
    public void filterGravity() {
        for (AccelerometerReading reading : accelerometerReadings) {
            processReading(reading);
        }
    }

    /***
     * Processes each accelerometer reading
     * @param reading The AccelerometerReading to process. It is <b>modified</b> inside.
     */
    private void processReading(AccelerometerReading reading) {
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
