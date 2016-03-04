package tamps.cinvestav.s0lver.HAR_platform;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import tamps.cinvestav.s0lver.HAR_platform.har.activities.Activities;
import tamps.cinvestav.s0lver.HAR_platform.har.classifiers.NaiveBayesListener;
import tamps.cinvestav.s0lver.HAR_platform.har.hubs.AccelerometerHub;
import tamps.cinvestav.s0lver.HAR_platform.har.utils.Constants;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        prepareSoundPlayers();
        prepareSpinners();
        accelerometerHub = new AccelerometerHub(this);
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
}
