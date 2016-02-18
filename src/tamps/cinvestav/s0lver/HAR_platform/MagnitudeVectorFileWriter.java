package tamps.cinvestav.s0lver.HAR_platform;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MagnitudeVectorFileWriter {
    private File vectorFile;
    private double stdDev, mean;
    private double[] magnitudeVector;
    private int currentRun;

    public MagnitudeVectorFileWriter(int currentRun, String filepath, double stdDev, double mean, double[] magnitudeVector) {
        this.vectorFile = new File(filepath);
        this.currentRun = currentRun;
        this.stdDev = stdDev;
        this.mean = mean;
        this.magnitudeVector = magnitudeVector;
    }

    public void writeFile() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(vectorFile, true));
            pw.println("# Run: " + currentRun + ", Mean: " + mean + ", StdDev: " + stdDev);
            for (double v : magnitudeVector) {
                pw.println(currentRun + ", " + v);
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(this.getClass().getSimpleName(), "I could not write the magnitude vector file");
        }

    }
}
