package tamps.cinvestav.s0lver.HAR_platform.har.io;

import android.os.Environment;
import tamps.cinvestav.s0lver.HAR_platform.har.classifiers.NaiveBayesConfiguration;
import tamps.cinvestav.s0lver.HAR_platform.har.utils.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/***
 * Writes a NaiveBayesConfiguration object into a file
 * @see NaiveBayesConfiguration
 */
public class NaiveBayesConfigurationFileWriter {
    private final NaiveBayesConfiguration naiveBayesConfiguration;


    public NaiveBayesConfigurationFileWriter(NaiveBayesConfiguration naiveBayesConfiguration) {
        this.naiveBayesConfiguration = naiveBayesConfiguration;
    }

    public void writeFile() {
        double[] probabilityPerClass = naiveBayesConfiguration.getProbabilityPerClass();
        double[][] variancePerClass = naiveBayesConfiguration.getVariancePerClass();
        double[][] meanPerClass = naiveBayesConfiguration.getMeanPerClass();
        try {
            String filePath = Environment.getExternalStorageDirectory() + File.separator
                    + "har-system-training-files" + File.separator + "training-configuration.csv";
            PrintWriter pw = new PrintWriter(new FileWriter(filePath));
            for (int i = 0; i < Constants.UNIQUE_CLASSES; i++) {
                // 1. Probability of this class
                pw.println(probabilityPerClass[i]);
                // 2. Means
                pw.println(meanPerClass[Constants.STD_DEV_DIMENSION][i] + "," + meanPerClass[Constants.MEAN_DIMENSION][i]);
                // 3. Variances
                pw.println(variancePerClass[Constants.STD_DEV_DIMENSION][i] + "," + variancePerClass[Constants.MEAN_DIMENSION][i]);
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
