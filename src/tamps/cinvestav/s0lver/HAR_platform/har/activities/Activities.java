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
     * @param type The activity type to validate
     * @return true if the activity is recognized, false otherwise
     */
    public static boolean isRecognized(byte type){
        switch (type) {
            case STATIC:
            case WALKING:
            case RUNNING:
                return true;
            default:
                return false;
        }
    }
}
