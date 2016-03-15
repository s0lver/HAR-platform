package tamps.cinvestav.s0lver.HAR_platform.har.io;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class MagnitudeVectorFileWriter {
    private final File vectorFile;
    private final double[] magnitudeVector;
    private final int currentRun;

    public MagnitudeVectorFileWriter(int currentRun, String filepath, double[] magnitudeVector) {
        this.vectorFile = new File(filepath);
        this.currentRun = currentRun;
        this.magnitudeVector = magnitudeVector;
    }

    public void writeFile() {
        PrintWriter pw;
        try {
            pw = new PrintWriter(new FileWriter(vectorFile, true));
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
