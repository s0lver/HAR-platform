package tamps.cinvestav.s0lver.HAR_platform.mobility.modules;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.mobility.classifiers.MobilityListener;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.LocationAnalyzer;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.StayPointRepository;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.StayPointVisitsRepository;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPointVisit;

import java.util.ArrayList;
import java.util.Date;

/***
 * Reacts to each location update, detecting when the user is arriving or leaving a previously learned StayPoint and
 * controlling the HAR module accordingly.
 * @see HarController
 * @see StayPointRepository
 * @see StayPointVisitsRepository
 */
public class GeoFencing {
    private final StayPointRepository stayPointRepository;
    private final StayPointVisitsRepository visitRepository;
    private DbStayPoint currentStayPoint;
    private DbStayPoint previousStayPoint;
    private DbStayPointVisit currentVisit;
    private HarController harController;
    private final double radioDistance;
    private boolean isUserAtStayPoint, wasUserAtStayPoint;

    /***
     * Constructor
     *
     * @param context                     The context for creating and accessing to the StayPoint repository
     * @param radioDistance    The minimum distance parameter for geo-fencing analysis
     * @param harWindowsPerIntervention   The amount of accelerometer data windows to classify in each intervention. (HAR system is triggered when user enters into a StayPoint)
     * @param readingsPeriodRate          The period or interval between each intervention for reading and classifying accelerometer data.
     * @see MobilityListener
     */
    public GeoFencing(Context context, double radioDistance, int harWindowsPerIntervention, long readingsPeriodRate) {
        this.stayPointRepository = new StayPointRepository(context, radioDistance);
        this.visitRepository = new StayPointVisitsRepository(context);
        this.harController = new HarController(context, this, harWindowsPerIntervention, readingsPeriodRate);
        this.radioDistance = radioDistance;
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
        DbStayPoint closestStayPoint = LocationAnalyzer.findClosestStayPoint(location, stayPoints, radioDistance);

        wasUserAtStayPoint = isUserAtStayPoint;
        previousStayPoint = currentStayPoint;
        currentStayPoint = closestStayPoint;

        if (currentStayPoint != null) {
            isUserAtStayPoint = true;
            if (isUserArrivingStayPoint()) {
                Log.i(this.getClass().getSimpleName(), "Identified, user arriving @ StayPoint " + currentStayPoint);
                // 1. Add the visit information for this StayPoint
                DbStayPointVisit visit = buildVisit(location);
                currentVisit = visitRepository.add(visit);
                // 2. Update the visit information of this StayPoint
                stayPointRepository.update(currentStayPoint);
                // 3. Notify that the user is arriving, so that HAR can be triggered...
                harController.onUserArrivingStayPoint(currentStayPoint, new Date(location.getTime()));
            }
        } else {
            isUserAtStayPoint = false;
            if (isUserLeavingStayPoint()) {
                Log.i(this.getClass().getSimpleName(), "Identified, user leaving the StayPoint " + previousStayPoint);
                // 1. Update the visit information for this StayPoint
                currentVisit.setDepartureTime(new Date(location.getTime()));
                visitRepository.updateVisitInformation(currentVisit);
                // 2. Notifies that the user is leaving, so that HAR can be stopped...
                harController.onUserLeavingStayPoint(previousStayPoint, currentVisit.getDepartureTime());
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
        return isUserAppearingInANewStayPoint();
    }

    /***
     * Evaluates if user is showing up in a StayPoint after being in a StayPoint.
     * This would happen if GPS fixes are collected quite sparse in such way that the transition to other StayPoints
     * is not detected.
     * @return true if the user is <italic>suddenly</italic> showing up in a new StayPoint
     */
    private boolean isUserAppearingInANewStayPoint() {
        return wasUserAtStayPoint && isUserAtStayPoint && !currentStayPoint.getStayPoint().equals(previousStayPoint.getStayPoint());
    }

    /***
     * Determines whether the user is leaving a StayPoint.
     * This operation implies that the user was at a StayPoint before but not at the moment
     * @return true is the user is leaving a StayPoint, false otherwise
     */
    private boolean isUserLeavingStayPoint() {
        return wasUserAtStayPoint && !isUserAtStayPoint;
    }

    /***
     * Obtains the current StayPoint that user is visiting
     * @return The StayPoint where the user currently is.
     */
    public DbStayPointVisit getCurrentVisit() {
        return currentVisit;
    }

    /***
     * Stops the collection + analysis of accelerometer data
     * @see HarController
     */
    public void stopGeoFencing() {
        harController.stopHarSystem();
        harController = null;
    }
}
