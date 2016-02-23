package tamps.cinvestav.s0lver.HAR_platform;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import tamps.cinvestav.s0lver.HAR_Processing.ThreadSensorReader;

public class MainActivity extends Activity {
    private static final int ONE_SECOND = 1000;
    private ThreadSensorReader reader;
    private MediaPlayer mediaPlayerOn;
    private MediaPlayer mediaPlayerOff;
    private boolean readingInProgress;
    private Spinner lstActivities;

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
    }

    public void clickStartReadings(View view) {
        String selectedActivity = lstActivities.getSelectedItem().toString().toLowerCase();
        reader = new ThreadSensorReader(getApplicationContext(), selectedActivity, 5 * ONE_SECOND, 3);

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
                    Thread.sleep(ONE_SECOND * 5);
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
}
