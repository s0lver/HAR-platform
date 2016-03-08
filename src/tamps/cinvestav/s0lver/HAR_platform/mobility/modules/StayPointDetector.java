package tamps.cinvestav.s0lver.HAR_platform.mobility.modules;

import android.content.Context;
import android.location.Location;
import tamps.cinvestav.s0lver.HAR_platform.mobility.classifiers.ZhenAlgorithm;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.StayPointRepository;

public class StayPointDetector{
    private ZhenAlgorithm zhenAlgorithm;
    private StayPointRepository stayPointRepository;

    public StayPointDetector(Context context, long minimumTimeThreshold, double minimumDistanceThreshold) {
        this.zhenAlgorithm = new ZhenAlgorithm(minimumTimeThreshold, minimumDistanceThreshold);
        this.stayPointRepository = new StayPointRepository(context, minimumDistanceThreshold);
    }

    public void analyzeLocation(Location location) {
        StayPoint stayPoint = zhenAlgorithm.processFix(location);
        if (stayPoint != null) {
            stayPointRepository.add(stayPoint);
        }
    }

    public void analyzeLastPart() {
        StayPoint stayPoint = zhenAlgorithm.processLastPart();
        stayPointRepository.add(stayPoint);
    }
}
