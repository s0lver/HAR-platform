package tamps.cinvestav.s0lver.HAR_platform.mobility.io;

import android.location.Location;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.battery.BatteryStatus;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.LocationAnalyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GpsLocationsFileWriter {
    private final Location location;
    private final File locationsFile;
    private BatteryStatus batteryStatus;

    public GpsLocationsFileWriter(String filePath, Location location) {
        this.location = location;
        this.locationsFile = new File(filePath);
    }

    public GpsLocationsFileWriter(String filepath, Location location, BatteryStatus batteryStatus) {
        this.location = location;
        this.locationsFile = new File(filepath);
        this.batteryStatus = batteryStatus;
    }

    public void writeFile(){
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(locationsFile, true));
            String line = LocationAnalyzer.convertLocationToCsv(location);
            if (batteryStatus != null) {
                line += "," + batteryStatus.toCSV();
            }
            pw.println(line);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(this.getClass().getSimpleName(), "I could not write the locations file");
        }
    }
}
