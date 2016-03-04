package tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.modules;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.classifiers.StayPointListener;
import tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.classifiers.ZhenAlgorithm;
import tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.entities.StayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.hal.LocationReader;
import tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.repository.StayPointRepository;
import tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.utils.Constants;

public class StayPointDetector implements LocationListener{
    private final LocationReader locationReader;
    private final GeoFencingDetector geoFencingDetector;
    private StayPointListener stayPointListener;
    private ZhenAlgorithm zhenAlgorithm;
    private Context context;
    private StayPointRepository stayPointRepository;

    public StayPointDetector(Context context, StayPointListener stayPointListener, long minTimeThreshold, double distanceTreshold) {
        this.stayPointListener = stayPointListener;
        this.context = context;
        this.zhenAlgorithm = new ZhenAlgorithm(minTimeThreshold, distanceTreshold);
        this.locationReader = new LocationReader(context, this);
        this.stayPointRepository = new StayPointRepository();
        this.geoFencingDetector = new GeoFencingDetector(stayPointRepository, Constants.MIN_DISTANCE_PARAMETER);
    }

    public void startStayPointTracking() {
        // Start all the process!
        locationReader.startReadings();
    }

    @Override
    public void onLocationChanged(Location location) {
        StayPoint stayPoint = zhenAlgorithm.processFix(location);
        if (stayPoint != null) {
            // 1. Write it to file

            // 2. Analyze if this is a known staypoint
            // yes? Update frequency of visits
            // no? add it to the repository
            stayPointRepository.addOrUpdate(stayPoint);
        }

        StayPoint nearestStayPoint = geoFencingDetector.obtainNearestStayPoint(location);
        if (nearestStayPoint != null) {
            // Start HAR module!!
            Log.i(this.getClass().getSimpleName(), "User inside StayPoint " + nearestStayPoint);
            Log.i(this.getClass().getSimpleName(), "Starting HAR module");
            // TODO define a mechanism for defining how many times to invoke and how to stop the HAR system
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Not used by the moment
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Not used
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Not used
    }
}
