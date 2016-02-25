package tamps.cinvestav.s0lver.HAR_platform.processing;

import android.content.Context;
import android.os.Environment;
import tamps.cinvestav.s0lver.HAR_platform.activities.Activities;
import tamps.cinvestav.s0lver.HAR_platform.activities.ActivityPattern;
import tamps.cinvestav.s0lver.HAR_platform.entities.AccelerometerReading;
import tamps.cinvestav.s0lver.HAR_platform.io.NaiveBayesConfigurationFileReader;
import tamps.cinvestav.s0lver.HAR_platform.processing.classifiers.NaiveBayesClassifier;
import tamps.cinvestav.s0lver.HAR_platform.processing.classifiers.NaiveBayesTrainer;
import tamps.cinvestav.s0lver.HAR_platform.processing.classifiers.NaiveBayesConfiguration;
import tamps.cinvestav.s0lver.HAR_platform.processing.classifiers.NaiveBayesListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ThreadDataProcessor implements Runnable{
    private ArrayList<AccelerometerReading> samplingWindow;
    private GravityFilterer gravityFilterer;
    private double[] magnitudeVector;
    private int subSamplingWindowSize;
    private double mean;
    private double stdDev;
    private final String associatedRecordsFileName;
    private final String associatedVectorFileName;
    private int currentRun;
    private NaiveBayesClassifier naiveBayesTrainer;
    private NaiveBayesListener naiveBayesListener;
    private final static int UNIQUE_CLASES = 3;
    private Context context;

    public ThreadDataProcessor(Context context, Date startTime,
                               int currentRun, String filePrefix,
                               ArrayList<AccelerometerReading> samplingWindow, int subSamplingWindowSize,
                               NaiveBayesListener naiveBayesListener) {
        this.context = context;
        this.currentRun = currentRun;
        this.samplingWindow = samplingWindow;
        this.subSamplingWindowSize = subSamplingWindowSize;
        this.naiveBayesListener = naiveBayesListener;

        this.gravityFilterer = new GravityFilterer();
        String DATE_FORMAT = "dd-MM-yyyy_HH-mm-ss";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        this.associatedRecordsFileName = Environment.getExternalStorageDirectory() + File.separator
                + "har-system" + File.separator + filePrefix + "records_" + sdf.format(startTime) + ".csv";
        this.associatedVectorFileName = Environment.getExternalStorageDirectory() + File.separator
                + "har-system" + File.separator + filePrefix + "magnitudevector_" + sdf.format(startTime) + ".csv";
    }

    @Override
    public void run() {
        calculateMagnitudeVector();
        calculateMean();
        calculateStandardDeviation();
        buildNaiveBayes();
        testNaiveBayes();
    }

    private void testNaiveBayes() {
        ActivityPattern pattern = new ActivityPattern(Activities.RUNNING, mean, stdDev);
        byte pred = naiveBayesTrainer.classify(pattern);
        naiveBayesListener.notify(pred);
    }

    private void buildNaiveBayes() {
        try {
            NaiveBayesConfiguration nbConf = NaiveBayesConfigurationFileReader.readFile(context, UNIQUE_CLASES);
            naiveBayesTrainer = new NaiveBayesClassifier(nbConf);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /***
     * Filters out the gravity on each acceleration reading
     */
    private void filterGravity() {
        for (AccelerometerReading reading : samplingWindow) {
            gravityFilterer.filterGravity(reading);
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
     * Updates each sample, considering the subSamplingWindowSize neighbors using a simple average operation
     * If it is not called, then the samplingWindow to be processed is the original one.
     */
    private void subsample() {
        if (samplingWindow.size() < subSamplingWindowSize) return;

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
