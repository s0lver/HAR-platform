package tamps.cinvestav.s0lver.HAR_platform.har.modules;

import android.content.Context;
import android.os.Environment;
import tamps.cinvestav.s0lver.HAR_platform.har.entities.AccelerometerReading;
import tamps.cinvestav.s0lver.HAR_platform.har.hal.AccelerometerReader;
import tamps.cinvestav.s0lver.HAR_platform.har.hal.AccelerometerReadingListener;
import tamps.cinvestav.s0lver.HAR_platform.har.io.AccelerationsFileWriter;
import tamps.cinvestav.s0lver.HAR_platform.har.utils.Constants;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/***
 * Class for requesting readings of accelerometer data and logging them to file
 * @see AccelerometerReading
 * @see AccelerometerReader
 * @see AccelerometerReadingListener
 */
public class ModuleAccelerometerLogger implements AccelerometerReadingListener {
    private int currentRun;
    private String activityType;
    private Date startTime;
    private AccelerometerReader accelerometerReader;
    private long sizeOfWindow;

    /***
     * Creates a ModuleAccelerometerLogger instance with the specified window size
     * @param context Context for accessing the Sensor-Sensing system service
     * @param activityType Type of activity (for filename)
     * @param sizeOfWindow The size of the window on which data will be allocated, this is in millisecods
     */
    public ModuleAccelerometerLogger(Context context, String activityType, long sizeOfWindow) {
        this.activityType = activityType;
        this.sizeOfWindow = sizeOfWindow;
        this.currentRun = 1;
        this.startTime = new Date(System.currentTimeMillis());

        this.accelerometerReader = new AccelerometerReader(context, this, sizeOfWindow);
    }

    public void startAccelerometerReadings() {
        accelerometerReader.startReadings();
    }

    public void stopAccelerometerReadings() {
        accelerometerReader.stopReadings();
    }

    /***
     * Triggered after a window of sample has been filled up by the Accelerometer Reader
     * @param readings The readings collected by the AccelerometerReader
     */
    @Override
    public void onSamplingWindowCompleted(ArrayList<AccelerometerReading> readings) {
        writeAccelerometerRecords(readings);
    }

    /***
     * Writes the list of accelerometer readings to a file
     * @param readings The list of accelerometer records to store
     */
    private void writeAccelerometerRecords(ArrayList<AccelerometerReading> readings) {
        String filePrefix = activityType + "_" + (sizeOfWindow / Constants.ONE_SECOND) + "_secs_";
        String filePath = Environment.getExternalStorageDirectory() + File.separator
                + "har-system" + File.separator + filePrefix + "records_" +
                Constants.SIMPLE_DATE_FORMAT.format(startTime) + ".csv";
        new AccelerationsFileWriter(currentRun, filePath, readings).writeFile();
    }
}
