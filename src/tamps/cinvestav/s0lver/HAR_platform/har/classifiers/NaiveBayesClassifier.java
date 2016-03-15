package tamps.cinvestav.s0lver.HAR_platform.har.classifiers;

import tamps.cinvestav.s0lver.HAR_platform.har.activities.ActivityPattern;
import tamps.cinvestav.s0lver.HAR_platform.har.utils.Constants;

/***
 * Classifies a given pattern into a class, according to a NaiveBayesConfiguration training configuration
 * @see NaiveBayesConfiguration
 */
public class NaiveBayesClassifier {
    private final NaiveBayesConfiguration nbConf;

    /***
     * Creates a Naive Bayes classifier using the specified training information
     * @param naiveBayesConfiguration The configuration "learned" and to be employed by the NB classifier
     */
    public NaiveBayesClassifier(NaiveBayesConfiguration naiveBayesConfiguration) {
        this.nbConf = naiveBayesConfiguration;
    }

    /***
     * Classifies the specified pattern
     * @param pattern The pattern to be classified
     * @return The predicted activity type of the pattern
     * @see tamps.cinvestav.s0lver.HAR_platform.har.activities.Activities
     */
    public byte classify(ActivityPattern pattern) {
        double[][] probability = new double[Constants.TOTAL_DIMENSIONS][Constants.UNIQUE_CLASSES];
        double[][] variancePerClass = nbConf.getVariancePerClass();
        double[][] meanPerClass = nbConf.getMeanPerClass();
        double[] probabilityPerClass = nbConf.getProbabilityPerClass();

        double[] pdfPerClass = new double[Constants.UNIQUE_CLASSES];
        double[] MAP = new double[Constants.UNIQUE_CLASSES];

        for (int k = 0; k < Constants.UNIQUE_CLASSES; k++) {
            probability[Constants.STD_DEV_DIMENSION][k] =
                    (1 / Math.sqrt(2 * Math.PI * variancePerClass[Constants.STD_DEV_DIMENSION][k]))
                    * Math.exp(-(Math.pow(pattern.getStandardDeviation() - meanPerClass[Constants.STD_DEV_DIMENSION][k], 2)
                    / (2 * variancePerClass[Constants.STD_DEV_DIMENSION][k])));

            probability[Constants.MEAN_DIMENSION][k] =
                    (1 / Math.sqrt(2 * Math.PI * variancePerClass[Constants.MEAN_DIMENSION][k]))
                    * Math.exp(-(Math.pow(pattern.getMean() - meanPerClass[Constants.MEAN_DIMENSION][k], 2)
                    / (2 * variancePerClass[Constants.MEAN_DIMENSION][k])));

            pdfPerClass[k] = probability[Constants.STD_DEV_DIMENSION][k] * probability[Constants.MEAN_DIMENSION][k];
            MAP[k] = probabilityPerClass[k] * pdfPerClass[k];
        }

//        for (int i = 0; i < Constants.UNIQUE_CLASSES; i++) {
//            Log.i(this.getClass().getSimpleName(), "MAP[" + i + "] = " + MAP[i]);
//        }

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
