package tamps.cinvestav.s0lver.HAR_platform.processing.classifiers;

import android.os.Environment;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.activities.ActivityPattern;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class NaiveBayes {
    private final double laplaceCorrection = 0.01;
    private ArrayList<ActivityPattern> patterns;

    private byte uniqueClasses;
    private double[] probabilityPerClass;
    private double[][] meanPerClass;
    private double[][] variancePerClass;

    double[] pdfPerClass;
    double[] MAP;

    int stdDevDimension = 0;
    int meanDimension = 1;
    int totalDimensions = 2;

    /***
     * Constructor used when training
     * @param patterns    The patterns to train the NaiveBayes classifier
     */
    public NaiveBayes(ArrayList<ActivityPattern> patterns) {
        this.patterns = patterns;
    }

    /***
     * Constructor used when classifying
     * @param nbConfiguration
     */
    public NaiveBayes(NaiveBayesConfiguration nbConfiguration) {
        this.probabilityPerClass = nbConfiguration.getProbabilityPerClass();
        this.meanPerClass = nbConfiguration.getMeanPerClass();
        this.variancePerClass = nbConfiguration.getVariancePerClass();
        this.uniqueClasses = 3;
    }

    /***
     * Trains the Naive Bayes classifier using the ActivityPattern field
     */
    public void train() {
        countUniqueClasses();
        probabilityPerClass = new double[uniqueClasses];
        meanPerClass = new double[totalDimensions][uniqueClasses];
        variancePerClass = new double[totalDimensions][uniqueClasses];

        for (int i = 0; i < uniqueClasses; i++) {
            ArrayList<ActivityPattern> patternsOfCurrentClass = getPatternsOfClass(i + 1); // Remember first class is 1

            probabilityPerClass[i] = (double)patternsOfCurrentClass.size() / (double) patterns.size();

            double[] means = calculateMeans(patternsOfCurrentClass);
            meanPerClass[stdDevDimension][i] = means[stdDevDimension] + laplaceCorrection; // 0,i = std-dimension of this class
            meanPerClass[meanDimension][i] = means[meanDimension] + laplaceCorrection; // 1,i = mean-dimension of this class

            double[] variances = calculateVariances(patternsOfCurrentClass, means);
            variancePerClass[stdDevDimension][i] = variances[stdDevDimension] + laplaceCorrection;
            variancePerClass[meanDimension][i] = variances[meanDimension] + laplaceCorrection;
        }

        writeData();
    }

    private void writeData() {
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

    public byte classify(ActivityPattern pattern) {
        double[][] probability = new double[totalDimensions][uniqueClasses];
        pdfPerClass = new double[uniqueClasses];
        MAP = new double[uniqueClasses];

        for (int k = 0; k < uniqueClasses; k++) {
            probability[stdDevDimension][k] = (1 / Math.sqrt(2 * Math.PI * variancePerClass[stdDevDimension][k]))
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
