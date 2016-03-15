package tamps.cinvestav.s0lver.HAR_platform.mobility.classifiers;

import android.location.Location;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;

import java.util.Date;

/***
 * An implementation of the Zhen <b>Live</b> Algorithm for detecting staypoints.
 * This implementation does not use a buffer for queuing points, instead it only accumulates
 * the location (latitude, longitude) information for calculating the centroid.
 */
public class ZhenAlgorithm {
    private Location pi, pj, pjMinus;
    private int amountFixes;
    private double sigmaLatitude;
    private double sigmaLongitude;
    private Date arrivalTime, departureTime;
    private double distanceThreshold;
    private long minTimeThreshold;


    /***
     * Constructor of the Zhen <b>Live</b> Sigma algorithm
     * @param minTimeThreshold The minimum time for detecting a StayPoint. In milliseconds
     * @param distanceThreshold The distance for detecting a StayPoint (minimum size of the geographical zone). In meters
     * @see StayPoint
     */
    public ZhenAlgorithm(long minTimeThreshold, double distanceThreshold) {
        this.minTimeThreshold = minTimeThreshold;
        this.distanceThreshold = distanceThreshold;

        this.amountFixes = 0;
        this.sigmaLatitude = 0;
        this.sigmaLongitude = 0;
    }

    /***
     * Processes the specified Location fix using the Zhen algorithm
     * @param fix The Location to be processed
     * @return The StayPoint if found, otherwise a null reference is returned
     * @see StayPoint
     * @see Location
     */
    public StayPoint processFix(Location fix) {
        includeFix(fix);
        if (amountFixes == 1) {
            pi = fix;
            arrivalTime = new Date(fix.getTime());
            return null;
        }
        else if (amountFixes == 2) {
            pjMinus = pi;
            pj = fix;
        }
        else {
            pjMinus = pj;
            pj = fix;
        }
        return processLive();
    }

    /***
     * Includes the specified Location fix, accumulating its information
     * @param fix    The Location to include
     * @see Location
     */
    private void includeFix(Location fix) {
        amountFixes++;
        departureTime = new Date(fix.getTime());
        sigmaLatitude += fix.getLatitude();
        sigmaLongitude += fix.getLongitude();
    }

    /***
     * Executes the core of the algorithm. If a stay point is found, it is returned
     * @return The staypoint found, or a null reference
     * @see StayPoint
     */
    private StayPoint processLive() {
        double distance = pi.distanceTo(pj);

        if (distance > distanceThreshold) {
            long timespan = timeDifference(pi, pj);

            if (timespan > minTimeThreshold) {
                StayPoint sp = StayPoint.createStayPoint(sigmaLatitude, sigmaLongitude, arrivalTime, departureTime, amountFixes);
                resetAccumulated();
                return sp;
            }
            resetAccumulated();
            return null;
        }
        return null;
    }

    /***
     * Process the last part of fixes when the tracking of user is not going to be performed anymore
     * @return The staypoint, if found
     * @see StayPoint
     */
    public StayPoint processLastPart() {
        if (amountFixes >= 1){
            return StayPoint.createStayPoint(sigmaLatitude, sigmaLongitude, arrivalTime, departureTime, amountFixes);
        }
        return null;
    }

    /***
     * Calculates the difference between two location objects
     * @param firstPoint The first (chronological) location
     * @param lastPoint The second (chronological) location
     * @return The time difference in milliseconds
     * @see Location
     */
    private long timeDifference(Location firstPoint, Location lastPoint) {
        if (firstPoint == null || lastPoint == null) {
            throw new RuntimeException("Can't calculate time difference on null locations");
        }
        return lastPoint.getTime() - firstPoint.getTime();
    }


    /***
     * Cleans-reset the information accumulated by the algorithm.
     * The current pj is the new pi
     */
    private void resetAccumulated() {
        amountFixes = 1;
        sigmaLatitude = pj.getLatitude();
        sigmaLongitude = pj.getLongitude();
        arrivalTime = new Date(pj.getTime());
        departureTime = new Date(pj.getTime());

        pi = pj;
        pj = null;
        pjMinus = null;
    }

}