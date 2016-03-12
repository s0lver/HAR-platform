package tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPointVisit;
import tamps.cinvestav.s0lver.HAR_platform.mobility.utils.Constants;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

class StayPointVisitsDal {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allVisitsTableColumns = {SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_STAY_POINT_ID, SQLiteHelper.COLUMN_ARRIVAL_TIME,
            SQLiteHelper.COLUMN_DEPARTURE_TIME, SQLiteHelper.COLUMN_STATIC_PERCENTAGE,
            SQLiteHelper.COLUMN_WALKING_PERCENTAGE, SQLiteHelper.COLUMN_RUNNING_PERCENTAGE};

    public StayPointVisitsDal(Context context) {
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
     * Adds a DbStayPointVisit to the repository
     * @param visit    The DbStayPointVisit to add
     * @return A DbStayPointVisit with the information stored in the database
     * @see DbStayPointVisit
     */
    public DbStayPointVisit add(DbStayPointVisit visit) {
        this.open();
        ContentValues values = buildContentValues(visit);

        long insertId = database.insert(SQLiteHelper.TABLE_VISITS, null, values);
        visit = findVisit(insertId);
        this.close();

        Log.i(this.getClass().getSimpleName(), "Record added");
        return visit;
    }

    public DbStayPointVisit update(DbStayPointVisit visit) {
        this.open();

        ContentValues values = buildContentValues(visit);
        database.update(SQLiteHelper.TABLE_VISITS, values, SQLiteHelper.COLUMN_ID + " = " + visit.getId(), null);
        DbStayPointVisit updatedVisit = findVisit(visit.getId());

        this.close();
        Log.i(this.getClass().getSimpleName(), "Record updated");
        return updatedVisit;
    }

    /***
     * Builds a ContentValues dictionary? from the values of the DbStayPointVisit reference
     * @param visit
     * @return A ContentValues object with the mapped information from the DbStayPointVisit reference
     * @see DbStayPointVisit
     */
    private ContentValues buildContentValues(DbStayPointVisit visit) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_STAY_POINT_ID, visit.getIdStayPoint());
        values.put(SQLiteHelper.COLUMN_ARRIVAL_TIME, Constants.FILE_NAMES_SIMPLE_DATE_FORMAT.format(visit.getArrivalTime()));
        values.put(SQLiteHelper.COLUMN_DEPARTURE_TIME, Constants.FILE_NAMES_SIMPLE_DATE_FORMAT.format(visit.getDepartureTime()));
        values.put(SQLiteHelper.COLUMN_STATIC_PERCENTAGE, visit.getStaticPercentage());
        values.put(SQLiteHelper.COLUMN_WALKING_PERCENTAGE, visit.getWalkingPercentage());
        values.put(SQLiteHelper.COLUMN_RUNNING_PERCENTAGE, visit.getRunningPercentage());

        return values;
    }

    /***
     * Finds an DbStayPointVisit record given its id
     * @param id The id to look for
     * @return A DbStayPointVisit object with the information found in the repository
     * @see DbStayPointVisit
     */
    private DbStayPointVisit findVisit(long id) {
        Cursor cursor = database.query(SQLiteHelper.TABLE_VISITS, allVisitsTableColumns,
                SQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        return createStayPointVisitFromCursor(cursor);
    }

    /***
     * Creates a DbStayPointVisit object from the cursor's current position.
     * @param cursor A valid cursor to the database
     * @return A DbStayPointVisit with the information collected from cursor
     * @see DbStayPointVisit
     */
    private DbStayPointVisit createStayPointVisitFromCursor(Cursor cursor) {
        try {
            long id = cursor.getLong(0);
            long idStayPoint = cursor.getLong(1);
            Date arrivalTime = Constants.FILE_NAMES_SIMPLE_DATE_FORMAT.parse(cursor.getString(2));
            Date departureTime = Constants.FILE_NAMES_SIMPLE_DATE_FORMAT.parse(cursor.getString(3));
            double staticPercentage = cursor.getDouble(4);
            double walkingPercentage = cursor.getDouble(5);
            double runningPercentage = cursor.getDouble(6);

            return new DbStayPointVisit(id, idStayPoint, arrivalTime, departureTime,
                    staticPercentage, walkingPercentage, runningPercentage);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred when building the DbStayPointVisit from cursor");
        }
    }

    /***
     * Obtains all the visits DbStayPointVisit from the DbStayPoint specified
     * @param stayPoint The StayPoint from which visits are to be collected
     * @return An ArrayList of the DbStayPointVisit objects
     * @see DbStayPointVisit
     */
    public ArrayList<DbStayPointVisit> getAllByStayPoint(DbStayPoint stayPoint) {
        return getAllByStayPoint(stayPoint.getId());
    }

    /***
     * Obtains all the visits DbStayPointVisit from the StayPoint id specified
     * @param stayPointId The id of the StayPoint from which visits are to be collected
     * @return An ArrayList of the DbStayPointVisit objects
     * @see DbStayPointVisit
     */
    public ArrayList<DbStayPointVisit> getAllByStayPoint(long stayPointId) {
        ArrayList<DbStayPointVisit> dbStayPointVisits = new ArrayList<>();
        this.open();
        Cursor cursor = database.query(SQLiteHelper.TABLE_STAY_POINTS, allVisitsTableColumns, SQLiteHelper.COLUMN_STAY_POINT_ID, new String[]{String.valueOf(stayPointId)}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DbStayPointVisit visit = createStayPointVisitFromCursor(cursor);
            dbStayPointVisits.add(visit);
            cursor.moveToNext();
        }

        cursor.close();
        this.close();
        return dbStayPointVisits;
    }

}
