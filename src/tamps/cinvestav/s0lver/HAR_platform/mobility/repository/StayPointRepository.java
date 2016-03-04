package tamps.cinvestav.s0lver.HAR_platform.mobility.repository;

import android.location.Location;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.LocationAnalyzer;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;

import java.util.ArrayList;

public class StayPointRepository {
    private double minimumDistanceThreshold;

    public StayPointRepository(double minimumDistanceThreshold) {
        this.minimumDistanceThreshold = minimumDistanceThreshold;
    }

    public boolean contains(StayPoint stayPoint) {
        ArrayList<StayPoint> stayPoints = getAllStayPoints();
        Location stayPointAsLocation = stayPoint.convertStayPointToLocation();

        StayPoint closestStayPoint = LocationAnalyzer.findClosestStayPoint(stayPointAsLocation, stayPoints, minimumDistanceThreshold);
        return closestStayPoint != null;
    }

    /***
     * This is our "onStayPointDetected(StayPoint stayPoint)"
     * @param newStayPoint
     * @return
     */
    public boolean addOrUpdate(StayPoint newStayPoint) {
        // If exists, then update metadata and return false, otherwhise try it and return true
        throw new RuntimeException("Not implemented yet");
    }

    private void updateMetadata(StayPoint stayPoint) {
        throw new RuntimeException("Not implemented yet");
    }

    /***
     * Obtains the full list of stay points learned by the platform
     * @return An ArrayList of the stay points learned by the platform
     * @see StayPoint
     */
    public ArrayList<StayPoint> getAllStayPoints() {
        throw new RuntimeException("Not implemented yet");
    }
}
