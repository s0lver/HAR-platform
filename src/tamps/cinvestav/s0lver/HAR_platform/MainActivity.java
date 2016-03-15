package tamps.cinvestav.s0lver.HAR_platform;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import tamps.cinvestav.s0lver.HAR_platform.har.activities.Activities;
import tamps.cinvestav.s0lver.HAR_platform.har.classifiers.NaiveBayesListener;
import tamps.cinvestav.s0lver.HAR_platform.har.hubs.AccelerometerHub;
import tamps.cinvestav.s0lver.HAR_platform.har.utils.Constants;
import tamps.cinvestav.s0lver.HAR_platform.mobility.hubs.MobilityHub;
import tamps.cinvestav.s0lver.HAR_platform.mobility.io.SmartphoneFixesFileReader;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.StayPointRepository;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.SQLiteHelper;
import tamps.cinvestav.s0lver.HAR_platform.mobility.repository.db.entities.DbStayPoint;

import java.io.File;
import java.util.ArrayList;

/***
 * Testing activity
 */
public class MainActivity extends Activity {
    private MediaPlayer mediaPlayerOn;
    private MediaPlayer mediaPlayerOff;
    private MediaPlayer mediaPlayerStatic;
    private MediaPlayer mediaPlayerWalking;
    private MediaPlayer mediaPlayerRunning;

    AccelerometerHub accelerometerHub;
    private boolean readingInProgress;
    private boolean classificationInProgress;
    private Spinner lstActivities;

    private MobilityHub mobilityHub;
    private final int HAR_WINDOWS_PER_INTERVENTION = 5;
    private final long readingsPeriodRate = tamps.cinvestav.s0lver.HAR_platform.mobility.utils.Constants.ONE_MINUTE;

    private final int[] STARTING_ROWS_OF_STAY_POINT = new int[]{1, 278, 351, 653, 801, 955, 1050, 1122, 1363, 1543, 1700, 1778, 1952, 2152, 2648, 2802, 2948};
    private final int[] INDICES_OF_VISITS_TO_STAY_POINTS = new int[]{630, 652, 932, 956, 1051, 1122, 1541, 1701, 1952, 2018, 2022, 2152, 2555, 2944, 2948};
    private final int LAST_LEARNING_FIX = 614;
    private final int LAST_FIX = 3254;
    private SmartphoneFixesFileReader reader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        prepareSoundPlayers();
        prepareSpinners();
        accelerometerHub = new AccelerometerHub(this);
        mobilityHub = new MobilityHub(this, Constants.ONE_MINUTE * 45, tamps.cinvestav.s0lver.HAR_platform.mobility.utils.Constants.MIN_DISTANCE_PARAMETER, HAR_WINDOWS_PER_INTERVENTION, readingsPeriodRate);

