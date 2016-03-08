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
 * Database operations of the StayPoints class
 */
public class StayPointsDal {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_LATITUDE, SQLiteHelper.COLUMN_LONGITUDE,
            SQLiteHelper.COLUMN_ARRIVAL_TIME, SQLiteHelper.COLUMN_DEPARTURE_TIME};

    public StayPointsDal(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    private void open() {
        database = dbHelper.getWritableDatabase();
    }

    private void close() {
        dbHelper.close();
    }

    public DbStayPoint addStayPoint(StayPoint stayPoint) {
        this.open();
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_LATITUDE, stayPoint.getLatitude());
        values.put(SQLiteHelper.COLUMN_LONGITUDE, stayPoint.getLongitude());
        values.put(SQLiteHelper.COLUMN_ARRIVAL_TIME, Constants.SIMPLE_DATE_FORMAT.format(stayPoint.getArrivalTime()));
        values.put(SQLiteHelper.COLUMN_DEPARTURE_TIME, Constants.SIMPLE_DATE_FORMAT.format(stayPoint.getDepartureTime()));

        long insertId = database.insert(SQLiteHelper.TABLE_STAY_POINTS, null, values);
        Cursor cursor = database.query(SQLiteHelper.TABLE_STAY_POINTS, allColumns,
                SQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        DbStayPoint dbStayPoint = createStayPointFromCursor(cursor);
        this.close();

        Log.i(this.getClass().getSimpleName(), "Record added");
        return dbStayPoint;
    }

    private DbStayPoint createStayPointFromCursor(Cursor cursor) {
        try {
            long id = cursor.getLong(0);
            double latitude = cursor.getDouble(1);
            double longitude = cursor.getDouble(2);
            Date arrivalTime = Constants.SIMPLE_DATE_FORMAT.parse(cursor.getString(3));
            Date departureTime = Constants.SIMPLE_DATE_FORMAT.parse(cursor.getString(4));

            return new DbStayPoint(id, latitude, longitude, arrivalTime, departureTime, 0);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred when building the StayPoint from cursor");
        }
    }

    public void deleteStayPoint(DbStayPoint stayPoint) {
        long id = stayPoint.getId();
        deleteStayPoint(id);
    }

    public void deleteStayPoint(long id) {
        open();
        database.delete(SQLiteHelper.TABLE_STAY_POINTS, SQLiteHelper.COLUMN_ID + " = " + id, null);
        close();
    }

    public ArrayList<DbStayPoint> getAllStayPoints() {
        ArrayList<DbStayPoint> comments = new ArrayList<DbStayPoint>();
        this.open();
        Cursor cursor = database.query(SQLiteHelper.TABLE_STAY_POINTS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            DbStayPoint stayPoint = createStayPointFromCursor(cursor);
            comments.add(stayPoint);
            cursor.moveToNext();
        }

        cursor.close();
        this.close();
        return comments;
    }
}
