package tamps.cinvestav.s0lver.HAR_platform.mobility.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/***
 * Constants employed overall the mobility hub
 */
public class Constants {
    /***
     * The minimum distance (size) of the geographical area to be considered a StayPoint
     */
    public static final double MIN_DISTANCE_PARAMETER = 500;

    /***
     * Standard date format for output of data (for instance, in file names).
     */
    public static final String DATE_FORMAT = "dd-MM-yyyy_HH-mm-ss";

    /***
     * Date format object for formating dates and keeping them uniform across the system
     */
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
}
