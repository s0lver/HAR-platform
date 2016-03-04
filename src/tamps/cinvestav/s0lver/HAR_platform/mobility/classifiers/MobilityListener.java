package tamps.cinvestav.s0lver.HAR_platform.mobility.classifiers;

import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;

import java.util.Date;

/***
 * Listens to user entering or leaving StayPoints
 * @see StayPoint
 */
public interface MobilityListener {
    /***
     * Triggered when the user is arriving a StayPoint
     * @param stayPoint The StayPoint to where the user has arrived
     * @param timeOfArrivalDetected The time on which the arrival was detected (might not coincide with
     *                              the stored information of the StayPoint)
     * @see StayPoint
     */
    void onUserArrivingStayPoint(StayPoint stayPoint, Date timeOfArrivalDetected);

    /***
     * Triggered when the user leaves a StayPoint
     * @param timeOfDeparture The time on which the departure was detected (might not coincide with
     *                        the stored information of the StayPoint)
     */
    void onUserLeavingStayPoint(Date timeOfDeparture);
}
