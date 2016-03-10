package tamps.cinvestav.s0lver.HAR_platform.mobility.repository;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.LocationAnalyzer;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.StayPointsDal;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPoint;

import java.util.ArrayList;

public class StayPointRepository {
    private double minimumDistanceThreshold;

    private StayPointsDal stayPointsDal;

    public StayPointRepository(Context context, double minimumDistanceThreshold) {
        this.minimumDistanceThreshold = minimumDistanceThreshold;
        stayPointsDal = new StayPointsDal(context);
    }

    public StayPointsDal getStayPointsDal() {
        return stayPointsDal;
    }

    public boolean contains(StayPoint stayPoint) {
        ArrayList<DbStayPoint> stayPoints = stayPointsDal.getAll();
        Location stayPointAsLocation = stayPoint.convertStayPointToLocation();

        DbStayPoint closestStayPoint = LocationAnalyzer.findClosestStayPoint(stayPointAsLocation, stayPoints, minimumDistanceThreshold);
        return closestStayPoint != null;
    }

    /***
     * This is our "onStayPointDetected(StayPoint stayPoint)"
     * @param stayPoint The StayPoint to add.
     * @return true if the StayPoint <b>doesn't exist</b> and can be added to the repository. False otherwise.
     */
    public boolean add(StayPoint stayPoint) {
        if (contains(stayPoint)) {
            Log.w(this.getClass().getSimpleName(), "The StayPoint already exists, can't add it");
            return false;
        }
        else{
            Log.i(this.getClass().getSimpleName(), "The StayPoint didn't exist, proceeding to add it");
            stayPointsDal.add(stayPoint);
            return true;
        }
    }

    public boolean update(DbStayPoint dbStayPoint) {
        if (contains(dbStayPoint.getStayPoint())) {
            Log.i(this.getClass().getSimpleName(), "Updating metadata...");
            updateMetadata(dbStayPoint);
            return true;
        }
        else{
            Log.w(this.getClass().getSimpleName(), "Can't modify stayPoint because it doesn't exist in the repository");
            return false;
        }
    }

    private void updateMetadata(DbStayPoint stayPoint) {
        stayPoint.setVisitCount(stayPoint.getVisitCount() + 1);
        DbStayPoint updatedDbStayPoint = stayPointsDal.update(stayPoint);
    }

    /***
     * Obtains the full list of stay points learned by the platform
     * @return An ArrayList of the stay points learned by the platform
     * @see DbStayPoint
     */
    public ArrayList<DbStayPoint> getAllStayPoints() {
        return stayPointsDal.getAll();
    }
}
