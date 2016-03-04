package tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.classifiers;


import tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.entities.StayPoint;

public interface StayPointListener {
    void onStayPointDetected(StayPoint stayPoint);
}
