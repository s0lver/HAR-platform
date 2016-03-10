package tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import tamps.cinvestav.s0lver.HAR_platform.mobility.entities.StayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPoint;
import tamps.cinvestav.s0lver.HAR_platform.mobility.utils.Constants;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/***
 * Database operations of the DbStayPoint class
 * @see DbStayPoint
 */
public class StayPointsDal {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allStayPointsTableColumns = {SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_LATITUDE, SQLiteHelper.COLUMN_LONGITUDE,
            SQLiteHelper.COLUMN_ARRIVAL_TIME, SQLiteHelper.COLUMN_DEPARTURE_TIME, SQLiteHelper.COLUMN_VISIT_COUNT};

    public StayPointsDal(Context context) {
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
     * Adds a StayPoint to the repository
     * @param stayPoint    The StayPoint to add
     * @return A DbStayPoint object with the information stored in the database
     * @see StayPoint
     * @see DbStayPoint
     */
    public DbStayPoint add(StayPoint stayPoint) {
        this.open();
        DbStayPoint dbStayPoint = new DbStayPoint(0, 1, stayPoint);
        ContentValues values = buildContentValues(dbStayPoint);

        long insertId = database.insert(SQLiteHelper.TABLE_STAY_POINTS, null, values);
        dbStayPoint = findStayPoint(insertId);
        this.close();

        Log.i(this.getClass().getSimpleName(), "Record added");
        return dbStayPoint;
    }

    /***
     * Finds a StayPoint given its id
     * @param id The id to look for
     * @return A DbStayPoint object with the information found in the repository
     * @see DbStayPoint
     */
    private DbStayPoint findStayPoint(long id) {
        Cursor cursor = database.query(SQLiteHelper.TABLE_STAY_POINTS, allStayPointsTableColumns,
                SQLiteHelper.COLUMN_ID + " = " + id, null, null, null, null);
        cursor.moveToFirst();
        return createStayPointFromCursor(cursor);
    }

    /***
     * Updates a DbStayPoint and returns an updated reference
     * @param stayPoint The DbStayPoint to update
     * @return A reference to a DbStayPoint with the updated information
     * @see DbStayPoint
     */
    public DbStayPoint update(DbStayPoint stayPoint) {
        this.open();

        ContentValues values = buildContentValues(stayPoint);
        database.update(SQLiteHelper.TABLE_STAY_POINTS, values, SQLiteHelper.COLUMN_ID + " = " + stayPoint.getId(), null);
        DbStayPoint updatedStayPoint = findStayPoint(stayPoint.getId());

        this.close();
        Log.i(this.getClass().getSimpleName(), "Record updated");
        return updatedStayPoint;
    }

    /***
     * Builds a ContentValues dictionary? from the values of the DbStayPoint reference
     * @param stayPoint
     * @return A ContentValues object with the mapped information from the DbStayPoint reference
     * @see DbStayPoint
     */
    private ContentValues buildContentValues(DbStayPoint stayPoint) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_LATITUDE, stayPoint.getStayPoint().getLatitude());
        values.put(SQLiteHelper.COLUMN_LONGITUDE, stayPoint.getStayPoint().getLongitude());
        values.put(SQLiteHelper.COLUMN_ARRIVAL_TIME, Constants.SIMPLE_DATE_FORMAT.format(stayPoint.getStayPoint().getArrivalTime()));
        values.put(SQLiteHelper.COLUMN_DEPARTURE_TIME, Constants.SIMPLE_DATE_FORMAT.format(stayPoint.getStayPoint().getDepartureTime()));
        values.put(SQLiteHelper.COLUMN_VISIT_COUNT, stayPoint.getVisitCount());

        return values;
    }

    /***
     * Creates a DbStayPoint object from the cursor's current position.
     * @param cursor A valid cursor to the database
     * @return A DbStayPoint with the information collected from cursor
     * @see DbStayPoint
     */
    private DbStayPoint createStayPointFromCursor(Cursor cursor) {
        try {
            long id = cursor.getLong(0);
            double latitude = cursor.getDouble(1);
            double longitude = cursor.getDouble(2);
            Date arrivalTime = Constants.SIMPLE_DATE_FORMAT.parse(cursor.getString(3));
            Date departureTime = Constants.SIMPLE_DATE_FORMAT.parse(cursor.getString(4));
            int visitCount = cursor.getInt(5);

            return new DbStayPoint(id, latitude, longitude, arrivalTime, departureTime, 0, visitCount);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred when building the StayPoint from cursor");
        }
    }

    /***
     * Deletes a DbStayPoint
     * @param stayPoint The DbStayPoint reference to delete. (Id needed).
     * @see DbStayPoint
     */
    public void delete(DbStayPoint stayPoint) {
        long id = stayPoint.getId();
        delete(id);
    }

    /***
     * Deletes a DbStayPoint given its id
     * @param id The id of the record to delete
     */
    public void delete(long id) {
        open();
        database.delete(SQLiteHelper.TABLE_STAY_POINTS, SQLiteHelper.COLUMN_ID + " = " + id, null);
        close();
    }

    /***
     * Collects all the stay points present in the database
     * @return An ArrayList with the whole set of stay points in the database
     * @see DbStayPoint
     */
    public ArrayList<DbStayPoint> getAll() {
        ArrayList<DbStayPoint> dbStayPoints = new ArrayList<DbStayPoint>();
        this.open();
        Cursor cursor = database.query(SQLiteHelper.TABLE_STAY_POINTS, allStayPointsTableColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DbStayPoint stayPoint = createStayPointFromCursor(cursor);
            dbStayPoints.add(stayPoint);
            cursor.moveToNext();
        }

        cursor.close();
        this.close();
        return dbStayPoints;
    }

    /***
     * Clears all database' tables
     */
    public void clearDatabase() {
        open();
        dbHelper.clearDatabase(database);
        close();
        Log.i(this.getClass().getSimpleName(), "Table " + SQLiteHelper.TABLE_STAY_POINTS + " cleared");
    }

    /***
     * Drops all tables and recreate database
     */
    public void recreateDatabase() {
        open();
        dbHelper.recreateDatabase(database);
        close();
    }
}
