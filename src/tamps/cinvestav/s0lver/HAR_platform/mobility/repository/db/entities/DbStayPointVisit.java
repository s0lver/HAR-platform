package tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities;

import java.util.Date;

/***
 * Models a Visit to a StayPoint and the activity information of such visit.
 */
public class DbStayPointVisit {
    private long id;
    private long idStayPoint;
    private Date arrivalTime;
    private Date departureTime;
    private double staticPercentage;
    private double walkingPercentage;
    private double runningPercentage;

    public DbStayPointVisit(long id, long idStayPoint, Date arrivalTime, Date departureTime, double staticPercentage, double walkingPercentage, double runningPercentage) {
        this.id = id;
        this.idStayPoint = idStayPoint;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.staticPercentage = staticPercentage;
        this.walkingPercentage = walkingPercentage;
        this.runningPercentage = runningPercentage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdStayPoint() {
        return idStayPoint;
    }

    public void setIdStayPoint(long idStayPoint) {
        this.idStayPoint = idStayPoint;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public double getStaticPercentage() {
        return staticPercentage;
    }

    public void setStaticPercentage(double staticPercentage) {
        this.staticPercentage = staticPercentage;
    }

    public double getWalkingPercentage() {
        return walkingPercentage;
    }

    public void setWalkingPercentage(double walkingPercentage) {
        this.walkingPercentage = walkingPercentage;
    }

    public double getRunningPercentage() {
        return runningPercentage;
    }

    public void setRunningPercentage(double runningPercentage) {
        this.runningPercentage = runningPercentage;
    }
}
