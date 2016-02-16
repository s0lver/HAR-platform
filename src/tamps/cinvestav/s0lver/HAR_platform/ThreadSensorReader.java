package tamps.cinvestav.s0lver.HAR_platform;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

// public class ThreadSensorReader implements Runnable, SensorEventListener{
public class ThreadSensorReader implements SensorEventListener{
    private boolean isRunning;

    private SensorManager sensorManager;
    private Sensor sensor;
    private int sizeOfWindow;
    private int sizeOfAveragedSamples;

    private ArrayList<AccelerometerReading> buffer;
//    private short recordsCount;

    public ThreadSensorReader(Context context, int sizeOfWindow, int sizeOfAveragedSamples) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.sizeOfWindow = sizeOfWindow;
        this.sizeOfAveragedSamples = sizeOfAveragedSamples;
    }

//    @Override
//    public void run() {
//        isRunning = true;
//        startReadings();
//        while (isRunning) {
//
//        }
//    }
//
//    public void stopThread(){
//        isRunning = false;
//    }

    public void startReadings(){
//        recordsCount = 0;
        buffer = new ArrayList<>();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopReadings(){
        sensorManager.unregisterListener(this, sensor);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        AccelerometerReading accReading = new AccelerometerReading(values[0], values[1], values[2], sensorEvent.timestamp);
        buffer.add(accReading);
//        recordsCount++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {    }
}

