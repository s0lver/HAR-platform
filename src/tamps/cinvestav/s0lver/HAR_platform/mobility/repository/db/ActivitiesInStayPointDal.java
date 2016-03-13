package tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbActivityInStayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPointVisit;
import tamps.cinvestav.s0lver.HAR_platform.mobility.utils.Constants;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/***
 * Data Access Layer of the Activities entity
 */
public class ActivitiesInStayPointDal {
    private final Context context;
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allActivitiesTableColumns = {SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_VISIT_ID, SQLiteHelper.COLUMN_ACTIVITY_TYPE,
            SQLiteHelper.COLUMN_TIMESTAMP};

    public ActivitiesInStayPointDal(Context context) {
        this.context = context;
        dbHelper = new SQLiteHelper(context);
    }

    /***
     * Opens and establishes access to the database with writable features
     */
    private void open() {
        database = dbHelper.getWritableDatabase();
    }

    /***
     * Closes the connection with the SQLite database
     */
    private void close() {
        dbHelper.close();
    }

    /***
     * Adds a DbActivityInStayPoint to the repository
     * @param activity    The Activity to add
     * @return A DbActivityInStayPoint with the information stored in the database
     * @see DbActivityInStayPoint
     */
    public DbActivityInStayPoint add(DbActivityInStayPoint activity) {
        this.open();
        ContentValues values = buildContentValues(activity);

        long insertId = database.insert(SQLiteHelper.TABLE_ACTIVITIES, null, values);
        activity = findActivity(insertId);
        this.close();

        Log.i(this.getClass().getSimpleName(), "Record added");
        return activity;
    }

    /***
     * Builds a ContentValues dictionary? from the values of the DbActivityInStayPoint reference
     * @param activity
     * @return A ContentValues object with the mapped information from the DbStayPoint reference
     * @see DbActivityInStayPoint
     */
    private ContentValues buildContentValues(DbActivityInStayPoint activity) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_VISIT_ID, activity.getIdVisit());
        values.put(SQLiteHelper.COLUMN_ACTIVITY_TYPE, activity.getActivityType());
        values.put(SQLiteHelper.COLUMN_TIMESTAMP, Constants.FILE_NAMES_SIMPLE_DATE_FORMAT.format(activity.getTimestamp()));

        return values;
    }

    /***
     * Finds an DbActivityInStayPoint record given its id
     * @param id The id to look for
     * @return A DbActivityInStayPoint object with the information found in the repository
     * @see DbActivityInStayPoint
     */
    private DbActivityInStayPoint findActivity(long id) {
        Cursor cursor = database.query(SQLiteHelper.TABLE_ACTIVITIES, allActivitiesTableColumns,
                SQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        return createActivityFromCursor(cursor);
    }

    /***
     * Creates a DbActivityInStayPoint object from the cursor's current position.
     * @param cursor A valid cursor to the database
     * @return A DbActivityInStayPoint with the information collected from cursor
     * @see DbActivityInStayPoint
     */
    private DbActivityInStayPoint createActivityFromCursor(Cursor cursor) {
        try {
            long id = cursor.getLong(0);
            long idVisit = cursor.getLong(1);
            int activityType = cursor.getInt(2);
            Date timestamp = Constants.FILE_NAMES_SIMPLE_DATE_FORMAT.parse(cursor.getString(3));

            return new DbActivityInStayPoint(id, idVisit, activityType, timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred when building the DbActivityInStayPoint from cursor");
        }
    }

    /***
     * Get activities for the specified visit
     * @param idVisit The id of the visit to a specific stay point
     * @return An ArrayList of the activities performed on the specified idVisit
     * @see DbActivityInStayPoint
     */
    public ArrayList<DbActivityInStayPoint> getAllByVisit(long idVisit) {
        ArrayList<DbActivityInStayPoint> dbActivities = new ArrayList<DbActivityInStayPoint>();
        this.open();
//        Cursor cursor = database.query(SQLiteHelper.TABLE_ACTIVITIES, allActivitiesTableColumns, SQLiteHelper.COLUMN_VISIT_ID, new String[]{String.valueOf(idVisit)}, null, null, null);
        Cursor cursor = database.query(SQLiteHelper.TABLE_ACTIVITIES, allActivitiesTableColumns, SQLiteHelper.COLUMN_VISIT_ID + " = " + idVisit, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DbActivityInStayPoint activity = createActivityFromCursor(cursor);
            dbActivities.add(activity);
            cursor.moveToNext();
        }

        cursor.close();
        this.close();
        return dbActivities;
    }

    /***
     * Gets the list of activities performed on the specified StayPoint id.
     * @param idStayPoint The id of the StayPoint
     * @return The list of activities performed on the StayPoint (<b>all time</b>).
     * @see DbActivityInStayPoint
     */
    public ArrayList<DbActivityInStayPoint> getAllByStayPoint(long idStayPoint) {
        ArrayList<DbActivityInStayPoint> activities = new ArrayList<>();

        StayPointVisitsDal visitsDal = new StayPointVisitsDal(this.context);
        ArrayList<DbStayPointVisit> visits = visitsDal.getAllByStayPoint(idStayPoint);

        for (DbStayPointVisit visit : visits) {
            ArrayList<DbActivityInStayPoint> activitiesPerVisit = getAllByVisit(visit.getId());
            activities.addAll(activitiesPerVisit);
        }

        return activities;
    }
}
