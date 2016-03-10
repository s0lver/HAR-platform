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

public class MobilityHub {
    private LocationReader locationReader;
    private StayPointDetector stayPointDetector;
    private GeoFencing geoFencing;
    private LocationLogger locationLogger;

    public MobilityHub(Context context, long minimumTimeThreshold, double minimumDistanceParameter) {
        this.locationReader = new LocationReader(context, new LocalLocationListener());
        this.stayPointDetector = new StayPointDetector(context, minimumTimeThreshold, minimumDistanceParameter);
        this.geoFencing = new GeoFencing(context, minimumDistanceParameter);
        this.locationLogger = new LocationLogger(context);
    }

    public void startLocationsLogging() {
        locationLogger.startLocationsLogging();
    }

    public void stopLocationsLogging() {
        locationLogger.stopLocationsLogging();
    }

    public void startMobilityTracking() {
        locationReader.startReadings();
    }

    public void stopMobilityTracking() {
        locationReader.stopReadings();
        stayPointDetector.analyzeLastPart();
    }

    private StayPointListener buildStayPointListener() {
        return new StayPointListener() {
            @Override
            public void onStayPointDetected(StayPoint stayPoint) {

            }
        };
    }

    private class LocalLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            stayPointDetector.analyzeLocation(location);
            geoFencing.evaluateMobility(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // Not used at the moment
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Not used at the moment
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Not used at the moment
        }
    }

}
