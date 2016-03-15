package tamps.cinvestav.s0lver.HAR_platform.har.modules;

import android.os.Environment;
import tamps.cinvestav.s0lver.HAR_platform.har.activities.Activities;
import tamps.cinvestav.s0lver.HAR_platform.har.activities.ActivityPattern;
import tamps.cinvestav.s0lver.HAR_platform.har.entities.AccelerometerReading;
import tamps.cinvestav.s0lver.HAR_platform.har.io.PatternsFileWriter;
import tamps.cinvestav.s0lver.HAR_platform.har.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/***
 * Pre processes a list of AccelerometerReadings.
 * Internally builds a magnitude vector, at the end the produced output is
 * the mean and stdDev attributes of such magnitude vector.
 */
class ModuleAccelerometerPreprocessor {
    private ArrayList<AccelerometerReading> samplingWindow;
    private double[] magnitudeVector;
    private double mean;
    private double stdDev;

    private final long sizeOfWindow;
    private final int subSamplingWindowSize;
    private final String activityType;

    private Date startTime;

    public ModuleAccelerometerPreprocessor(ArrayList<AccelerometerReading> samplingWindow,
                                           String activityType,
                                           long sizeOfWindow,
                                           int subSamplingWindowSize,
                                           Date startTime) {
        this.activityType = activityType;
        this.sizeOfWindow = sizeOfWindow;
        this.samplingWindow = samplingWindow;
        this.subSamplingWindowSize = subSamplingWindowSize;
        this.startTime = new Date(System.currentTimeMillis());
        this.startTime = startTime;
    }

    public double[] preprocessSamplingWindow() {
        // filterGravity();
        calculateMagnitudeVector();
        calculateMean();
        calculateStandardDeviation();
        writeFiles();
        return new double[]{stdDev, mean};
    }

    /***
     * Write the patterns [~ (deleted) and magnitude vectors] to file system
     */
    private void writeFiles() {
        String filePrefix = activityType + "_" + (sizeOfWindow / Constants.ONE_SECOND) + "_secs_";
        String partialFilePath = Environment.getExternalStorageDirectory() + File.separator +
                "har-system" + File.separator + filePrefix;
//        String filepathMagnitudeVector = partialFilePath + "magnitudevector_" + Constants.FILE_NAMES_SIMPLE_DATE_FORMAT.format(startTime) + ".csv";
//        new MagnitudeVectorFileWriter(currentRun, filepathMagnitudeVector, magnitudeVector).writeFile();
        String filepathPatterns = partialFilePath + "patterns_" + Constants.SIMPLE_DATE_FORMAT.format(startTime) + ".csv";
        ActivityPattern pattern = new ActivityPattern(Activities.UNKNOWN, stdDev, mean);
        new PatternsFileWriter(filepathPatterns, pattern).writeFile();
    }

    /***
     * Filters out the gravity on each acceleration reading
     */
    private void filterGravity() {
        GravityFilterer gravityFilterer = new GravityFilterer(samplingWindow);
        gravityFilterer.filterGravity();
    }

    /***
     * Calculates the standard deviation of the magnitude vector
     */
    private void calculateStandardDeviation() {
        double sum = 0, difference;
        for (double v : magnitudeVector) {
            difference = v - mean;
            sum += (difference * difference);
        }
        this.stdDev = Math.sqrt(sum / magnitudeVector.length);
    }

    /***
     * Calculates the mean of the magnitude vector
     */
    private void calculateMean() {
        double sum = 0;
        for (double v : magnitudeVector) {
            sum += v;
        }

        this.mean = sum / magnitudeVector.length;
    }

    /***
     * Calculates the magnitude vector from the samplingWindow list of accelerometer readings.
     */
    private void calculateMagnitudeVector() {
        float sum;
        int averagedSize = samplingWindow.size();
        magnitudeVector = new double[averagedSize];
        int i = 0;

        for (AccelerometerReading reading : samplingWindow) {
            sum = (reading.getX() * reading.getX()) + (reading.getY() * reading.getY()) + (reading.getZ() * reading.getZ());
            magnitudeVector[i] = Math.sqrt(sum);
            i++;
        }
    }

    /***
     * Updates each sample, considering the subSamplingWindowSize neighbors using a simple average operation
     * If it is not called, then the samplingWindow to be processed is the original one.
     */
    private void subsample() {
        if (samplingWindow.size() < subSamplingWindowSize) return;
        if (subSamplingWindowSize==1) return;

        ArrayList<AccelerometerReading> subSampleWindow = new ArrayList<>();
        float sigmaX, sigmaY, sigmaZ;
        sigmaX = sigmaY = sigmaZ = 0;

        int subWindowCenter = subSamplingWindowSize % 2 == 0 ? (subSamplingWindowSize / 2) - 1 : (int) (Math.ceil((double) subSamplingWindowSize / 2) - 1);
        int startSubWindow = 0;
        int index;

        boolean inside = true;
        while (inside){
            index = startSubWindow + subWindowCenter;
            for (int i = 0; i < subSamplingWindowSize; i++) {
                sigmaX += samplingWindow.get(startSubWindow + i).getX();
                sigmaY += samplingWindow.get(startSubWindow + i).getY();
                sigmaZ += samplingWindow.get(startSubWindow + i).getZ();
            }

            AccelerometerReading currentReading = samplingWindow.get(index);
            AccelerometerReading fused = new AccelerometerReading(sigmaX / subSamplingWindowSize,
                    sigmaY / subSamplingWindowSize,
                    sigmaZ / subSamplingWindowSize,
                    currentReading.getTimestamp().getTime());
            subSampleWindow.add(fused);

            startSubWindow++;
            sigmaX = sigmaY = sigmaZ = 0;
            int nextEnd = startSubWindow + subSamplingWindowSize - 1;
            if (nextEnd >= samplingWindow.size()) {
                inside = false;
            }
        }
        this.samplingWindow = subSampleWindow;
    }
}
