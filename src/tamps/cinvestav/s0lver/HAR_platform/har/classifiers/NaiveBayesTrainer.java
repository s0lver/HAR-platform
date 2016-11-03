package tamps.cinvestav.s0lver.HAR_platform.har.classifiers;

import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.har.activities.ActivityPattern;
import tamps.cinvestav.s0lver.HAR_platform.har.utils.Constants;

import java.util.ArrayList;

public class NaiveBayesTrainer {
    private final static double LAPLACE_CORRECTION = 0.01;
    private final ArrayList<ActivityPattern> patterns;

    private double[] probabilityPerClass;
    private double[][] meanPerClass;
    private double[][] variancePerClass;

    /***
     * Constructor for training
     * @param patterns The patterns to be employed for learning. The list of patterns can (and should) contain
     *                 mixed types of activities.
     * @see tamps.cinvestav.s0lver.HAR_platform.har.activities.Activities
     */
    public NaiveBayesTrainer(ArrayList<ActivityPattern> patterns) {
        this.patterns = patterns;
    }

    /***
     * Trains the Naive Bayes classifier using the ActivityPattern field
     */
    public NaiveBayesConfiguration train() {
        probabilityPerClass = new double[Constants.UNIQUE_CLASSES];
        meanPerClass = new double[Constants.TOTAL_DIMENSIONS][Constants.UNIQUE_CLASSES];
        variancePerClass = new double[Constants.TOTAL_DIMENSIONS][Constants.UNIQUE_CLASSES];

        for (int i = 0; i < Constants.UNIQUE_CLASSES; i++) {
            ArrayList<ActivityPattern> patternsOfCurrentClass = getPatternsOfClass(i + 1);

//            probabilityPerClass[i] = (double)patternsOfCurrentClass.size() / (double) patterns.size();
            probabilityPerClass[i] = 1.0 / Constants.UNIQUE_CLASSES;
            double[] means = calculateMeans(patternsOfCurrentClass);
            meanPerClass[Constants.STD_DEV_DIMENSION][i] = means[Constants.STD_DEV_DIMENSION] + LAPLACE_CORRECTION;
            meanPerClass[Constants.MEAN_DIMENSION][i] = means[Constants.MEAN_DIMENSION] + LAPLACE_CORRECTION;

            double[] variances = calculateVariances(patternsOfCurrentClass, means);
            variancePerClass[Constants.STD_DEV_DIMENSION][i] = variances[Constants.STD_DEV_DIMENSION] + LAPLACE_CORRECTION;
            variancePerClass[Constants.MEAN_DIMENSION][i] = variances[Constants.MEAN_DIMENSION] + LAPLACE_CORRECTION;
        }

        printValues();
        return new NaiveBayesConfiguration(probabilityPerClass, meanPerClass, variancePerClass);
    }

    /***
     * Outputs the training configuration to the Android Log
     */
    private void printValues() {
        for (int i = 0; i < Constants.UNIQUE_CLASSES; i++) {
            Log.i(this.getClass().getSimpleName(), "Class: " + i);
            Log.i(this.getClass().getSimpleName(), "Probability=" + probabilityPerClass[i]);
            Log.i(this.getClass().getSimpleName(), "Means: StdDevDimension=" + meanPerClass[Constants.STD_DEV_DIMENSION][i]
                    + ", Mean=" + meanPerClass[Constants.MEAN_DIMENSION][i]);
            Log.i(this.getClass().getSimpleName(), "Variances: StdDevDimension=" + variancePerClass[Constants.STD_DEV_DIMENSION][i]
                    + ", Mean=" + variancePerClass[Constants.MEAN_DIMENSION][i]);
        }
    }

    /***
     * Calculates the variance of the current class
     * @param patternsOfCurrentClass Patterns of the current class
     * @param means The mean of each dimension of the current class
     * @return The variances of each dimension of the current class.
     */
    private double[] calculateVariances(ArrayList<ActivityPattern> patternsOfCurrentClass, double[] means) {
        double sumStdDimension = 0, sumMeanDimension = 0;

        for (ActivityPattern pattern : patternsOfCurrentClass) {
            sumStdDimension += (pattern.getStandardDeviation() - means[0]) * (pattern.getStandardDeviation() - means[0]);
            sumMeanDimension += (pattern.getMean() - means[1]) * (pattern.getMean() - means[1]);
        }

        return new double[]{
                sumStdDimension / (patternsOfCurrentClass.size()-1),
                sumMeanDimension / (patternsOfCurrentClass.size()-1)
        };
    }

    /***
     * Calculates the means of the current class
     * @param patternsOfCurrentClass The patterns of the current class
     * @return The means of the dimensions
     */
    private double[] calculateMeans(ArrayList<ActivityPattern> patternsOfCurrentClass) {
        double sumStdDeviation = 0;
        double sumMean = 0;
        for (ActivityPattern pattern : patternsOfCurrentClass) {
            sumStdDeviation += pattern.getStandardDeviation();
            sumMean += pattern.getMean();
        }
        return new double[]{
                sumStdDeviation / patternsOfCurrentClass.size(),
                sumMean / patternsOfCurrentClass.size()
        };
    }

    private ArrayList<ActivityPattern> getPatternsOfClass(int i) {
        ArrayList<ActivityPattern> filteredPatterns = new ArrayList<>();
        for (ActivityPattern pattern : patterns) {
            if (pattern.getType() == i) {
                filteredPatterns.add(pattern);
            }
        }
        return filteredPatterns;
    }
}
