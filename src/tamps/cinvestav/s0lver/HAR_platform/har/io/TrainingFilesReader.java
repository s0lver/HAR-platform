package tamps.cinvestav.s0lver.HAR_platform.har.io;

import android.content.Context;
import android.os.Environment;
import tamps.cinvestav.s0lver.HAR_platform.har.activities.Activities;
import tamps.cinvestav.s0lver.HAR_platform.har.activities.ActivityPattern;

import java.io.*;
import java.util.ArrayList;

/***
 * Reads the training information from files
 * @see ActivityPattern
 * @see Activities
 */
public class TrainingFilesReader {
    public static ArrayList<ActivityPattern> readStaticFile() throws IOException {
        return readFile("training-static.csv", Activities.STATIC);
    }

    public static ArrayList<ActivityPattern> readWalkingFile() throws IOException {
        return readFile("training-walking.csv", Activities.WALKING);
    }

    public static ArrayList<ActivityPattern> readRunningFile() throws IOException {
        return readFile("training-running.csv", Activities.RUNNING);
    }

    public static ArrayList<ActivityPattern> readVehicleFile() throws IOException {
        return readFile("training-vehicle.csv", Activities.VEHICLE);
    }

    private static ArrayList<ActivityPattern> readFile(String filename, byte type) throws IOException {
        ArrayList<ActivityPattern> trainingPatterns = new ArrayList<>();
        String filePath = Environment.getExternalStorageDirectory() + File.separator
                + "har-system-training-files" + File.separator + filename;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String line = reader.readLine();
        while (line != null) {
            ActivityPattern pattern = processLine(line, type);
            trainingPatterns.add(pattern);
            line = reader.readLine();
        }
        reader.close();

        return trainingPatterns;
    }

    private static ActivityPattern processLine(String line, byte activityType) {
        String[] slices = line.split(",");
        double stdDev = Double.valueOf(slices[1]);
        double mean = Double.valueOf(slices[2]);
        return new ActivityPattern(activityType, stdDev, mean);
    }
}
