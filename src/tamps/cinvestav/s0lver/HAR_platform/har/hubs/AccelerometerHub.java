package tamps.cinvestav.s0lver.HAR_platform.har.hubs;

import android.content.Context;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.har.classifiers.NaiveBayesConfiguration;
import tamps.cinvestav.s0lver.HAR_platform.har.classifiers.NaiveBayesListener;
import tamps.cinvestav.s0lver.HAR_platform.har.modules.*;
import tamps.cinvestav.s0lver.HAR_platform.har.utils.Constants;

/***
 * Concentrates the full-set of operations available through the linear acceleration sensor
 * @see ModuleAccelerometerLogger
 * @see ModuleAccelerometerTrainer
 * @see ModuleAccelerometerClassifier
 */
public class AccelerometerHub {
    private final Context context;
    private ModuleAccelerometerLogger logger;
    private ModuleAccelerometerClassifier classifier;

    /***
     * Constructor
     * @param context Context for passing to the different involved modules
     */
    public AccelerometerHub(Context context) {
        this.context = context;
    }

    /***
     * Starts the collection of data from the accelerometer
     * @param activityType The type of the activity to be monitored
     */
    public void startDataCollection(String activityType) {
        logger = new ModuleAccelerometerLogger(context, activityType, Constants.WINDOW_LENGTH);
        logger.startAccelerometerReadings();
    }

    /***
     * Stops the collection of data from the accelerometer
     */
    public void stopDataCollection() {
        logger.stopAccelerometerReadings();
    }

    /***
     * Performs a round of training using the training files located at the har-system folder
     * @return A NaiveBayesConfiguration with the result of the training
     */
    public NaiveBayesConfiguration trainClassifier() {
        ModuleAccelerometerTrainer trainer = new ModuleAccelerometerTrainer();
        return trainer.train();
    }

    /***
     * Starts the classification of live data collected from the linear acceleration sensor
     * @param naiveBayesListener A NaiveBayesListener for notifying the classification of a pattern
     * @see NaiveBayesListener
     */
    public void startClassification(NaiveBayesListener naiveBayesListener) {
        classifier = new ModuleAccelerometerClassifier(context, naiveBayesListener, Constants.WINDOW_LENGTH);
        classifier.startClassification();
    }

    /***
     * Stops the classification of live data from the acceleration sensor
     */
    public void stopClassification() {
        classifier.stopClassification();
    }

    public void buildPatternFiles() {
        ModuleAccelerometerFilePreprocessor fp = new ModuleAccelerometerFilePreprocessor("raw-static.csv", "patterns-static.csv");
        fp.preProcessFile();
        Log.i("hub", "pattern file generated for static");

        fp = new ModuleAccelerometerFilePreprocessor("raw-walking.csv", "patterns-walking.csv");
        fp.preProcessFile();
        Log.i("hub", "pattern file generated for walking");

        fp = new ModuleAccelerometerFilePreprocessor("raw-running.csv", "patterns-running.csv");
        fp.preProcessFile();
        Log.i("hub", "pattern file generated for running");

        fp = new ModuleAccelerometerFilePreprocessor("raw-vehicle.csv", "patterns-vehicle.csv");
        fp.preProcessFile();
        Log.i("hub", "pattern file generated for vehicle");
    }
}
