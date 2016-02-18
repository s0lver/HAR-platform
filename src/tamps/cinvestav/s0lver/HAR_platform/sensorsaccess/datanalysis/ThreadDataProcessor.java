package tamps.cinvestav.s0lver.HAR_platform.sensorsaccess.datanalysis;

import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.AccelerationsFileWriter;
import tamps.cinvestav.s0lver.HAR_platform.AccelerometerReading;
import tamps.cinvestav.s0lver.HAR_platform.MagnitudeVectorFileWriter;

import java.util.ArrayList;
import java.util.Date;

public class ThreadDataProcessor implements Runnable{
    private ArrayList<AccelerometerReading> samplingWindow;
    private double[] magnitudeVector;
    private int sizeOfAveragedSamples;
    private double mean;
    private double stdDev;
    private String filePrefix;

    public ThreadDataProcessor(String filePrefix, ArrayList<AccelerometerReading> samplingWindow, int sizeOfAveragedSamples) {
        this.filePrefix = filePrefix;
        this.samplingWindow = samplingWindow;
        this.sizeOfAveragedSamples = sizeOfAveragedSamples;
    }

    @Override
    public void run() {
        Date startTime = new Date(System.currentTimeMillis());
        Log.i(this.getClass().getSimpleName(), "Processing a sampling window of " + samplingWindow.size() + " elements");

        new AccelerationsFileWriter(filePrefix, startTime, samplingWindow).writeFile();
        filterGravity();
        fuseFromAverage();

        calculateMagnitudeVector();
        calculateMean();
        calculateStandardDeviation();
        new MagnitudeVectorFileWriter(filePrefix, startTime, stdDev, mean, magnitudeVector).writeFile();
    }

    /***
     * Filters out the gravity on each acceleration reading
     */
    private void filterGravity() {
        for (AccelerometerReading reading : samplingWindow) {
            GravityFilterer.filterGravity(reading);
        }
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

        for (AccelerometerReading average : samplingWindow) {
            sum = (average.getX() * average.getX()) + (average.getY() * average.getY()) + (average.getZ() * average.getZ());
            magnitudeVector[i] = Math.sqrt(sum);
            i++;
        }
    }

    /***
     * Fuses each sizeOfAveragedSamples samples into one, using a simple average operation, and stores it
     * into the same list of accelerometer readings.
     * If it is not called, then the samplingWindow to be processed is the original one.
     */
    private void fuseFromAverage() {
        ArrayList<AccelerometerReading> averagedWindow = new ArrayList<>();
        int i = 0;
        float sigmaX, sigmaY, sigmaZ;
        long sigmaTime = 0;
        sigmaX = sigmaY = sigmaZ = 0;
        for (AccelerometerReading reading : samplingWindow) {
            i++;
            sigmaX += reading.getX();
            sigmaY += reading.getY();
            sigmaZ += reading.getZ();
            sigmaTime += reading.getTimestamp().getTime();

            if (i == sizeOfAveragedSamples) {
                AccelerometerReading average =
                        new AccelerometerReading(sigmaX / sizeOfAveragedSamples,
                                sigmaY / sizeOfAveragedSamples,
                                sigmaZ / sizeOfAveragedSamples,
                                sigmaTime / sizeOfAveragedSamples);

                averagedWindow.add(average);
                sigmaX = sigmaY = sigmaZ = sigmaTime = 0;
                i = 0;
            }
        }

        if (i != 0) {
            AccelerometerReading average = new AccelerometerReading(sigmaX / i, sigmaY / i, sigmaZ / i, sigmaTime / i);
            averagedWindow.add(average);
        }

        this.samplingWindow = averagedWindow;
    }
}
