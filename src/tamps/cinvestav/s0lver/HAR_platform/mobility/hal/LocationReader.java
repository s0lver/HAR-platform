package tamps.cinvestav.s0lver.HAR_platform.mobility.hal;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

/***
 * Connects to the GPS hardware and deliver readings
 */
public class LocationReader {
    private LocationManager locationManager;
    private LocationListener locationListener;

    /***
     * Basic constructor
     * @param context Context for accessing to the Location System Service
     * @param locationListener    The listener to which notification of locations will be delivered
     */
    public LocationReader(Context context, LocationListener locationListener) {
        this.locationListener = locationListener;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /***
     * Triggers the collection of location data
     */
    public void startReadings() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    /***
     * Stops the collection of location data
     */
    public void stopReadings() {
        locationManager.removeUpdates(locationListener);
    }
}
