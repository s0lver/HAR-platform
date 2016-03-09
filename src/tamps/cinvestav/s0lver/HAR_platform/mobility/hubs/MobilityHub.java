package tamps.cinvestav.s0lver.HAR_platform.mobility.hubs;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.har.classifiers.NaiveBayesListener;
import tamps.cinvestav.s0lver.HAR_platform.har.hubs.AccelerometerHub;
import tamps.cinvestav.s0lver.HAR_platform.mobility.classifiers.MobilityListener;
import tamps.cinvestav.s0lver.HAR_platform.mobility.classifiers.StayPointListener;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.hal.LocationReader;
import tamps.cinvestav.s0lver.HAR_platform.mobility.modules.GeoFencing;
import tamps.cinvestav.s0lver.HAR_platform.mobility.modules.LocationLogger;
import tamps.cinvestav.s0lver.HAR_platform.mobility.modules.StayPointDetector;

import java.util.Date;

public class MobilityHub {
    private LocationReader locationReader;
    private StayPointDetector stayPointDetector;
    private GeoFencing geoFencing;
    private AccelerometerHub accelerometerHub;

    private LocationLogger locationLogger;

    public MobilityHub(Context context, long minimumTimeThreshold, double minimumDistanceParameter) {
        this.locationReader = new LocationReader(context, new LocalLocationListener());
        this.stayPointDetector = new StayPointDetector(context, minimumTimeThreshold, minimumDistanceParameter);
        this.geoFencing = new GeoFencing(context, minimumDistanceParameter, buildMobilityListener());
        this.locationLogger = new LocationLogger(context);
        this.accelerometerHub = new AccelerometerHub(context);
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

    private MobilityListener buildMobilityListener() {
        return new MobilityListener() {
            @Override
            public void onUserArrivingStayPoint(StayPoint stayPoint, Date timeOfArrivalDetected) {
                Log.i(this.getClass().getSimpleName(), "User is arriving at a StayPoint");
                // TODO define a mechanism for defining how many times to invoke and how to stop the HAR system
                Log.i(this.getClass().getSimpleName(), "Starting HAR module");
                accelerometerHub.startClassification(buildNaiveBayesListener());
            }

            @Override
            public void onUserLeavingStayPoint(Date timeOfDeparture) {
                Log.i(this.getClass().getSimpleName(), "User is leaving a StayPoint");
                Log.i(this.getClass().getSimpleName(), "Stopping HAR module");
            }
        };
    }

    private NaiveBayesListener buildNaiveBayesListener() {
        return new NaiveBayesListener() {
            @Override
            public void onClassifiedPattern(byte predictedActivityType) {
                // TODO work from here, launch the counting-voting of the activity and work for saving such information on db
            }
        };
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
