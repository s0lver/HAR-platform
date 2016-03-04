package tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.repository;

import tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.entities.StayPoint;

import java.util.ArrayList;

public class StayPointRepository {
    public boolean contains(StayPoint stayPoint) {
        throw new RuntimeException("Not implemented yet");
    }

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
