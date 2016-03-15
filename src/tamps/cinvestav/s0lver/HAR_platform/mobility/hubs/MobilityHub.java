package tamps.cinvestav.s0lver.HAR_platform.mobility.hubs;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import tamps.cinvestav.s0lver.HAR_platform.mobility.classifiers.StayPointListener;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.hal.LocationReader;
import tamps.cinvestav.s0lver.HAR_platform.mobility.modules.GeoFencing;
import tamps.cinvestav.s0lver.HAR_platform.mobility.modules.LocationLogger;
import tamps.cinvestav.s0lver.HAR_platform.mobility.modules.StayPointDetector;

/***
 * Controls the tracking of user, both in terms of location and activity
 */
public class MobilityHub implements LocationListener{
    private LocationReader locationReader;
    private StayPointDetector stayPointDetector;
    private GeoFencing geoFencing;
    private LocationLogger locationLogger;

    /***
     * Constructor
     * @param context The context for accessing to Sensors and Location system services
     * @param minimumTimeThreshold         The minimum time threshold to be employed for detecting StayPoints
     * @param minimumDistanceParameter     The minimum distance parameter to be employed for detecting StayPoints. The half of this parameter is employed as the radio distance for detecting re-entrance to a StayPoint
     * @param harWindowsPerIntervention    The amount of sampling windows to be considered in each intervention for identifying user activity
     * @param readingsPeriodRate           The period at which user activity identification is to be triggered. Should be smaller than 5 secs * harWindowsPerIntervention
     */
    public MobilityHub(Context context, long minimumTimeThreshold, double minimumDistanceParameter, int harWindowsPerIntervention, long readingsPeriodRate) {
        this.locationReader = new LocationReader(context, this);
        this.stayPointDetector = new StayPointDetector(context, minimumTimeThreshold, minimumDistanceParameter);
        double radioDistanceForGeoFencing = minimumDistanceParameter / 2;
        this.geoFencing = new GeoFencing(context, radioDistanceForGeoFencing, harWindowsPerIntervention, readingsPeriodRate);
        this.locationLogger = new LocationLogger(context);
    }

    /***
     * Starts only the collection of GPS fixes and stores them in a file
     */
    public void startLocationsLogging() {
        locationLogger.startLocationsLogging();
    }

    /***
     * Stops the collection of GPS fixes
     */
    public void stopLocationsLogging() {
        locationLogger.stopLocationsLogging();
    }

    /***
     * Starts the tracking of user's mobility, in terms of StayPoint calculations and Geo-Fencing + HAR interaction
     */
    public void startMobilityTracking() {
        locationReader.startReadings();
    }

    /***
     * Stops the tracking of user's mobility, disconnecting from GPS and accelerometer.
     * Callable when exiting application
     */
    public void stopMobilityTracking() {
        locationReader.stopReadings();
        stayPointDetector.analyzeLastPart();
        geoFencing.stopGeoFencing();
    }

    @Override
    public void onLocationChanged(Location location) {
        stayPointDetector.analyzeLocation(location);
        geoFencing.evaluateMobility(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
