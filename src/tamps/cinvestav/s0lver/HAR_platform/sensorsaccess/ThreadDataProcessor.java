package tamps.cinvestav.s0lver.HAR_platform.sensorsaccess;


import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.AccelerometerReading;

import java.util.ArrayList;

public class ThreadDataProcessor implements Runnable{
    private ArrayList<AccelerometerReading> window;

    public ThreadDataProcessor(ArrayList<AccelerometerReading> window) {
        this.window = window;
    }

    @Override
    public void run() {
        Log.i(this.getClass().getSimpleName(), "Look ma, I am processing windows of data");
        // TODO Average each n samples
        // TODO Obtain magnitude of vector
        // Calculate median and standar deviation

    }
}
