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
import tamps.cinvestav.s0lver.HAR_platform.activities.Activities;
import tamps.cinvestav.s0lver.HAR_platform.classifiers.NaiveBayesConfiguration;
import tamps.cinvestav.s0lver.HAR_platform.classifiers.NaiveBayesListener;
import tamps.cinvestav.s0lver.HAR_platform.modules.ModuleAccelerometerClassifier;
import tamps.cinvestav.s0lver.HAR_platform.modules.ModuleAccelerometerLogger;
import tamps.cinvestav.s0lver.HAR_platform.modules.ModuleTrainer;
import tamps.cinvestav.s0lver.HAR_platform.utils.Constants;

public class MainActivity extends Activity {
    private MediaPlayer mediaPlayerOn;
    private MediaPlayer mediaPlayerOff;
    private MediaPlayer mediaPlayerStatic;
    private MediaPlayer mediaPlayerWalking;
    private MediaPlayer mediaPlayerRunning;

    private ModuleAccelerometerLogger reader;
    private ModuleAccelerometerClassifier classifier;
    private boolean readingInProgress;
    private Spinner lstActivities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        prepareSoundPlayers();
        prepareSpinners();
        this.classifier = new ModuleAccelerometerClassifier(getApplicationContext(), buildActivityDetector(), 5 * Constants.ONE_SECOND);
    }

    private void prepareSoundPlayers() {
        mediaPlayerOn = MediaPlayer.create(getApplicationContext(), R.raw.notification_on);
        mediaPlayerOff = MediaPlayer.create(getApplicationContext(), R.raw.notification_off);

        mediaPlayerStatic = MediaPlayer.create(getApplicationContext(), R.raw.idle);
        mediaPlayerWalking = MediaPlayer.create(getApplicationContext(), R.raw.walking);
        mediaPlayerRunning = MediaPlayer.create(getApplicationContext(), R.raw.running);
    }

    public void clickTrainNaiveBayes(View view) {
        ModuleTrainer trainer = new ModuleTrainer(this);
        NaiveBayesConfiguration train = trainer.train();
    }

    public void clickStartClassification(View view) {
        classifier.startClassification();
    }

    public void clickStopClassification(View view) {
        classifier.stopClassification();
    }

    public void clickStartCollection(View view) {
        String selectedActivity = lstActivities.getSelectedItem().toString().toLowerCase();
        reader = new ModuleAccelerometerLogger(getApplicationContext(), selectedActivity, 5 * Constants.ONE_SECOND);

        showWaitingBox();
    }

    private void startCollection() {
        reader.startAccelerometerReadings();
        mediaPlayerOn.start();
        readingInProgress = true;
    }

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

    public void clickStopCollection(View view) {
        reader.stopAccelerometerReadings();
        readingInProgress = false;
        mediaPlayerOff.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (readingInProgress) reader.stopAccelerometerReadings();

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

    private NaiveBayesListener buildActivityDetector() {
        return new NaiveBayesListener() {
            @Override
            public void onClassifiedPattern(byte activityType) {
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
