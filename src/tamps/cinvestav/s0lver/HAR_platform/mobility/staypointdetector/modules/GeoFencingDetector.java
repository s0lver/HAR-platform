package tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.modules;

import android.location.Location;
import tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.entities.StayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.repository.StayPointRepository;

import java.util.ArrayList;

public class GeoFencingDetector {
    private final StayPointRepository stayPointRepository;
    private final double minDistancePArameter;

    public GeoFencingDetector(StayPointRepository stayPointRepository, double minDistanceParameter) {
        this.stayPointRepository = stayPointRepository;
        this.minDistancePArameter = minDistanceParameter;
    }

    /***
     * Returns the nearest stay point to the given location.
     * Such stay point is within the range of the minDistance parameter of the Staypoint detection algorithm.
     * @param location The location to test against all learned staypoints
     * @return The closest staypoint on which the location is embedded, null if the location is not inside any staypoint.
     * @see StayPoint
     */
    public StayPoint obtainNearestStayPoint(Location location) {
        ArrayList<StayPoint> stayPoints = stayPointRepository.getAllStayPoints();
        double closestDistance = 0;
        StayPoint closestStayPoint = null;

        for (StayPoint stayPoint : stayPoints) {
            Location spAsLocation = convertStayPointToLocation(stayPoint);
            float distance = location.distanceTo(spAsLocation);

            if (distance < closestDistance) {
                closestDistance = distance;
                closestStayPoint = stayPoint;
            }
        }

        // Only return the nearest StayPoint if it is within the MIN_DISTANCE_PARAMETER
        // Actually, this could be replaced by a call to a listener of "userInsideStayPointListener", but at the moment
        // it is so simple that it is omitted.
        if (closestDistance < minDistancePArameter) return closestStayPoint;
        else
            return null;
    }

    /***
     * Converts a StayPoint to a Location object.
     * @param stayPoint The StayPoint to translate.
     * @return A Location instance geographically equivalent to the StayPoint
     * @see StayPoint
     */
    private Location convertStayPointToLocation(StayPoint stayPoint) {
        Location location = new Location("Custom");
        location.setLatitude(stayPoint.getLatitude());
        location.setLongitude(stayPoint.getLongitude());
        location.setTime(stayPoint.getArrivalTime().getTime());

        return location;
    }
}
