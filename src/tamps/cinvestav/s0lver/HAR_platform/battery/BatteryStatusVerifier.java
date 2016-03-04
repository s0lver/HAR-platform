package tamps.cinvestav.s0lver.HAR_platform.battery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/***
 * Registers an Intent for obtaining the battery status
 * @see BatteryStatus
 */
public class BatteryStatusVerifier {
    private Context context;

    public BatteryStatusVerifier(Context context) {
        this.context = context;
    }

    public static final String STRING_BATT_LEVEL = "level";
    public static final String STRING_BATT_SCALE = "scale";
    public static final String STRING_BATT_PRESENT = "present";
    public static final String STRING_BATT_TECH = "technology";
    public static final String STRING_BATT_VOLTAGE = "voltage";
    public static final String STRING_BATT_STATUS = "status";
    public static final String STRING_BATT_PLUGGED = "plugged";
    public static final String STRING_BATT_HEALTH = "health";
    public static final String STRING_BATT_TEMPERATURE = "temperature";

    public BatteryStatus getBatteryStatus() {
        Intent batteryIntent = context.registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int rawLevel = batteryIntent.getIntExtra(STRING_BATT_LEVEL, -1);
        double scale = batteryIntent.getIntExtra(STRING_BATT_SCALE, -1);
        double level = -1;
        if (rawLevel >= 0 && scale > 0) {
            level = rawLevel / scale;
        }

        boolean isPresent = batteryIntent.getBooleanExtra(STRING_BATT_PRESENT, false);
        String technology = batteryIntent.getStringExtra(STRING_BATT_TECH);
        int voltage = batteryIntent.getIntExtra(STRING_BATT_VOLTAGE, 0);
        int status = batteryIntent.getIntExtra(STRING_BATT_STATUS, 0);
        int connectedTo = batteryIntent.getIntExtra(STRING_BATT_PLUGGED, -1);
        int health = batteryIntent.getIntExtra(STRING_BATT_HEALTH, 0);
        int temperature = batteryIntent.getIntExtra(STRING_BATT_TEMPERATURE, 0);

        return new BatteryStatus(level, scale, isPresent, technology, voltage, status, connectedTo, health, temperature);
    }
}
