package tamps.cinvestav.s0lver.HAR_platform.har.hal;

import tamps.cinvestav.s0lver.HAR_platform.har.entities.AccelerometerReading;

import java.util.ArrayList;

/***
 * Notifies when a full window of samples has been collected
 * @see AccelerometerReading
 */
public interface AccelerometerReadingListener {
    /***
     * Triggered after a full window of samples has been collected
     * @param readings The list of readings
     */
    void onSamplingWindowCompleted(ArrayList<AccelerometerReading> readings);
}
