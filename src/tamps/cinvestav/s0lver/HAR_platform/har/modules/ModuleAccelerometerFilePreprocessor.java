package tamps.cinvestav.s0lver.HAR_platform.har.modules;

import tamps.cinvestav.s0lver.HAR_platform.har.entities.AccelerometerReading;
import tamps.cinvestav.s0lver.HAR_platform.har.entities.AccelerometerReadingWithRunId;
import tamps.cinvestav.s0lver.HAR_platform.har.io.AccelerationsFileReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/***
 * Preprocesses a file of raw accelerometer readings, generating the corresponding patterns file.
 */
public class ModuleAccelerometerFilePreprocessor {
    private final Date startTime;
    String inputFile, outputFile;

    public ModuleAccelerometerFilePreprocessor(String inputFile, String outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.startTime = new Date(System.currentTimeMillis());
    }

    public void preProcessFile() {
        try {
            AccelerationsFileReader reader = new AccelerationsFileReader(inputFile);
            ArrayList<AccelerometerReadingWithRunId> readings = reader.readFile();
            int sizeReadings = readings.size();
            int amountRuns = readings.get(sizeReadings - 1).getRunId();

            for (int i = 1; i <= amountRuns; i++) {
                ArrayList<AccelerometerReadingWithRunId> readingsOfRun = getReadingsOfRun(readings, i);
                ArrayList<AccelerometerReading> accelerometerReadings = extractReadings(readingsOfRun);
                ModuleAccelerometerPreprocessor preprocessor = new ModuleAccelerometerPreprocessor(
                        accelerometerReadings, outputFile, 1000 * 5, 3, startTime);
                preprocessor.preprocessSamplingWindow();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<AccelerometerReading> extractReadings(ArrayList<AccelerometerReadingWithRunId> readingsRun) {
        ArrayList<AccelerometerReading> readings = new ArrayList<>();
        for (AccelerometerReadingWithRunId reading : readingsRun) {
            readings.add(reading.getReading());
        }
        return readings;
    }

    private ArrayList<AccelerometerReadingWithRunId> getReadingsOfRun(ArrayList<AccelerometerReadingWithRunId> readings, int runId) {
        ArrayList<AccelerometerReadingWithRunId> filteredReadings = new ArrayList<>();
        for (AccelerometerReadingWithRunId reading : readings) {
            if (reading.getRunId() == runId) {
                filteredReadings.add(reading);
            }
        }
        return filteredReadings;

    }
}