        String path = Environment.getExternalStorageDirectory() + File.separator + "har-system" + File.separator+ "input-records.csv";
        Log.i(this.getClass().getSimpleName(), "Trying to open path " + path);
        reader = new SmartphoneFixesFileReader(path);

    }

    /***
     * Configures the sound players
     */
    private void prepareSoundPlayers() {
        mediaPlayerOn = MediaPlayer.create(getApplicationContext(), R.raw.notification_on);
        mediaPlayerOff = MediaPlayer.create(getApplicationContext(), R.raw.notification_off);

        mediaPlayerStatic = MediaPlayer.create(getApplicationContext(), R.raw.idle);
        mediaPlayerWalking = MediaPlayer.create(getApplicationContext(), R.raw.walking);
        mediaPlayerRunning = MediaPlayer.create(getApplicationContext(), R.raw.running);
    }

    /***
     * Called when the train naive bayes button is pressed
     * @param view The origin of the event
     */
    public void clickTrainNaiveBayes(View view) {
        accelerometerHub.trainClassifier();
    }

    /***
     * Called when the start classification button is pressed
     * @param view The origin of the event
     */
    public void clickStartClassification(View view) {
        mediaPlayerOn.start();
        accelerometerHub.startClassification(buildActivityDetector());
        classificationInProgress = true;
    }

    /***
     * Called when the stop classification button is pressed
     * @param view The origin of the event
     */
    public void clickStopClassification(View view) {
        accelerometerHub.stopClassification();
        readingInProgress = false;
        mediaPlayerOff.start();
    }

    /***
     * Called when the start collection-reading button is pressed.
     * It first shows a small waiting box for giving time to user to wear phone properly.
     * @param view The origin of the event
     */
    public void clickStartCollection(View view) {
        showWaitingBox();
    }

    /***
     * Starts the actual collection-reading of accelerometer data.
     */
    private void startCollection() {
        String selectedActivity = lstActivities.getSelectedItem().toString().toLowerCase();
        accelerometerHub.startDataCollection(selectedActivity);
        mediaPlayerOn.start();
        readingInProgress = true;
    }

    /***
     * Shows a waiting box, useful for giving time to user for putting phone on jeans pocket
     */
    private void showWaitingBox() {
        ProgressDialog ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...",	"Get ready ...", true);
        ringProgressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Constants.ONE_SECOND);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Something wrong happened, I have to go", Toast.LENGTH_SHORT).show();
                    finish();
                }
                ringProgressDialog.dismiss();
                startCollection();

            }
        }).start();
    }

    /***
     * Called when the stop collection-reading button is pressed
     * @param view The origin of touch event
     */
    public void clickStopCollection(View view) {
        accelerometerHub.stopDataCollection();
        readingInProgress = false;
        mediaPlayerOff.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (readingInProgress) accelerometerHub.stopDataCollection();

        if (classificationInProgress) accelerometerHub.stopClassification();

        mediaPlayerOn.reset();
        mediaPlayerOn.release();
        mediaPlayerOn = null;

        mediaPlayerOff.reset();
        mediaPlayerOff.release();
        mediaPlayerOff = null;

        mobilityHub.stopMobilityTracking();
    }

    private void prepareSpinners() {
        this.lstActivities = (Spinner) findViewById(R.id.lstActivities);
        final String[] availableActivities = new String[]{"Static", "Walking", "Running"};

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, availableActivities);

        lstActivities.setAdapter(adapter);
    }

    /***
     * Returns an NaiveBayesListener that is invoked once a pattern has been classified.
     * @return A NaiveBayesListener object
     * @see NaiveBayesListener
     */
    private NaiveBayesListener buildActivityDetector() {
        return new NaiveBayesListener() {
            @Override
            public void onClassifiedPattern(byte activityType) {
                Log.i(MainActivity.this.getClass().getSimpleName(), "Recognized activity type is " + activityType);
                switch (activityType) {
                    case Activities.STATIC:
                        mediaPlayerStatic.start();
                        break;
                    case Activities.WALKING:
                        mediaPlayerWalking.start();
                        break;
                    case Activities.RUNNING:
                        mediaPlayerRunning.start();
                        break;
                    default:
                        Log.i(MainActivity.this.getClass().getSimpleName(), "Unknown activity type");
                }
            }
        };
    }

    public void clickDoStuffWithDb(View view) {
//        simulateAdd();
        clearStayPointsTable();
//        recreateDatabase();
        showAllStayPoints();
    }

    private void recreateDatabase() {
        StayPointRepository repository = new StayPointRepository(this, tamps.cinvestav.s0lver.HAR_platform.mobility.utils.Constants.MIN_DISTANCE_PARAMETER / 2);
        SQLiteHelper dbHelper = new SQLiteHelper(this);
        dbHelper.recreateDatabase();
    }

    private void showAllStayPoints() {
        StayPointRepository repository = new StayPointRepository(this, tamps.cinvestav.s0lver.HAR_platform.mobility.utils.Constants.MIN_DISTANCE_PARAMETER / 2);
        ArrayList<DbStayPoint> stayPoints = repository.getStayPointsDal().getAll();
        for (DbStayPoint stayPoint : stayPoints) {
            Log.i(this.getClass().getSimpleName(), stayPoint.toString());
        }
    }

    private void clearStayPointsTable() {
        StayPointRepository repository = new StayPointRepository(this, tamps.cinvestav.s0lver.HAR_platform.mobility.utils.Constants.MIN_DISTANCE_PARAMETER / 2);
        SQLiteHelper helper = new SQLiteHelper(this);
        helper.clearDatabase();
        helper.recreateDatabase();
    }

    public void clickStartMobilityTracker(View view) {
        mobilityHub.startMobilityTracking();
    }

    public void clickStopMobilityTracker(View view) {
        mobilityHub.stopMobilityTracking();
    }


    private int currentRow = 0;
    private int currentIndexOfVisits = 0;
    
    public void clickBtnLearnStayPoints(View view) {
        // First, restart database
        clickDoStuffWithDb(view);
        Log.i(this.getClass().getSimpleName(), "Trying to learn");
        // Pass over the row 614 (on after 613, the last of the stay point), so it can learn Home 1, Home 2 and Cinvestav.
        for (int i = 0; i < LAST_LEARNING_FIX; i++) {
            Location location = reader.readLine();
            Log.i(this.getClass().getSimpleName(), "Passing row " + i);
            currentRow = i;
            if (location.getProvider().equals("null")) {
                Log.i(this.getClass().getSimpleName(), "Location not obtained at row " + i);
                continue;
            }
            mobilityHub.onLocationChanged(location);
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }

        currentRow = LAST_LEARNING_FIX;
        currentIndexOfVisits = 0;
        Log.i(this.getClass().getSimpleName(), "I just learned the first StayPoints");
    }

    public void clikBtnGoToNextStayPoint(View view) {
        if (currentIndexOfVisits == INDICES_OF_VISITS_TO_STAY_POINTS.length) {
            Toast.makeText(MainActivity.this, "You just passed the last StayPoint", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int index = INDICES_OF_VISITS_TO_STAY_POINTS[currentIndexOfVisits];
        for (int i = currentRow; i < index; i++) {
            Location location = reader.readLine();
//            Log.i(this.getClass().getSimpleName(), "Passing row " + i);
            mobilityHub.onLocationChanged(location);
        }
        Log.i(this.getClass().getSimpleName(), "The user is at the entrance of StayPoint " + currentIndexOfVisits + " (0 based)");
        currentRow = index;
        currentIndexOfVisits++;
    }
}
