package tamps.cinvestav.s0lver.HAR_platform.io;

import android.content.Context;
import tamps.cinvestav.s0lver.HAR_platform.classifiers.NaiveBayesConfiguration;
import tamps.cinvestav.s0lver.HAR_platform.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NaiveBayesConfigurationFileReader {
    public static NaiveBayesConfiguration readFile(Context context) throws IOException {
        double[] probabilityPerClass = new double[Constants.UNIQUE_CLASES];
        double[][] meanPerClass = new double[Constants.TOTAL_DIMENSIONS][Constants.UNIQUE_CLASES];
        double[][] variancePerClass = new double[Constants.TOTAL_DIMENSIONS][Constants.UNIQUE_CLASES];

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
