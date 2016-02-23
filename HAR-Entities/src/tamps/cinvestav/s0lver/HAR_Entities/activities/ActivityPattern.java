package tamps.cinvestav.s0lver.HAR_Entities.activities;

public class ActivityPattern {
    private byte type;
    private double mean;
    private double standardDeviation;

    public ActivityPattern(byte type, double mean, double standardDeviation) {
        this.type = type;
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }

    public byte getType() {
        return type;
    }

    public double getMean() {
        return mean;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }
}
