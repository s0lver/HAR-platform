package tamps.cinvestav.s0lver.HAR_platform.hal;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.entities.AccelerometerReading;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/***
 * Connects to hardware and reads data from the accelerometer
 */
public class AccelerometerReader implements SensorEventListener{
    private SensorManager sensorManager;
    private Sensor sensor;
    private ArrayList<AccelerometerReading> buffer;
    private Timer timerReadings;
    private TimerTask timerTaskReading;
    private int sizeOfWindow;
    AccelerometerReadingListener readingListener;

    public AccelerometerReader(Context context, AccelerometerReadingListener readingListener, int sizeOfWindow) {
        this.readingListener = readingListener;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        this.sizeOfWindow = sizeOfWindow;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        AccelerometerReading accReading = new AccelerometerReading(values[0], values[1], values[2], System.currentTimeMillis());
        buffer.add(accReading);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {    }

    /***
     * Triggers the readings according to parameters specified in constructor
     */
    public void startReadings(){
        Log.i(this.getClass().getSimpleName(), "Starting accelerometer readings");
        buffer = new ArrayList<>();
        scheduleTimer();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /***
     * Schedules the timer that will <b>tick</b> once a window sampling is completed
     */
    private void scheduleTimer() {
        Log.i(this.getClass().getSimpleName(), "Scheduling accelerometer timer");
        timerReadings = new Timer("timerReadings");
        timerTaskReading = new TimerTaskReadings();
        timerReadings.scheduleAtFixedRate(timerTaskReading, sizeOfWindow, sizeOfWindow);
    }

    /***
     * Stop readings and the associated timers and processing components
     */
    public void stopReadings(){
        Log.i(this.getClass().getSimpleName(), "Stopping accelerometer readings");
        sensorManager.unregisterListener(this, sensor);
        cancelTimer();
    }

    /***
     * Cancels the timer (if there are any pending ticks)
     */
    private void cancelTimer() {
        Log.i(this.getClass().getSimpleName(), "Cancelling accelerometer timer");
        timerTaskReading.cancel();
        timerReadings.cancel();
        timerReadings.purge();
    }

    /***
     * Called when a window sampling is completed.
     * It notifies the list of readings to a AccelerometerReadingListener object
     * @see AccelerometerReadingListener
     */
    private void timeoutReached() {
        Log.i(this.getClass().getSimpleName(), "Timeout reached, window filled");
        ArrayList<AccelerometerReading> refBuffer = this.buffer;
        this.buffer = new ArrayList<>();
        readingListener.onSamplingWindowCompleted(refBuffer);
    }

    /***
     * Inner class for detecting each time a sampling window is filled.
     */
    class TimerTaskReadings extends TimerTask {
        @Override
        public void run() {
            timeoutReached();
        }
    }
}
