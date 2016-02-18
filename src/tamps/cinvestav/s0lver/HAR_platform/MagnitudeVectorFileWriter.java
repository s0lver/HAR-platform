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
    private String filepath;
    private double stdDev, mean;
    private double[] magnitudeVector;

    public MagnitudeVectorFileWriter(String filePrefix, Date date, double stdDev, double mean, double[] magnitudeVector) {
        String DATE_FORMAT = "dd-MM-yyyy_HH-mm-ss";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        this.filepath = Environment.getExternalStorageDirectory() + File.separator
                + "har-system" + File.separator + filePrefix + "magnitudevector_" + sdf.format(date) + ".csv";
        this.stdDev = stdDev;
        this.mean = mean;
        this.magnitudeVector = magnitudeVector;
    }

    public void writeFile() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(filepath));
            pw.println("# Mean: " + mean + ", StdDev: " + stdDev);
            for (double v : magnitudeVector) {
                pw.println(v);
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(this.getClass().getSimpleName(), "I could not write the magnitude vector file");
        }

    }
}
