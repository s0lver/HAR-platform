package tamps.cinvestav.s0lver.HAR_platform.mobility.io;

import android.location.Location;
import tamps.cinvestav.s0lver.HAR_platform.mobility.utils.Constants;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class SmartphoneFixesFileReader {
    private final int LATITUDE = 1;
    private final int LONGITUDE = 2;
    private final int ALTITUDE = 3;
    private final int ACCURACY = 4;
    private final int SPEED = 5;
    private final int TIMESTAMP = 6;

    private Scanner scanner;

    public SmartphoneFixesFileReader(String path) {
        try {
            scanner = new Scanner(new FileReader(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("I couldn't open the file. Additionally I hate checked exceptions");
        }
    }

    public Location readLine() {
        Location location = null;
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            location = processLine(line);
        }
        return location;
    }

    public ArrayList<Location> readFile() {
        ArrayList<Location> gpsFixes = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Location gpsFix = processLine(line);
            if (gpsFix != null)
                gpsFixes.add(gpsFix);
        }

        scanner.close();
        return gpsFixes;
    }

    private Location processLine(String line) {
        Location fix;
        String[] slices = line.split(",");
        if (slices[0].equals("Si")) {
            try {
                fix = new Location("CustomProvider");
                fix.setLatitude(Double.parseDouble(slices[LATITUDE]));
                fix.setLongitude(Double.parseDouble(slices[LONGITUDE]));
                fix.setAltitude(Double.parseDouble(slices[ALTITUDE]));
                fix.setAccuracy(Float.parseFloat(slices[ACCURACY]));
                fix.setSpeed(Float.parseFloat(slices[SPEED]));
                fix.setTime(Constants.RECORDS_SIMPLE_DATE_FORMAT.parse(slices[TIMESTAMP]).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException("I couldn't parse the date, and I hate checked exceptions");
            }
        }else{
            fix = new Location("null");
        }
        return fix;
    }

}
