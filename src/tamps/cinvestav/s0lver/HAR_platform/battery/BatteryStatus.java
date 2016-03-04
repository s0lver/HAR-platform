package tamps.cinvestav.s0lver.HAR_platform.battery;

import android.os.BatteryManager;

/***
 * Models the status of the smartphone's battery
 */
public class BatteryStatus{
    private double level;
    private double scale;
    private boolean isPresent;
    private String technology;
    private int voltage;
    private int status;
    private int connectedTo;
    private int health;
    private int temperature;

    public BatteryStatus(double level, double scale, boolean isPresent, String technology, int voltage, int status, int connectedTo, int health, int temperature) {
        this.level = level;
        this.scale = scale;
        this.isPresent = isPresent;
        this.technology = technology;
        this.voltage = voltage;
        this.status = status;
        this.connectedTo = connectedTo;
        this.health = health;
        this.temperature = temperature;
    }

    public double getLevel() {
        return level;
    }

    public double getScale() {
        return scale;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public String getIsBatteryPresentAsString() {
        return isPresent() ? "Yes" : "No";
    }

    public String getTechnology() {
        return technology;
    }

    public int getVoltage() {
        return voltage;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusAsString() {
        switch (getStatus()) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "discharging";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "full";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "not-charging";
            //case BatteryManager.BATTERY_STATUS_UNKNOWN:
            default:
                return "unknown";
        }
    }

    public int getConnectedTo() {
        return connectedTo;
    }

    public String getConnectedToAsString() {
        switch (getConnectedTo()) {
            case BatteryManager.BATTERY_PLUGGED_USB:
                return "USB";
            case BatteryManager.BATTERY_PLUGGED_AC:
                return "AC";
            default:
                return "unplugged";
        }
    }

    public int getHealth() {
        return health;
    }

    public String getHealthAsString() {
        switch (getHealth()) {
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "dead";
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "good";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "over-voltage";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "over-heat";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return "unspecified-failure";
            //case BatteryManager.BATTERY_HEALTH_UNKNOWN:
            default:
                return "unknown";
        }
    }

    public int getTemperature() {
        return temperature;
    }

    public String toCSV() {

        return String.valueOf(getLevel()) +
                "," +
                getVoltage() +
                "," +
                getStatusAsString() +
                "," +
                getTemperature() +
                "," +
                getConnectedToAsString();
    }
}
