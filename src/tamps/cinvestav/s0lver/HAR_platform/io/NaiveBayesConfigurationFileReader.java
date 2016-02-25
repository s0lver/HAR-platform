package tamps.cinvestav.s0lver.HAR_platform.io;

import android.content.Context;
import tamps.cinvestav.s0lver.HAR_platform.processing.classifiers.NaiveBayesConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NaiveBayesConfigurationFileReader {
    public static NaiveBayesConfiguration readFile(Context context, int uniqueClasses) throws IOException {
        double[] probabilityPerClass = new double[uniqueClasses];
        double[][] meanPerClass = new double[2][uniqueClasses];
        double[][] variancePerClass = new double[2][uniqueClasses];

        int stdDevDimension = 0, meanDimension = 1;

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
            meanPerClass[stdDevDimension][i] = Double.valueOf(slices[stdDevDimension]);
            meanPerClass[meanDimension][i] = Double.valueOf(slices[meanDimension]);

            // 3. Standard deviations
            line = reader.readLine();
            slices = line.split(",");
            variancePerClass[stdDevDimension][i] = Double.valueOf(slices[stdDevDimension]);
            variancePerClass[meanDimension][i] = Double.valueOf(slices[meanDimension]);

            line = reader.readLine();
            i++;
        }
        reader.close();

        return new NaiveBayesConfiguration(probabilityPerClass, meanPerClass, variancePerClass);
    }
}
