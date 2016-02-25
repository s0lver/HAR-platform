package tamps.cinvestav.s0lver.HAR_platform.processing.classifiers;

import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.activities.ActivityPattern;

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

    /***
     * Trains the Naive Bayes classifier using the ActivityPattern field
     */
    public void train() {
        int dimensions = 2; // We have mean and std dev dimensions
        int stdDevDimension = 0;
        int meanDimension = 1;

        countUniqueClasses();
        probabilityPerClass = new double[uniqueClasses];
        meanPerClass = new double[dimensions][uniqueClasses];
        variancePerClass = new double[dimensions][uniqueClasses];

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

//        printValues();
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



    public void classify() {
        //    % Total de clases reconocidas durante el entrenamiento
//            totalClases = size(P_wi,2);
//
//    % Inicialización de variables
//            map = zeros(totalClases,totalInstances);
//    Ypred = zeros(1,totalInstances);
//    sigma_error = 0;
//    prob = zeros(totalDimensions,totalClases);
//    pdf = zeros(totalClases, totalInstances);
//
//    % Por cada instancia a clasificar
//    for x = 1:totalInstances
//            x_i = Xtt(:,x);
//    % Por cada una de las clases
//    for k = 1:totalClases
//    % Calcular la probabilidad de que cada atributo pertenezca a esa
//    % clase
//    for j = 1:totalDimensions
//    prob(j, k) = (1/sqrt(2*pi*var_wi(j, k))) * ...
//    exp(- (power(x_i(j) - mu_wi(j,k), 2) / ...
//            (2 * var_wi(j, k))) );
//    end
//    % Calcular la productoria de las probabilidades de los atributos
//    % para esta clase
//    pdf(k, x) = prod(prob(:,k));
//    % Multiplicar por la probabilidad de esta clase (en este caso es
//            % constante) y almacenar para decisión posterior
//    map(k, x) = pdf(k, x) * P_wi(1,k);
//    end
//
//    % Una vez calculadas las probabilidades, simplemente elegir la
//    % mayor...
//            [~,predicted] = max(map(:,x));
//    % ...colocarla en Ypred...
//    Ypred(x) = predicted;
//    %...y actualizar la cantidad de errores en caso de discrepancia
//    sigma_error = sigma_error + (predicted ~= Ytt(x));
//    end
    }
}
