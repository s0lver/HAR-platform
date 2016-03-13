package tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db;

import android.content.Context;
import android.util.Log;
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
     * The information updated is related to the activity performed during the visit to the StayPoint.
     * @param visit The DbStayPointVisit object to update.
     * @return A DbStayPointVisit with the updated information
     */
    public DbStayPointVisit updateVisitInformation(DbStayPointVisit visit) {
        ActivitiesInStayPointDal activitiesDal = new ActivitiesInStayPointDal(context);

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

        int amountOfActivities = activities.size();
        Log.i(this.getClass().getSimpleName(), "Amount of activities is " + amountOfActivities);

        double staticPercentage = 0, walkingPercentage = 0, runningPercentage = 0;
        if (amountOfActivities != 0) {
            staticPercentage = frequencyStatic / amountOfActivities;
            walkingPercentage = frequencyWalking / amountOfActivities;
            runningPercentage = frequencyRunning / amountOfActivities;
        }

        visit.setStaticPercentage(staticPercentage);
        visit.setWalkingPercentage(walkingPercentage);
        visit.setRunningPercentage(runningPercentage);

        visit = visitsDal.update(visit);
        return visit;
    }
}
