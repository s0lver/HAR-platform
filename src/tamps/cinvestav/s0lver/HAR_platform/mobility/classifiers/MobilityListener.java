package tamps.cinvestav.s0lver.HAR_platform.mobility.classifiers;

import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPoint;

import java.util.Date;

/***
 * Listens to user entering or leaving StayPoints
 * @see DbStayPoint
 */
public interface MobilityListener {
    /***
     * Triggered when the user is arriving a StayPoint
     * @param dbStayPoint The DbStayPoint to where the user has arrived
     * @param timeOfArrivalDetected The time on which the arrival was detected (might not coincide with
     *                              the stored information of the StayPoint)
     * @see DbStayPoint
     */
    void onUserArrivingStayPoint(DbStayPoint dbStayPoint, Date timeOfArrivalDetected);

    /***
     * Triggered when the user leaves a StayPoint
     * @param timeOfDeparture The time on which the departure was detected (might not coincide with
     *                        the stored information of the StayPoint)
     */
    void onUserLeavingStayPoint(Date timeOfDeparture);
}
