package tamps.cinvestav.s0lver.HAR_platform.har.entities;

public class AccelerometerReadingWithRunId{
    private AccelerometerReading reading;
    private int runId;

    public AccelerometerReading getReading() {
        return reading;
    }

    public void setReading(AccelerometerReading reading) {
        this.reading = reading;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public AccelerometerReadingWithRunId(int runId, AccelerometerReading reading) {
        this.reading = reading;
        this.runId = runId;
    }
}