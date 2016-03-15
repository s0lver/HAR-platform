package tamps.cinvestav.s0lver.HAR_platform.har.classifiers;

/***
 * Models the information learned by a NaiveBayes classifier, which is employed for performing
 * the actual classification
 * @see NaiveBayesClassifier
 */
public class NaiveBayesConfiguration {
    private final double[] probabilityPerClass;
    private final double[][] meanPerClass;
    private final double[][] variancePerClass;

    /***
     * Constructor (The order of classes in each vector must be maintained)
     * @param probabilityPerClass The probability per each class
     * @param meanPerClass The mean per dimension, per class
     * @param variancePerClass The variance per dimension, per class
     */
    public NaiveBayesConfiguration(double[] probabilityPerClass, double[][] meanPerClass, double[][] variancePerClass) {
        this.probabilityPerClass = probabilityPerClass;
        this.meanPerClass = meanPerClass;
        this.variancePerClass = variancePerClass;
    }

    public double[] getProbabilityPerClass() {
        return probabilityPerClass;
    }

    public double[][] getMeanPerClass() {
        return meanPerClass;
    }

    public double[][] getVariancePerClass() {
        return variancePerClass;
    }
}
