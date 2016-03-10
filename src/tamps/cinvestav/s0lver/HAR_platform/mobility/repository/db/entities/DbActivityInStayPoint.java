package tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities;

import java.util.Date;

/***
 * Models the activity performed in a visit to a StayPoint
 * @see DbStayPoint
 * @see tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint
 */
public class DbActivityInStayPoint {
    private long id;
    private long idVisit;
    private int activityType;
    private Date timestamp;

    public DbActivityInStayPoint(long id, long idVisit, int activityType, Date timestamp) {
        this.id = id;
        this.idVisit = idVisit;
        this.activityType = activityType;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdVisit() {
        return idVisit;
    }

    public void setIdVisit(long idVisit) {
        this.idVisit = idVisit;
    }

    public int getActivityType() {
        return activityType;
    }

    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
