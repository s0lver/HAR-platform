package tamps.cinvestav.s0lver.HAR_platform.har.io;

import android.content.Context;
import tamps.cinvestav.s0lver.HAR_platform.har.classifiers.NaiveBayesConfiguration;
import tamps.cinvestav.s0lver.HAR_platform.har.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/***
 * Reads a NaiveBayesConfiguration object from file
 * @see NaiveBayesConfiguration
 */
public class NaiveBayesConfigurationFileReader {
    public static NaiveBayesConfiguration readFile(Context context) throws IOException {
        double[] probabilityPerClass = new double[Constants.UNIQUE_CLASSES];
        double[][] meanPerClass = new double[Constants.TOTAL_DIMENSIONS][Constants.UNIQUE_CLASSES];
        double[][] variancePerClass = new double[Constants.TOTAL_DIMENSIONS][Constants.UNIQUE_CLASSES];

        String filepath = "training-configuration.csv";
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filepath)));

        int i = 0;
        String line = reader.readLine();
        while (line != null) {
            // 1. Probability of this class
            probabilityPerClass[i] = Double.valueOf(line);

            // 2. Means
            line = reader.readLine();
            String[] slices = line.split(",");
            meanPerClass[Constants.STD_DEV_DIMENSION][i] = Double.valueOf(slices[Constants.STD_DEV_DIMENSION]);
            meanPerClass[Constants.MEAN_DIMENSION][i] = Double.valueOf(slices[Constants.MEAN_DIMENSION]);

            // 3. Variances
            line = reader.readLine();
            slices = line.split(",");
            variancePerClass[Constants.STD_DEV_DIMENSION][i] = Double.valueOf(slices[Constants.STD_DEV_DIMENSION]);
            variancePerClass[Constants.MEAN_DIMENSION][i] = Double.valueOf(slices[Constants.MEAN_DIMENSION]);

            line = reader.readLine();
            i++;
        }
        reader.close();

        return new NaiveBayesConfiguration(probabilityPerClass, meanPerClass, variancePerClass);
    }
}
