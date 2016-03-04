package tamps.cinvestav.s0lver.HAR_platform.mobility.modules;

import android.location.Location;
import tamps.cinvestav.s0lver.HAR_platform.mobility.classifiers.MobilityListener;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.LocationAnalyzer;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.StayPointRepository;

import java.util.ArrayList;
import java.util.Date;

public class GeoFencing {
    private final StayPointRepository stayPointRepository;
    private StayPoint currentStayPoint;
    private StayPoint previousStayPoint;
    private MobilityListener mobilityListener;
    private final double minDistanceParameter;

    private boolean isUserAtStayPoint, wasUserAtStayPoint;

    /***
     * Constructor
     * @param minimumDistanceParameter    The minimum distance parameter for geo-fencing analysis
     * @param mobilityListener            The MobilityListener for reporting mobility changes to.
     * @see MobilityListener
     */
    public GeoFencing(double minimumDistanceParameter, MobilityListener mobilityListener) {
        this.stayPointRepository = new StayPointRepository(minimumDistanceParameter);
        this.minDistanceParameter = minimumDistanceParameter;
        this.mobilityListener = mobilityListener;
        this.isUserAtStayPoint = false;
        this.wasUserAtStayPoint = false;
    }

    /***
     * Evaluates the given location for detecting whether the user is inside a StayPoint or traversing
     * within them.
     * @param location The Location for evaluating mobility
     * @see StayPoint
     */
    public void evaluateMobility(Location location) {
        ArrayList<StayPoint> stayPoints = stayPointRepository.getAllStayPoints();
        StayPoint closestStayPoint = LocationAnalyzer.findClosestStayPoint(location, stayPoints, minDistanceParameter);

        wasUserAtStayPoint = isUserAtStayPoint;
        previousStayPoint = currentStayPoint;
        currentStayPoint = closestStayPoint;

        if (currentStayPoint != null) {
            isUserAtStayPoint = true;
            if (isUserArrivingStayPoint()) {
                mobilityListener.onUserArrivingStayPoint(currentStayPoint, new Date(location.getTime()));
            }
        } else {
            isUserAtStayPoint = false;
            if (isUserLeavingStayPoint()) {
                mobilityListener.onUserLeavingStayPoint(new Date(location.getTime()));
            }
        }
    }

    /***
     * Determines whether the user is arriving a StayPoint (using the internal currentStayPoint object).
     * This operation implies that the user was not at a StayPoint before, or that the user was in a
     * <b>different</b> StayPoint before.
     * @return true if the user is arriving at a StayPoint, false otherwise
     */
    private boolean isUserArrivingStayPoint() {
        if (!wasUserAtStayPoint && isUserAtStayPoint) return true;
        if (isUserAppearingInANewStayPoint()) return true;
        return false;
    }

    /***
     * Evaluates if user is showing up in a StayPoint after being in a StayPoint.
     * This would happen if GPS fixes are collected quite sparse in such way that the transition to other StayPoints
     * is not detected.
     * @return true if the user is <italic>suddenly</italic> showing up in a new StayPoint
     */
    private boolean isUserAppearingInANewStayPoint() {
        return wasUserAtStayPoint && isUserAtStayPoint && currentStayPoint != previousStayPoint;
    }

    /***
     * Determines whether the user is leaving a StayPoint.
     * This operation implies that the user was at a StayPoint before but not at the moment
     * @return true is the user is leaving a StayPoint, false otherwise
     */
    private boolean isUserLeavingStayPoint() {
        return wasUserAtStayPoint && !isUserAtStayPoint;
    }
}
