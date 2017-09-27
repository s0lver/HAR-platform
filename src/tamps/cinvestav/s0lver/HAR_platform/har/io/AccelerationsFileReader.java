package tamps.cinvestav.s0lver.HAR_platform.har.io;

import android.os.Environment;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.har.entities.AccelerometerReading;
import tamps.cinvestav.s0lver.HAR_platform.har.entities.AccelerometerReadingWithRunId;

import java.io.*;
import java.util.ArrayList;

public class AccelerationsFileReader {
    private String filename;

    public AccelerationsFileReader(String filename) {
        this.filename = filename;
    }

    public ArrayList<AccelerometerReadingWithRunId> readFile() throws IOException {
        ArrayList<AccelerometerReadingWithRunId> readings = new ArrayList<>();
        String filePath = Environment.getExternalStorageDirectory() + File.separator
                + "har-system-training-files" + File.separator + filename;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String line = reader.readLine();
        while (line != null) {
            AccelerometerReadingWithRunId reading = processLine(line);
            readings.add(reading);
            line = reader.readLine();
        }
        reader.close();

        return readings;
    }

    private AccelerometerReadingWithRunId processLine(String line) {
        String[] slices = line.split(",");
        int run = Integer.valueOf(slices[0]);
        double x = Double.valueOf(slices[1]);
        double y = Double.valueOf(slices[2]);
        double z = Double.valueOf(slices[3]);
        AccelerometerReading reading =  new AccelerometerReading(x, y, z, Long.valueOf(slices[4]));

        return new AccelerometerReadingWithRunId(run, reading);
    }


}
