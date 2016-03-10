package tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/***
 * Creates and updates the database
 * From http://www.vogella.com/tutorials/AndroidSQLite/article.html
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_STAY_POINTS = "staypoints";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_ARRIVAL_TIME = "arrivalTime";
    public static final String COLUMN_DEPARTURE_TIME = "departureTime";
    public static final String COLUMN_VISIT_COUNT = "visitCount";

    public static final String TABLE_ACTIVITIES = "activities";
    public static final String COLUMN_ACTIVITY_TYPE = "activityType";
    public static final String COLUMN_VISIT_ID = "idVisit";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public static final String TABLE_VISITS = "staypointvisits";
    public static final String COLUMN_STAY_POINT_ID = "idStaypoint";
    public static final String COLUMN_STATIC_PERCENTAGE = "staticTime";
    public static final String COLUMN_WALKING_PERCENTAGE = "walkingTime";
    public static final String COLUMN_RUNNING_PERCENTAGE = "runningTime";


    private static final String DATABASE_NAME = "mobility.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_STAY_POINTS_TABLE = "create table " + TABLE_STAY_POINTS
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_LATITUDE + " decimal not null, "
            + COLUMN_LONGITUDE + " decimal not null, "
            + COLUMN_ARRIVAL_TIME + " text not null, "
            + COLUMN_DEPARTURE_TIME + " text not null, "
            + COLUMN_VISIT_COUNT + " integer not null"
            + ");";

    private static final String CREATE_ACTIVITIES_TABLE = "create table " + TABLE_ACTIVITIES
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_VISIT_ID + " integer not null, "
            + COLUMN_ACTIVITY_TYPE + " integer not null, "
            + COLUMN_TIMESTAMP + " text not null"
            + ");";

    private static final String CREATE_STAY_POINT_VISITS_TABLE = "create table " + TABLE_VISITS
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_STAY_POINT_ID + " integer not null, "
            + COLUMN_ARRIVAL_TIME + " text not null, "
            + COLUMN_DEPARTURE_TIME + " text not null, "
            + COLUMN_STATIC_PERCENTAGE + " decimal not null, "
            + COLUMN_WALKING_PERCENTAGE + " decimal not null, "
            + COLUMN_RUNNING_PERCENTAGE + " decimal not null"
            + ");";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_STAY_POINTS_TABLE);
        database.execSQL(CREATE_ACTIVITIES_TABLE);
        database.execSQL(CREATE_STAY_POINT_VISITS_TABLE);
        Log.i(this.getClass().getSimpleName(), "Database has been created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(this.getClass().getSimpleName(), "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAY_POINTS);
        onCreate(db);
    }

    public void clearDatabase(SQLiteDatabase database) {
        String clearTableQuery = "DELETE FROM " + SQLiteHelper.TABLE_ACTIVITIES;
        database.execSQL(clearTableQuery);

        clearTableQuery = "DELETE FROM " + SQLiteHelper.TABLE_VISITS;
        database.execSQL(clearTableQuery);

        clearTableQuery = "DELETE FROM " + SQLiteHelper.TABLE_STAY_POINTS;
        database.execSQL(clearTableQuery);
    }

    public void recreateDatabase(SQLiteDatabase database) {
        String recreateDBQuery = "DROP TABLE " + SQLiteHelper.TABLE_ACTIVITIES;
        database.execSQL(recreateDBQuery);

        recreateDBQuery = "DROP TABLE " + SQLiteHelper.TABLE_VISITS;
        database.execSQL(recreateDBQuery);

        recreateDBQuery = "DROP TABLE " + SQLiteHelper.TABLE_STAY_POINTS;
        database.execSQL(recreateDBQuery);

        onCreate(database);
    }
}