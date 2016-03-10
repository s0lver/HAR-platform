package tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db;

import android.content.Context;
import tamps.cinvestav.s0lver.HAR_platform.har.activities.Activities;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbActivityInStayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPointVisit;

import java.util.ArrayList;
import java.util.Date;

public class StayPointVisitsRepository {
    private Context context;
    private StayPointVisitsDal visitsDal;

    public StayPointVisitsRepository(Context context) {
        this.context = context;
        this.visitsDal = new StayPointVisitsDal(context);
    }

    /***
     * Adds a DbStayPointVisit object to the database
     * @param visit The object to store
     * @return A DbStayPointVisit object with the information stored
     */
    public DbStayPointVisit add(DbStayPointVisit visit) {
        return visitsDal.add(visit);
    }

    /***
     * Updates the information of the DbStayPointVisit and stores it.
     * The information updated is related to the activity performed duringthe visit to the StayPoint.
     * @param visit The DbStayPointVisit object to update.
     * @return A DbStayPointVisit with the updated information
     */
    public DbStayPointVisit updateVisitInformation(DbStayPointVisit visit) {
        ActivitiesInStayPointDal activitiesDal = new ActivitiesInStayPointDal(context);

//        visit.setDepartureTime(new Date(System.currentTimeMillis()));

        ArrayList<DbActivityInStayPoint> activities = activitiesDal.getAllByVisit(visit.getId());

        int frequencyStatic = 0, frequencyWalking = 0, frequencyRunning = 0;

        for (DbActivityInStayPoint activity : activities) {
            switch (activity.getActivityType()) {
                case Activities.STATIC:
                    frequencyStatic++;
                    break;
                case Activities.WALKING:
                    frequencyWalking++;
                    break;
                case Activities.RUNNING:
                    frequencyRunning++;
                    break;
            }
        }

        visit.setStaticPercentage(frequencyStatic / activities.size());
        visit.setWalkingPercentage(frequencyWalking / activities.size());
        visit.setRunningPercentage(frequencyRunning / activities.size());

        visit = visitsDal.update(visit);
        return visit;
    }
}
