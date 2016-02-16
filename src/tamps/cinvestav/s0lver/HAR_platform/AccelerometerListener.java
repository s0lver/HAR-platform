package tamps.cinvestav.s0lver.HAR_platform;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

@Deprecated
public class AccelerometerListener implements SensorEventListener{
    private SensorManager sensorManager;
    private Sensor sensor;
    private int sizeOfWindow;
    private int sizeOfAveragedSamples;

    public AccelerometerListener(Context context, int sizeOfWindow, int sizeOfAveragedSamples) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.sizeOfWindow = sizeOfWindow;
        this.sizeOfAveragedSamples = sizeOfAveragedSamples;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
