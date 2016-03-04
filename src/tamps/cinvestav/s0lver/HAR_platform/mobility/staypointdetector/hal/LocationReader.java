package tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.hal;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

public class LocationReader {
    private Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public LocationReader(Context context, LocationListener locationListener) {
        this.context = context;
        this.locationListener = locationListener;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startReadings() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public void stopReadings() {
        locationManager.removeUpdates(locationListener);
    }
}
