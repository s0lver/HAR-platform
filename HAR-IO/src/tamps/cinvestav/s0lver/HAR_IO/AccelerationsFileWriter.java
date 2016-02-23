package tamps.cinvestav.s0lver.HAR_IO;

import android.util.Log;
import tamps.cinvestav.s0lver.HAR_Entities.entities.AccelerometerReading;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class AccelerationsFileWriter {
    private File accelerationsFile;
    private ArrayList<AccelerometerReading> data;
    private int currentRun;

    public AccelerationsFileWriter(int currentRun, String filepath, ArrayList<AccelerometerReading> data) {
        this.data = data;
        this.currentRun = currentRun;
        this.accelerationsFile = new File(filepath);
    }

    public void writeFile(){
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(accelerationsFile, true));
            for (AccelerometerReading reading : data) {
                pw.println(currentRun + "," + reading);
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(this.getClass().getSimpleName(), "I could not write the acceleration readings file");
        }
    }
}
