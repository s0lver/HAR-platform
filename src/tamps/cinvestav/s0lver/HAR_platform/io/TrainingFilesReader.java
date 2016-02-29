package tamps.cinvestav.s0lver.HAR_platform.io;

import android.content.Context;
import android.os.Environment;
import tamps.cinvestav.s0lver.HAR_platform.activities.Activities;
import tamps.cinvestav.s0lver.HAR_platform.activities.ActivityPattern;

import java.io.*;
import java.util.ArrayList;

public class TrainingFilesReader {
    public static ArrayList<ActivityPattern> readStaticFile(Context context) throws IOException {
        return readFile("training-static.csv", Activities.STATIC);
    }

    public static ArrayList<ActivityPattern> readWalkingFile(Context context) throws IOException {
        return readFile("training-walking.csv", Activities.WALKING);
    }

    public static ArrayList<ActivityPattern> readRunningFile(Context context) throws IOException {
        return readFile("training-running.csv", Activities.RUNNING);
    }

    private static ArrayList<ActivityPattern> readFile(String filename, byte type) throws IOException {
        ArrayList<ActivityPattern> trainingPatterns = new ArrayList<>();
        String filePath = Environment.getExternalStorageDirectory() + File.separator
                + "har-system" + File.separator + filename;
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
