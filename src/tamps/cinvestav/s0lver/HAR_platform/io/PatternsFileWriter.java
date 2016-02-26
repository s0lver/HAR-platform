package tamps.cinvestav.s0lver.HAR_platform.io;


import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.activities.ActivityPattern;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PatternsFileWriter {
    private File patternsFile;
    private ActivityPattern pattern;

    public PatternsFileWriter(String filepath, ActivityPattern pattern) {
        this.patternsFile = new File(filepath);
        this.pattern = pattern;
    }

    public void writeFile() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(patternsFile, true));
            pw.println(pattern.getType() + "," + pattern.getStandardDeviation() + "," + pattern.getMean());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(this.getClass().getSimpleName(), "I could not write the patterns file");
        }
    }
}