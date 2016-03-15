package tamps.cinvestav.s0lver.HAR_platform.mobility.modules;

import android.content.Context;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.har.activities.Activities;
import tamps.cinvestav.s0lver.HAR_platform.har.classifiers.NaiveBayesListener;
import tamps.cinvestav.s0lver.HAR_platform.har.hubs.AccelerometerHub;
import tamps.cinvestav.s0lver.HAR_platform.mobility.classifiers.MobilityListener;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.ActivitiesInStayPointDal;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbActivityInStayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.utils.Constants;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/***
 * Controls the execution of the HAR system according to changes in mobility and the result of accelerometer data classification.
 * At the moment, it starts HAR classification when user enters into a StayPoint and stops it when user leaves.
 * Also it stops when the amount of classified windows is equal to harWindowsPerIntervention,
 * relaunching the classification according to the readingsPeriodRate
 */
public class HarController implements MobilityListener, NaiveBayesListener{
    private int runningCount;
    private Timer harTimer;
    private TimerTask harTask;
    private final NaiveBayesListener harClassificationListener;
    private final AccelerometerHub accelerometerHub;
    private final ActivitiesInStayPointDal activitiesDal;
    private final GeoFencing geoFencing;
    private final int harWindowsPerIntervention;
    private final long readingsPeriodRate;

    /***
     * Constructor
     * @param context The context for accessing to sensors system service
     * @param geoFencing                   A reference to a GeoFencing object as for obtaining the current StayPoint in user mobility
     * @param harWindowsPerIntervention    The amount of sampling windows (of five seconds) to be employed in each intervention for classifying accelerometer data.
     * @param readingsPeriodRate           The sampling period of each intervention for classifying accelerometer data. Should be less than 5 secs * harWindowsPerIntervention
     */
    public HarController(Context context, GeoFencing geoFencing, int harWindowsPerIntervention, long readingsPeriodRate) {
        this.accelerometerHub = new AccelerometerHub(context);
        this.geoFencing = geoFencing;
        this.harWindowsPerIntervention = harWindowsPerIntervention;
        this.readingsPeriodRate = readingsPeriodRate;
        this.activitiesDal = new ActivitiesInStayPointDal(context);
        this.harClassificationListener = this;
        Log.i(this.getClass().getSimpleName(), "Har windows per intervention is " + harWindowsPerIntervention);
    }

    /***
     * Starts Timer and TimerTask machinery for launching the classification module
     */
    private void launchHarSystem() {
        runningCount = 0;
        Log.i(this.getClass().getSimpleName(), "HAR system started");
        // Next lines should be cancelled and changed by accelerometerHub.startClassification() if we want a permanent classification
        this.harTask = buildHarTask();
        harTimer = new Timer();
        harTimer.scheduleAtFixedRate(harTask, 0, readingsPeriodRate);
    }

    /***
     * Stops Timer and TimerTask machinery for stopping HAR classification module completely
     */
    public void stopHarSystem() {
        runningCount = 0;
        Log.i(this.getClass().getSimpleName(), "HAR system stopped");
        accelerometerHub.stopClassification();
        // Next lines should be ignored if we want a permanent classification
        harTask.cancel();
        harTimer.cancel();
        harTimer.purge();
    }

    /***
     * Stops the current task but let the other future interventions to work
     */
    private void stopHarSystemIntervention() {
        accelerometerHub.stopClassification();
        runningCount = 0;
        Log.i(this.getClass().getSimpleName(), "HAR system INTERVENTION is ending");
    }

    /***
     * Build the HAR TimerTask that is scheduled periodically by a Timer
     * @return The TimerTask to be scheduled for starting readings
     */
    private TimerTask buildHarTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Log.i(this.getClass().getSimpleName(), "HAR system INTERVENTION is starting");
                accelerometerHub.startClassification(harClassificationListener);
            }
        };
    }

    /***
     * Gets the notification of the user arriving at a StayPoint and instructs the launching of HAR system
     * @param stayPoint The DbStayPoint to where the user has arrived
     * @param timeOfArrival The time on which the arrival was detected (might not coincide with
     *                              the stored information of the StayPoint)
     */
    @Override
    public void onUserArrivingStayPoint(DbStayPoint stayPoint, Date timeOfArrival) {
        Log.i(this.getClass().getSimpleName(), "Got notification of user arriving at a StayPoint @ " + Constants.RECORDS_SIMPLE_DATE_FORMAT.format(timeOfArrival));
        Log.i(this.getClass().getSimpleName(), "StayPoint is " + stayPoint);
        launchHarSystem();
    }

    /***
     * Gets the notification of user leaving a StayPoint and instructs the cancelling of the HAR system
     * @param stayPoint The DbStayPoint that the user is leaving
     * @param timeOfDeparture The time on which the departure was detected (might not coincide with
     */
    @Override
    public void onUserLeavingStayPoint(DbStayPoint stayPoint, Date timeOfDeparture) {
        Log.i(this.getClass().getSimpleName(), "Got notification of user leaving a StayPoint @ " + Constants.RECORDS_SIMPLE_DATE_FORMAT.format(timeOfDeparture));
        Log.i(this.getClass().getSimpleName(), "StayPoint is " + stayPoint);
        stopHarSystem();
    }

    /***
     * Stores the activity and stops the classification tasks after exceeding the harWindowsPerIntervention threshold
     * @param predictedActivityType The type of the activity on which the pattern has been predicted
     */
    @Override
    public void onClassifiedPattern(byte predictedActivityType) {
        Log.i(this.getClass().getSimpleName(), "Detected activity " + Activities.getAsString(predictedActivityType));
        DbActivityInStayPoint activity = new DbActivityInStayPoint(0, geoFencing.getCurrentVisit().getId(), predictedActivityType, new Date(System.currentTimeMillis()));
        activity = activitiesDal.add(activity);
        runningCount++;
        if (runningCount == harWindowsPerIntervention) {
            stopHarSystemIntervention();
        }
    }
}
