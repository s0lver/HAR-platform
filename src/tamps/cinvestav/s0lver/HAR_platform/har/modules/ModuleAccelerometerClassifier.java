package tamps.cinvestav.s0lver.HAR_platform.har.modules;

import android.content.Context;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.har.activities.Activities;
import tamps.cinvestav.s0lver.HAR_platform.har.activities.ActivityPattern;
import tamps.cinvestav.s0lver.HAR_platform.har.classifiers.NaiveBayesClassifier;
import tamps.cinvestav.s0lver.HAR_platform.har.classifiers.NaiveBayesConfiguration;
import tamps.cinvestav.s0lver.HAR_platform.har.classifiers.NaiveBayesListener;
import tamps.cinvestav.s0lver.HAR_platform.har.entities.AccelerometerReading;
import tamps.cinvestav.s0lver.HAR_platform.har.hal.AccelerometerReader;
import tamps.cinvestav.s0lver.HAR_platform.har.hal.AccelerometerReadingListener;
import tamps.cinvestav.s0lver.HAR_platform.har.io.NaiveBayesConfigurationFileReader;
import tamps.cinvestav.s0lver.HAR_platform.har.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/***
 * Class for requesting readings of accelerometer data and logging them to file
 * @see AccelerometerReading
 * @see AccelerometerReader
 * @see AccelerometerReadingListener
 */
public class ModuleAccelerometerClassifier implements AccelerometerReadingListener {
    private final Date startTime;
    private final AccelerometerReader accelerometerReader;
    private final long sizeOfWindow;
    private final NaiveBayesListener naiveBayesListener;
    private NaiveBayesConfiguration naiveBayesConfiguration;

    /***
     * Creates a ModuleAccelerometerClassifier instance that will classify accelerometer data according to the specified
     * NaiveBayesConfiguration and window size
     *
     * @param context                 Context for accessing the Sensor-Sensing system service
     * @param naiveBayesListener      The listener to call after classifying an ActivityPattern
     * @param sizeOfWindow            The size of the window on which data will be allocated, in milliseconds
     */
    public ModuleAccelerometerClassifier(Context context, NaiveBayesListener naiveBayesListener, long sizeOfWindow) {
        this.sizeOfWindow = sizeOfWindow;
        this.startTime = new Date(System.currentTimeMillis());

        this.accelerometerReader = new AccelerometerReader(context, this, sizeOfWindow);
        this.naiveBayesListener = naiveBayesListener;
        loadTrainingInformationFromFile(context);
    }

    /***
     * Load the training information from files stored in the assets folder
     * @param context The context to access to the assets folder
     */
    private void loadTrainingInformationFromFile(Context context) {
        try {
            this.naiveBayesConfiguration = NaiveBayesConfigurationFileReader.readFile(context);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(this.getClass().getSimpleName(), "Couldn't read the file, I have to go");
            throw new RuntimeException("Error when reading the file of training configuration");
        }
    }

    /***
     * Starts the process of classification, triggering the reading of data, pre processing and classification per se
     */
    public void startClassification() {
        accelerometerReader.startReadings();
    }

    /***
     * Stops the classification process
     */
    public void stopClassification() {
        accelerometerReader.stopReadings();
    }

    /***
     * Triggered after a window of sample has been filled up by the Accelerometer Reader
     * @param readings The readings collected by the AccelerometerReader
     */
    @Override
    public void onSamplingWindowCompleted(ArrayList<AccelerometerReading> readings) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ModuleAccelerometerPreprocessor preprocessor =
                        new ModuleAccelerometerPreprocessor(readings, "classifying", sizeOfWindow, 1, startTime);
                double[] preprocessedReadings = preprocessor.preProcessSamplingWindow();

                ActivityPattern pattern =
                        new ActivityPattern(Activities.UNKNOWN, preprocessedReadings[0], preprocessedReadings[1]);

                byte predictedActivityType = new NaiveBayesClassifier(naiveBayesConfiguration).classify(pattern);
                pattern.setType(predictedActivityType);

                String filePrefix = "classifier_" + (sizeOfWindow / Constants.ONE_SECOND) + "_";
//                String filepath = Environment.getExternalStorageDirectory() + File.separator + "har-system" +
// File.separator + filePrefix + Constants.SIMPLE_DATE_FORMAT.format(startTime) + ".csv";

//                PatternsFileWriter writer = new PatternsFileWriter(filepath, pattern);
//                writer.writeFile();
                naiveBayesListener.onClassifiedPattern(predictedActivityType);
            }
        }).start();
    }
}

