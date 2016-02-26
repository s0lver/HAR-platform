package tamps.cinvestav.s0lver.HAR_platform.modules;

import android.content.Context;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.activities.ActivityPattern;
import tamps.cinvestav.s0lver.HAR_platform.classifiers.NaiveBayesConfiguration;
import tamps.cinvestav.s0lver.HAR_platform.classifiers.NaiveBayesTrainer;
import tamps.cinvestav.s0lver.HAR_platform.io.TrainingFilesReader;

import java.io.IOException;
import java.util.ArrayList;

/***
 * Module for training a Naive Bayes classifier.
 */
public class ModuleTrainer {
    private Context context;
    private NaiveBayesTrainer naiveBayesTrainer;

    public ModuleTrainer(Context context) {
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
        naiveBayesTrainer = new NaiveBayesTrainer(patterns);
        writeTrainingFile();
        return naiveBayesTrainer.train();
    }

    private void writeTrainingFile() {
        // TODO perform actual file writing
        Log.i(this.getClass().getSimpleName(), "Training done!");
    }

    /***
     * Loads the patterns from the files
     * @throws IOException
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
