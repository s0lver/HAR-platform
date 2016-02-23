package tamps.cinvestav.s0lver.HAR_Entities.activities;

public class Activities {
    public static final byte STATIC = 0x0;
    public static final byte WALKING = 0x1;
    public static final byte RUNNING = 0x2;

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
