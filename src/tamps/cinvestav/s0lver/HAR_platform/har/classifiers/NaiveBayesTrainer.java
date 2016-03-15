package tamps.cinvestav.s0lver.HAR_platform.har.classifiers;

import android.os.Environment;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.har.activities.ActivityPattern;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class NaiveBayesTrainer {
    private final static double LAPLACE_CORRECTION = 0.01;
    private ArrayList<ActivityPattern> patterns;

    private byte uniqueClasses;
    private double[] probabilityPerClass;
    private double[][] meanPerClass;
    private double[][] variancePerClass;

    int stdDevDimension = 0;
    int meanDimension = 1;
    int totalDimensions = 2;

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
        countUniqueClasses();
        probabilityPerClass = new double[uniqueClasses];
        meanPerClass = new double[totalDimensions][uniqueClasses];
        variancePerClass = new double[totalDimensions][uniqueClasses];

        for (int i = 0; i < uniqueClasses; i++) {
            ArrayList<ActivityPattern> patternsOfCurrentClass = getPatternsOfClass(i + 1);

            probabilityPerClass[i] = (double)patternsOfCurrentClass.size() / (double) patterns.size();

            double[] means = calculateMeans(patternsOfCurrentClass);
            meanPerClass[stdDevDimension][i] = means[stdDevDimension] + LAPLACE_CORRECTION;
            meanPerClass[meanDimension][i] = means[meanDimension] + LAPLACE_CORRECTION;

            double[] variances = calculateVariances(patternsOfCurrentClass, means);
            variancePerClass[stdDevDimension][i] = variances[stdDevDimension] + LAPLACE_CORRECTION;
            variancePerClass[meanDimension][i] = variances[meanDimension] + LAPLACE_CORRECTION;
        }

        return new NaiveBayesConfiguration(probabilityPerClass, meanPerClass, variancePerClass);
    }

    /***
     * Writes the training result into a csv file
     */
    public void writeData() {
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator
                    + "har-system" + File.separator + "training-configuration.csv"));
            for (int i = 0; i < uniqueClasses; i++) {
                pw.println(probabilityPerClass[i]);
                pw.println(meanPerClass[stdDevDimension][i] + "," + meanPerClass[meanDimension][i]);
                pw.println(variancePerClass[stdDevDimension][i] + "," + variancePerClass[meanDimension][i]);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /***
     * Outputs the training configuration to the Android Log
     */
    private void printValues() {
        int stdDevDimension = 0;
        int meanDimension = 1;

        for (int i=0; i<uniqueClasses; i++) {
            Log.i(this.getClass().getSimpleName(), "Class: " + i);
            Log.i(this.getClass().getSimpleName(), "Probability=" + probabilityPerClass[i]);
            Log.i(this.getClass().getSimpleName(), "Means: StdDevDimension=" + meanPerClass[stdDevDimension][i]
                    + ", Mean=" + meanPerClass[meanDimension][i]);
            Log.i(this.getClass().getSimpleName(), "Variances: StdDevDimension=" + variancePerClass[stdDevDimension][i]
                    + ", Mean=" + variancePerClass[meanDimension][i]);
        }
    }

    /***
     * Calculates the variance of the current class
     * @param patternsOfCurrentClass Patterns of the current class
     * @param means The mean of each dimension of the current class
     * @return
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

    /***
     * Counts the unique classes [we could simply put return 3 xD]
     */
    private void countUniqueClasses() {
        this.uniqueClasses = patterns.get(patterns.size() - 1).getType();
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
