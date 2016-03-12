package tamps.cinvestav.s0lver.HAR_platform.mobility.entities;

import android.location.Location;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.utils.Constants;

import java.util.ArrayList;
import java.util.Date;

/***
 * Useful methods for analyzing locations (instances of the Location Android class)
 */
public class LocationAnalyzer {
    /***
     * Evaluates if a given location is valid, i.e., if it was obtained from the Gps receiver
     * or if it was recreated by the platform when a TimeOut was declared
     * @param location The Location to analyze
     * @return true if the location is valid, false otherwise
     * @see Location
     */
    public static boolean isValidLocation(Location location) {
        if ((location.getLatitude() == 0) && (location.getLongitude() == 0)) return false;
        return true;
    }

    /***
     * Converts the given Location into a String representation.
     * The representation follows:
     * isValid, latitude, longitude, altitude, accuracy, speed, timestamp
     * @param location The location to translate
     * @return The String csv representation of the location
     */
    public static String convertLocationToCsv(Location location) {
        StringBuilder sb = new StringBuilder();
        Date date = new Date(location.getTime());

        sb.append(isValidLocation(location) ? "1" : "0")
                .append(location.getLatitude())
                .append(",")
                .append(location.getLongitude())
                .append(",")
                .append(location.getAltitude())
                .append(",")
                .append(location.getAccuracy())
                .append(",")
                .append(location.getSpeed())
                .append(Constants.FILE_NAMES_SIMPLE_DATE_FORMAT.format(date));

        return sb.toString();
    }

    /***
     * Finds the closest DbStayPoint to the given Location and within the minimumDistanceThreshold.
     * @param location The location to test against all learned StayPoints
     * @param dbStayPoints The list of stayPoints on which the nearest StayPoint should be found
     * @param minimumDistanceThreshold The ratio to find the closest StayPoint
     * @return The closest StayPoint to the given Location, null if no StayPoint within the minDistanceParameter ratio is found.
     * @see Location
     */
    public static DbStayPoint findClosestStayPoint(Location location, ArrayList<DbStayPoint> dbStayPoints, double minimumDistanceThreshold) {
        DbStayPoint closestStayPoint = null;
        double closestDistance = 0;
        for (DbStayPoint dbStayPoint : dbStayPoints) {
            Location spAsLocation = dbStayPoint.getStayPoint().convertStayPointToLocation();
            float distance = location.distanceTo(spAsLocation);

            if (distance <= closestDistance) {
                closestDistance = distance;
                closestStayPoint = dbStayPoint;
            }
        }

        if (minimumDistanceThreshold < closestDistance) {
            closestStayPoint = null;
        }

        return closestStayPoint;
    }
}
