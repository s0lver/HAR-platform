package tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities;

import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;

import java.util.Date;

public class DbStayPoint{
    private long id;
    private StayPoint stayPoint;

    public DbStayPoint(long id, double latitude, double longitude, Date arrivalTime, Date departureTime, int amountFixesInvolved) {
        this.stayPoint = new StayPoint(latitude, longitude, arrivalTime, departureTime, amountFixesInvolved);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public StayPoint getStayPoint() {
        return stayPoint;
    }

    public void setStayPoint(StayPoint stayPoint) {
        this.stayPoint = stayPoint;
    }
}
