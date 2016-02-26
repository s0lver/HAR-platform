package tamps.cinvestav.s0lver.HAR_platform.hal;

import tamps.cinvestav.s0lver.HAR_platform.entities.AccelerometerReading;

import java.util.ArrayList;

public interface AccelerometerReadingListener {
    void onSamplingWindowCompleted(ArrayList<AccelerometerReading> readings);
}
