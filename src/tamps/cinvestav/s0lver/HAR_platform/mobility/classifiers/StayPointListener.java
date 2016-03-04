package tamps.cinvestav.s0lver.HAR_platform.mobility.classifiers;


import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;

public interface StayPointListener {
    void onStayPointDetected(StayPoint stayPoint);
}
