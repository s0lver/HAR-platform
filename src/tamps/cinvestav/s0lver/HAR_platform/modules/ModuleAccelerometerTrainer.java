package tamps.cinvestav.s0lver.HAR_platform.modules;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.activities.ActivityPattern;
import tamps.cinvestav.s0lver.HAR_platform.classifiers.NaiveBayesConfiguration;
import tamps.cinvestav.s0lver.HAR_platform.classifiers.NaiveBayesTrainer;
import tamps.cinvestav.s0lver.HAR_platform.io.NaiveBayesConfigurationFileWriter;
import tamps.cinvestav.s0lver.HAR_platform.io.TrainingFilesReader;
import tamps.cinvestav.s0lver.HAR_platform.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/***
 * Module for training a Naive Bayes classifier.
 */
public class ModuleAccelerometerTrainer {
    private Context context;
    private NaiveBayesConfiguration naiveBayesConfiguration;

    /***
     * Constructor
     * @param context Context for accessing the assets folder
     */
    public ModuleAccelerometerTrainer(Context context) {
        this.context = context;
    }

    /***
     * Calls the training of a naive bayes instance using a list of patterns read from file
     * @return The training configuration result
     */
    public NaiveBayesConfiguration train() {
        ArrayList<ActivityPattern> patterns = null;
        try {
            patterns = loadPatternsFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NaiveBayesTrainer naiveBayesTrainer = new NaiveBayesTrainer(patterns);
        naiveBayesConfiguration = naiveBayesTrainer.train();
        writeNaiveBayesConfigFile();
        return naiveBayesConfiguration;
    }

    /***
     * Writes the result of the training into a file
     */
    private void writeNaiveBayesConfigFile() {
        new NaiveBayesConfigurationFileWriter(naiveBayesConfiguration).writeFile();
        Log.i(this.getClass().getSimpleName(), "Training done!");
    }

    /***
     * Loads the patterns from the files
     * @throws IOException When accessing the file
     */
    private ArrayList<ActivityPattern> loadPatternsFromFile() throws IOException {
        ArrayList<ActivityPattern> patterns = new ArrayList<>();
        ArrayList<ActivityPattern> patternsStatic = TrainingFilesReader.readStaticFile(context);
        ArrayList<ActivityPattern> patternsWalking = TrainingFilesReader.readWalkingFile(context);
        ArrayList<ActivityPattern> patternsRunning = TrainingFilesReader.readRunningFile(context);

        patterns.addAll(patternsStatic);
        patterns.addAll(patternsWalking);
        patterns.addAll(patternsRunning);
        return patterns;
    }
}
