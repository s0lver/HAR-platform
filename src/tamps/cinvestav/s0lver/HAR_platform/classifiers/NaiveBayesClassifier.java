package tamps.cinvestav.s0lver.HAR_platform.classifiers;

import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.activities.ActivityPattern;

import java.util.ArrayList;

public class NaiveBayesClassifier {
    private ArrayList<ActivityPattern> patterns;
    private byte uniqueClasses;
    private NaiveBayesConfiguration nbConf;

    double[] pdfPerClass;
    double[] MAP;

    int stdDevDimension = 0;
    int meanDimension = 1;
    int totalDimensions = 2;

    /***
     * Creates a Naive Bayes classifier using the specified training information
     * @param naiveBayesConfiguration The configuration "learned" and to be employed by the NB classifier
     */
    public NaiveBayesClassifier(NaiveBayesConfiguration naiveBayesConfiguration) {
        this.nbConf = naiveBayesConfiguration;
        this.uniqueClasses = 3;
    }

    /***
     * Classifies the specified pattern
     * @param pattern The pattern to be classified
     * @return The predicted acctivity type of the pattern
     * @see tamps.cinvestav.s0lver.HAR_platform.activities.Activities
     */
    public byte classify(ActivityPattern pattern) {
        double[][] probability = new double[totalDimensions][uniqueClasses];
        double[][] variancePerClass = nbConf.getVariancePerClass();
        double[][] meanPerClass = nbConf.getMeanPerClass();
        double[] probabilityPerClass = nbConf.getProbabilityPerClass();

        pdfPerClass = new double[uniqueClasses];
        MAP = new double[uniqueClasses];

        for (int k = 0; k < uniqueClasses; k++) {
            probability[stdDevDimension][k] =
                    (1 / Math.sqrt(2 * Math.PI * variancePerClass[stdDevDimension][k]))
                    * Math.exp(-(Math.pow(pattern.getStandardDeviation() - meanPerClass[stdDevDimension][k], 2)
                    / (2 * variancePerClass[stdDevDimension][k])));

            probability[meanDimension][k] = (1 / Math.sqrt(2 * Math.PI * variancePerClass[meanDimension][k]))
                    * Math.exp(-(Math.pow(pattern.getMean() - meanPerClass[meanDimension][k], 2)
                    / (2 * variancePerClass[meanDimension][k])));

            pdfPerClass[k] = probability[stdDevDimension][k] * probability[meanDimension][k];
            MAP[k] = probabilityPerClass[k] * pdfPerClass[k];
        }

        for (int i = 0; i < uniqueClasses; i++) {
            Log.i(this.getClass().getSimpleName(), "MAP[" + i + "] = " + MAP[i]);
        }

        return getHighestMap(MAP);
    }

    /***
     * Gets the index of the highest map value
     * @param map The list of A Posteriori values
     * @return the index of the predicted activity's type
     */
    private byte getHighestMap(double[] map) {
        byte largestIndex = -1;
        double largestValue = 0;
        for (int i = 0; i < map.length; i++) {
            if (map[i] > largestValue) {
                largestIndex = (byte) (i + 1);
                largestValue = map[i];
            }
        }
        return largestIndex;
    }
}
