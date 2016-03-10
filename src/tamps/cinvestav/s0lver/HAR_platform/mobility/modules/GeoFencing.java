package tamps.cinvestav.s0lver.HAR_platform.mobility.modules;

import android.content.Context;
import android.location.Location;
import tamps.cinvestav.s0lver.HAR_platform.mobility.classifiers.MobilityListener;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.LocationAnalyzer;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.StayPointRepository;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.ActivitiesInStayPointDal;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.StayPointVisitsRepository;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbActivityInStayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPointVisit;

import java.util.ArrayList;
import java.util.Date;

public class GeoFencing {
    private final StayPointRepository stayPointRepository;
    private final StayPointVisitsRepository visitRepository;
    private final ActivitiesInStayPointDal activitiesDal;
    private DbStayPoint currentStayPoint;
    private DbStayPoint previousStayPoint;
    private MobilityListener mobilityListener;
    private final double minDistanceParameter;

    private boolean isUserAtStayPoint, wasUserAtStayPoint;
    private DbStayPointVisit currentVisit;

    /***
     * Constructor
     *
     * @param context                     The context for creating and accessing to the StayPoint repository
     * @param minimumDistanceParameter    The minimum distance parameter for geo-fencing analysis
     * @param mobilityListener            The MobilityListener for reporting mobility changes to.
     * @see MobilityListener
     */
    public GeoFencing(Context context, double minimumDistanceParameter, MobilityListener mobilityListener) {
        this.stayPointRepository = new StayPointRepository(context, minimumDistanceParameter);
        this.visitRepository = new StayPointVisitsRepository(context);
        this.activitiesDal = new ActivitiesInStayPointDal(context);
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
        ArrayList<DbStayPoint> stayPoints = stayPointRepository.getAllStayPoints();
        DbStayPoint closestStayPoint = LocationAnalyzer.findClosestStayPoint(location, stayPoints, minDistanceParameter);

        wasUserAtStayPoint = isUserAtStayPoint;
        previousStayPoint = currentStayPoint;
        currentStayPoint = closestStayPoint;

        if (currentStayPoint != null) {
            isUserAtStayPoint = true;
            if (isUserArrivingStayPoint()) {
                // 1. Add the visit information for this StayPoint
                DbStayPointVisit visit = buildVisit(location);
                currentVisit = visitRepository.add(visit);

                // 2. Update the visit information of this StayPoint
                stayPointRepository.update(currentStayPoint);

                // 3. Notify that the user is arriving, so that HAR can be triggered
                mobilityListener.onUserArrivingStayPoint(currentStayPoint, visit.getArrivalTime());
            }
        } else {
            isUserAtStayPoint = false;
            if (isUserLeavingStayPoint()) {
                // 1. Update the visit information for this StayPoint
                visitRepository.updateVisitInformation(currentVisit);

                // 2. Notifies that the user is leaving, so that HAR can be stopped
                mobilityListener.onUserLeavingStayPoint(currentVisit.getDepartureTime());
            }
        }
    }

    /***
     * Builds an initial visit record with default parameters
     * @param location The location to extract arrivalTime
     * @return A DbStayPointVisit object with thee initial information of the visit.
     * @see DbStayPointVisit
     */
    private DbStayPointVisit buildVisit(Location location) {
        return new DbStayPointVisit(0, currentStayPoint.getId(), new Date(location.getTime()),
                new Date(location.getTime()), 0, 0, 0);
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
