package tamps.cinvestav.s0lver.HAR_platform.mobility.modules;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.battery.BatteryStatus;
import tamps.cinvestav.s0lver.HAR_platform.battery.BatteryStatusVerifier;
import tamps.cinvestav.s0lver.HAR_platform.mobility.hal.LocationReader;
import tamps.cinvestav.s0lver.HAR_platform.mobility.io.GpsLocationsFileWriter;
import tamps.cinvestav.s0lver.HAR_platform.mobility.utils.Constants;

import java.io.File;
import java.util.Date;

public class LocationLogger implements LocationListener{
    private final LocationReader locationReader;
    private final Date startTime;
    private final BatteryStatusVerifier batteryStatusVerifier;

    public LocationLogger(Context context) {
        this.locationReader = new LocationReader(context, this);
        this.startTime = new Date(System.currentTimeMillis());
        this.batteryStatusVerifier = new BatteryStatusVerifier(context);
    }

    public void startLocationsLogging() {
        Log.i(this.getClass().getSimpleName(), "Starting the collection of location updates");
        locationReader.startReadings();
    }

    public void stopLocationsLogging() {
        Log.i(this.getClass().getSimpleName(), "Stopping the collection of location updates");
        locationReader.stopReadings();
    }

    private void writeLocationAndBatteryInfo(Location location, BatteryStatus batteryStatus) {
        String filePath = Environment.getExternalStorageDirectory() + File.separator
                + "har-system" + File.separator + "Gps_records_" +
                Constants.FILE_NAMES_SIMPLE_DATE_FORMAT.format(startTime) + ".csv";
        new GpsLocationsFileWriter(filePath, location, batteryStatus).writeFile();
    }

    @Override
    public void onLocationChanged(Location location) {
        BatteryStatus batteryStatus = batteryStatusVerifier.getBatteryStatus();

        writeLocationAndBatteryInfo(location, batteryStatus);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Not used at the moment
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Not used by the moment
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Not used by the moment
    }
}
