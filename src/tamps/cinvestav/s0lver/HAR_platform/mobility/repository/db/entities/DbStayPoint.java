package tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities;

import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;

import java.util.Date;

/***
 * Models the StayPoints when stored in the database.
 * It consists on a StayPoint enhanced with id and visitCount
 * @see StayPoint
 */
public class DbStayPoint{
    private long id;
    private int visitCount;
    private StayPoint stayPoint;

    public DbStayPoint(long id, double latitude, double longitude, Date arrivalTime, Date departureTime, int amountFixesInvolved, int visitCount) {
        this.stayPoint = new StayPoint(latitude, longitude, arrivalTime, departureTime, amountFixesInvolved);
        this.id = id;
        this.visitCount = visitCount;
    }

    public DbStayPoint(long id, int visitCount, StayPoint stayPoint) {
        this.id = id;
        this.visitCount = visitCount;
        this.stayPoint = stayPoint;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public StayPoint getStayPoint() {
        return stayPoint;
    }

    public void setStayPoint(StayPoint stayPoint) {
        this.stayPoint = stayPoint;
    }

    @Override
    public String toString() {
        return "id " + id + ", visits " + visitCount + ", " + stayPoint.toString();
    }
}
