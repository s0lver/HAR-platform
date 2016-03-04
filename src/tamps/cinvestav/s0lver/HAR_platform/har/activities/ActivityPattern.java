package tamps.cinvestav.s0lver.HAR_platform.har.activities;

/***
 * Represents a basic pattern that can be employed for classification purposes
 */
public class ActivityPattern {
    private byte type;
    private double mean;
    private double standardDeviation;

    public ActivityPattern(byte type, double standardDeviation, double mean) {
        this.type = type;
        this.standardDeviation = standardDeviation;
        this.mean = mean;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public double getMean() {
        return mean;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }
}
