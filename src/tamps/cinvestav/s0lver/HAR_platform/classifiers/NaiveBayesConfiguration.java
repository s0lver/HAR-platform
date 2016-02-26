package tamps.cinvestav.s0lver.HAR_platform.classifiers;

public class NaiveBayesConfiguration {
    private double[] probabilityPerClass;
    private double[][] meanPerClass;
    private double[][] variancePerClass;

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
