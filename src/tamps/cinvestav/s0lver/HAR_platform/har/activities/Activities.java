package tamps.cinvestav.s0lver.HAR_platform.har.activities;

/***
 * The activities being recognized by the platform
 */
public class Activities {
    public static final byte UNKNOWN = 0x0;
    public static final byte STATIC = 0x1;
    public static final byte WALKING = 0x2;
    public static final byte RUNNING = 0x3;

    /***
     * Indicates whether the specified activity type is recognized by the platform
     * @param activityType The activity type to validate
     * @return true if the activity is recognized, false otherwise
     */
    public static boolean isRecognized(byte activityType){
        switch (activityType) {
            case STATIC:
            case WALKING:
            case RUNNING:
                return true;
            default:
                return false;
        }
    }


    /***
     * Obtains a String representation of the given activity type
     * @param activityType The activityType recognized by the classifier
     * @return A String representation of the activity type
     */
    public static String getAsString(byte activityType) {
        if (!isRecognized(activityType)) return "Unknown";
        switch (activityType) {
            case STATIC:
                return "Static";
            case WALKING:
                return "Walking";
            case RUNNING:
                return "Running";
            default:
                return "Unknown";
        }
    }
}
