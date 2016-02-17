package tamps.cinvestav.s0lver.HAR_platform;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import tamps.cinvestav.s0lver.HAR_platform.sensorsaccess.ThreadSensorReader;

public class MainActivity extends Activity {
    private static final int ONE_SECOND = 1000;
    private ThreadSensorReader reader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        reader = new ThreadSensorReader(getApplicationContext(), 5 * ONE_SECOND, 3);
    }

    public void clickStartReadings(View view) {
        reader.startReadings();
    }

    public void clickStopReadings(View view) {
        reader.stopReadings();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reader.stopReadings();
    }
}
