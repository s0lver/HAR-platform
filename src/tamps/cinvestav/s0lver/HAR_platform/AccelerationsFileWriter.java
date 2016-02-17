package tamps.cinvestav.s0lver.HAR_platform;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AccelerationsFileWriter {
    private String filepath;
    private ArrayList<AccelerometerReading> data;

    public AccelerationsFileWriter(Date date, ArrayList<AccelerometerReading> data) {
        String DATE_FORMAT = "dd-MM-yyyy_HH-mm-ss";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        this.filepath = Environment.getExternalStorageDirectory() + File.separator
                + "har-system" + File.separator + "records_" + sdf.format(date) + ".csv";
        this.data = data;
    }

    public void writeFile(){
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(filepath));
            for (AccelerometerReading reading : data) {
                pw.println(reading);
            }
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(this.getClass().getSimpleName(), "I could not write the acceleration readings file");
        }
    }
}
