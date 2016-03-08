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

    private static final String DATABASE_NAME = "mobility.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table " + TABLE_STAY_POINTS
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_LATITUDE + " decimal not null,"
            + COLUMN_LONGITUDE + " decimal not null,"
            + COLUMN_ARRIVAL_TIME + " text not null,"
            + COLUMN_DEPARTURE_TIME + " text not null"
            + ");";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        Log.i(this.getClass().getSimpleName(), "Database has been created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(this.getClass().getSimpleName(), "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAY_POINTS);
        onCreate(db);
    }

}