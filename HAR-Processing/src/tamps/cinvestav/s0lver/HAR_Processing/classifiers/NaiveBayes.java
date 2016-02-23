package tamps.cinvestav.s0lver.HAR_Processing.classifiers;

import tamps.cinvestav.s0lver.HAR_Entities.activities.ActivityPattern;

import java.util.ArrayList;

public class NaiveBayes {
    private final double laplaceCorrection = 0.01;
    private ArrayList<ActivityPattern> patterns;

    private byte uniqueClasses;
    private double[] probabilityPerClass;
    private double[][] meanPerClass;
    private double[][] variancePerClass;

    public NaiveBayes(ArrayList<ActivityPattern> patterns) {
        this.patterns = patterns;
    }
    //    % Corrección para evitar valores con 0 que nulifiquen a las probabilidades.
    //    % Recordar que en la clasificación, Naive Bayes es prácticamente reducido a
    //    % una multiplicatoria, por lo que cualquier valor de 0 nulifica el valor
    //    % final.
    //    laplace_correction = 0.01;
    //
    //    % Por cada clase única...
    //            for currentClass = 1:totalClasses
    //    % ... Obtener sus instancias ...
    //    classInstances = Xtr(:,Ytr==currentClass);
    //
    //    % Calcular la probabilidad de la clase (es constante debido a
    //            % crossvalind)
    //    P_wi(currentClass) = size(classInstances,2)/total_instances;
    //    for currentDim = 1:totalDimensions
    //    % ... calcular la media de cada dimensión para la clase
    //    mu_wi(currentDim, currentClass) = ...
    //    mean(classInstances(currentDim,:)) + laplace_correction;
    //    % ... calcular la varianza de cada dimensión para la clase
    //    var_wi(currentDim, currentClass) = ...
    //    var(classInstances(currentDim,:)) + laplace_correction;
    //    end
    //            end


    //

    public void train() {
        int dimensions = 2; // We have mean and std dev dimensions

        countUniqueClasses();
        probabilityPerClass = new double[uniqueClasses];
        meanPerClass = new double[dimensions][uniqueClasses];
        variancePerClass = new double[dimensions][uniqueClasses];

        for (int i = 0; i < uniqueClasses; i++) {
            ArrayList<ActivityPattern> patternsOfCurrentClass = getPatternsOfClass(i);

            probabilityPerClass[i] = patternsOfCurrentClass.size() / patterns.size();

            double[] means = calculateMeans(patternsOfCurrentClass);
            meanPerClass[0][i] = means[0] + laplaceCorrection; // 0,i = std-dimension of this class
            meanPerClass[1][i] = means[1] + laplaceCorrection; // 1,i = mean-dimension of this class

            double[] variances = calculateVariances(patternsOfCurrentClass, means);
            variancePerClass[0][i] = variances[0];
            variancePerClass[1][i] = variances[1];
        }
    }

    private double[] calculateVariances(ArrayList<ActivityPattern> patternsOfCurrentClass, double[] means) {
        double sumStdDimension = 0, sumMeanDimension = 0;

        for (ActivityPattern pattern : patternsOfCurrentClass) {
            sumStdDimension += (pattern.getStandardDeviation() - means[0]) * (pattern.getStandardDeviation() - means[0]);
            sumMeanDimension += (pattern.getMean() - means[1]) * (pattern.getMean() - means[1]);
        }

        return new double[]{
                sumStdDimension / patternsOfCurrentClass.size(),
                sumMeanDimension / patternsOfCurrentClass.size()
        };
    }

    private double[] calculateMeans(ArrayList<ActivityPattern> patternsOfCurrentClass) {
        double sumStdDeviation = 0;
        double sumMean = 0;
        for (ActivityPattern pattern : patternsOfCurrentClass) {
            sumStdDeviation = pattern.getStandardDeviation();
            sumMean = pattern.getMean();
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

    public void classify() {

    }
}
