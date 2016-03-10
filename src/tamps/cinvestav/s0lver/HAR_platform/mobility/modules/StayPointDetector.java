package tamps.cinvestav.s0lver.HAR_platform.mobility.modules;

import android.content.Context;
import android.location.Location;
import tamps.cinvestav.s0lver.HAR_platform.mobility.classifiers.ZhenAlgorithm;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.StayPointRepository;

/***
 * Calculates and detects the StayPoints from user location data.
 * @see Location
 * @see ZhenAlgorithm
 */
public class StayPointDetector{
    private ZhenAlgorithm zhenAlgorithm;
    private StayPointRepository stayPointRepository;

    public StayPointDetector(Context context, long minimumTimeThreshold, double minimumDistanceThreshold) {
        this.zhenAlgorithm = new ZhenAlgorithm(minimumTimeThreshold, minimumDistanceThreshold);
        this.stayPointRepository = new StayPointRepository(context, minimumDistanceThreshold);
    }

    /***
     * Analyzes the specified location for detecting a StayPoint.
     * If a StayPoint is generated, then it is tried to be stored in the repository
     * @param location The Location to test
     */
    public void analyzeLocation(Location location) {
        StayPoint stayPoint = zhenAlgorithm.processFix(location);
        if (stayPoint != null) {
            stayPointRepository.add(stayPoint);
        }
    }

    /***
     * Analyzes the last part of the trajectory.
     * If a StayPoint is generated, then it is tried to be stored in the repository.
     */
    public void analyzeLastPart() {
        StayPoint stayPoint = zhenAlgorithm.processLastPart();
        stayPointRepository.add(stayPoint);
    }
}
