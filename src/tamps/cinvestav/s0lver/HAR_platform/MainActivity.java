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
import tamps.cinvestav.s0lver.HAR_platform.activities.ActivityPattern;
import tamps.cinvestav.s0lver.HAR_platform.io.TrainingFilesReader;
import tamps.cinvestav.s0lver.HAR_platform.processing.ThreadSensorReader;
import tamps.cinvestav.s0lver.HAR_platform.processing.classifiers.NaiveBayes;
import tamps.cinvestav.s0lver.HAR_platform.processing.classifiers.NaiveBayesListener;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final int ONE_SECOND = 1000;
    private ThreadSensorReader reader;
    private MediaPlayer mediaPlayerOn;
    private MediaPlayer mediaPlayerOff;
    MediaPlayer mediaPlayerStatic;
    MediaPlayer mediaPlayerWalking;
    MediaPlayer mediaPlayerRunning;
    private boolean readingInProgress;
    private Spinner lstActivities;
    private NaiveBayes naiveBayes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        prepareSoundPlayers();
        prepareSpinners();
    }

    private void prepareSoundPlayers() {
        mediaPlayerOn = MediaPlayer.create(getApplicationContext(), R.raw.notification_on);
        mediaPlayerOff = MediaPlayer.create(getApplicationContext(), R.raw.notification_off);

        mediaPlayerStatic = MediaPlayer.create(getApplicationContext(), R.raw.idle);
        mediaPlayerWalking = MediaPlayer.create(getApplicationContext(), R.raw.walking);
        mediaPlayerRunning = MediaPlayer.create(getApplicationContext(), R.raw.running);
    }

    public void clickTrainNaiveBayes(View view) {
        try {
            ArrayList<ActivityPattern> patternsStatic = TrainingFilesReader.readStaticFile(getApplicationContext());
            ArrayList<ActivityPattern> patternsWalking = TrainingFilesReader.readWalkingFile(getApplicationContext());
            ArrayList<ActivityPattern> patternsRunning = TrainingFilesReader.readRunningFile(getApplicationContext());
            patternsStatic.addAll(patternsWalking);
            patternsStatic.addAll(patternsRunning);
            naiveBayes = new NaiveBayes(patternsStatic);
            naiveBayes.train();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clickClassifyNaiveBayes(View view) {
        ActivityPattern patternStatic = new ActivityPattern(Activities.STATIC, 0.133316815, 0.032953133);
        ActivityPattern patternWalking = new ActivityPattern(Activities.WALKING, 4.901127318, 2.329955038);
        ActivityPattern patternRunning = new ActivityPattern(Activities.RUNNING, 11.09304163, 4.731527324);

        byte predStatic = naiveBayes.classify(patternStatic);
        Log.i(this.getClass().getSimpleName(), "Static has been classified as " + predStatic);

        byte predWalking = naiveBayes.classify(patternWalking);
        Log.i(this.getClass().getSimpleName(), "Walking has been classified as " + predWalking);

        byte predRunning = naiveBayes.classify(patternRunning);
        Log.i(this.getClass().getSimpleName(), "Running has been classified as " + predRunning);
    }

    public void clickStartReadings(View view) {
        String selectedActivity = lstActivities.getSelectedItem().toString().toLowerCase();
        reader = new ThreadSensorReader(getApplicationContext(), buildActivityDetector(), selectedActivity, 5 * ONE_SECOND, 3);

        showWaitingBox();
    }

    private void startReadings() {
        reader.startReadings();
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
                    Thread.sleep(ONE_SECOND);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Something wrong happened, I have to go", Toast.LENGTH_SHORT).show();
                    finish();
                }
                ringProgressDialog.dismiss();
                startReadings();
            }
        }).start();
    }

    public void clickStopReadings(View view) {
        reader.stopReadings();
        readingInProgress = false;
        mediaPlayerOff.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (readingInProgress) reader.stopReadings();

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
            public void notify(byte activityType) {
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
                if (activityType == Activities.STATIC) {
                    mediaPlayerStatic.start();
                }
            }
        };
    }
}
