package tamps.cinvestav.s0lver.HAR_platform.mobility.modules;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.har.classifiers.NaiveBayesListener;
import tamps.cinvestav.s0lver.HAR_platform.har.hubs.AccelerometerHub;
import tamps.cinvestav.s0lver.HAR_platform.mobility.classifiers.MobilityListener;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.LocationAnalyzer;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.StayPointRepository;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.ActivitiesInStayPointDal;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.StayPointVisitsRepository;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbActivityInStayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPointVisit;
import tamps.cinvestav.s0lver.HAR_platform.mobility.utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GeoFencing {
    private final StayPointRepository stayPointRepository;
    private final StayPointVisitsRepository visitRepository;
    private final ActivitiesInStayPointDal activitiesDal;
    private final AccelerometerHub accelerometerHub;
    private final MobilityListener localMobilityListener;

    private DbStayPoint currentStayPoint;
    private DbStayPoint previousStayPoint;

    private final double minDistanceParameter;
    private boolean isUserAtStayPoint, wasUserAtStayPoint;
    private DbStayPointVisit currentVisit;

    private int runningCount = 0;
    private final int MAX_RUNNING_COUNT = 5;
    private Timer harTimer;
    private TimerTask harTask;
    private NaiveBayesListener harClassificationListener;


    /***
     * Constructor
     *
     * @param context                     The context for creating and accessing to the StayPoint repository
     * @param minimumDistanceParameter    The minimum distance parameter for geo-fencing analysis
     * @see MobilityListener
     */
    public GeoFencing(Context context, double minimumDistanceParameter) {
        this.accelerometerHub = new AccelerometerHub(context);
        this.stayPointRepository = new StayPointRepository(context, minimumDistanceParameter);
        this.visitRepository = new StayPointVisitsRepository(context);
        this.activitiesDal = new ActivitiesInStayPointDal(context);
        this.localMobilityListener = buildMobilityListener();
        this.minDistanceParameter = minimumDistanceParameter;
        this.isUserAtStayPoint = false;
        this.wasUserAtStayPoint = false;
        this.harTask = buildHarTask();
        this.harClassificationListener = buildNaiveBayesListener();
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

                // 3. Notify that the user is arriving, so that HAR can be triggered...
                localMobilityListener.onUserArrivingStayPoint(currentStayPoint, new Date(location.getTime()));
            }
        } else {
            isUserAtStayPoint = false;
            if (isUserLeavingStayPoint()) {
                // 1. Update the visit information for this StayPoint
                currentVisit.setDepartureTime(new Date(location.getTime()));
                visitRepository.updateVisitInformation(currentVisit);

                // 2. Notifies that the user is leaving, so that HAR can be stopped...
                localMobilityListener.onUserLeavingStayPoint(previousStayPoint, currentVisit.getDepartureTime());
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

    private MobilityListener buildMobilityListener() {
        return new MobilityListener() {
            @Override
            public void onUserArrivingStayPoint(DbStayPoint stayPoint, Date timeOfArrival) {
                Log.i(this.getClass().getSimpleName(), "User is arriving at a StayPoint @ " + Constants.SIMPLE_DATE_FORMAT.format(timeOfArrival));
                Log.i(this.getClass().getSimpleName(), "StayPoint is " + stayPoint);
                Log.i(this.getClass().getSimpleName(), "Starting HAR module");
                launchHarSystem();
            }

            @Override
            public void onUserLeavingStayPoint(DbStayPoint stayPoint, Date timeOfDeparture) {
                Log.i(this.getClass().getSimpleName(), "User is leaving a StayPoint @ " + Constants.SIMPLE_DATE_FORMAT.format(timeOfDeparture));
                Log.i(this.getClass().getSimpleName(), "Stopping HAR module");
                stopHarSystem();
            }
        };
    }

    private void launchHarSystem() {
        runningCount = 0;
        // Next lines should be cancelled and changed by accelerometerHub.startClassification() if we want a permanent classification
        harTimer = new Timer();
        harTimer.scheduleAtFixedRate(harTask, 0, Constants.ONE_MINUTE);
    }

    /***
     * Stops Timer and TimerTask machinery for stopping classification module
     */
    private void stopHarSystem() {
        runningCount = 0;
        Log.i(GeoFencing.class.getSimpleName(), "Classification stopped");
        accelerometerHub.stopClassification();
        // Next lines should be ignored if we want a permanent classification
        harTask.cancel();
        harTimer.cancel();
        harTimer.purge();
    }

    /***
     * Build the HAR TimerTask that is scheduled periodically by a Timer
     * @return The TimerTask to be scheduled for starting readings
     */
    private TimerTask buildHarTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Log.i(GeoFencing.class.getSimpleName(), "Classification started");
                accelerometerHub.startClassification(harClassificationListener);
            }
        };
    }

    /***
     * Builds the ClassificationListener for receiving notifications of classification and storing such information
     * @return A NaiveBayesListener that registers information and can stop the HAR system for achieving a
     * DutyCycling behavior
     * @see NaiveBayesListener
     */
    private NaiveBayesListener buildNaiveBayesListener() {
        return new NaiveBayesListener() {
            @Override
            public void onClassifiedPattern(byte predictedActivityType) {
                // 1 Create a DbActivityInStayPoint object with current Time
                DbActivityInStayPoint activity = new DbActivityInStayPoint(0, currentVisit.getId(), predictedActivityType, new Date(System.currentTimeMillis()));

                // 2 Save the activity in repository
                activity = activitiesDal.add(activity);

                runningCount++;
                if (runningCount == MAX_RUNNING_COUNT) {
                    stopHarSystem();
                }
            }
        };
    }
}
