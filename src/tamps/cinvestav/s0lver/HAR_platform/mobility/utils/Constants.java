package tamps.cinvestav.s0lver.HAR_platform.mobility.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/***
 * Constants employed overall the mobility hub
 */
public class Constants {

    /***
     * I.e., one second in milliseconds
     */
    public static final long ONE_SECOND = 1000;

    /***
     * One minute in milliseconds
     */
    public static final long ONE_MINUTE = ONE_SECOND * 60;

    /***
     * The minimum distance (size) of the geographical area to be considered a StayPoint
     */
    public static final double MIN_DISTANCE_PARAMETER = 500;

    /***
     * Standard date format for output of data (for instance, in file names).
     */
    public static final String FILE_NAMES_DATE_FORMAT = "dd-MM-yyyy_HH-mm-ss";

    /***
     * Date format object for formatting dates and keeping them uniform across the system
     */
    public static final SimpleDateFormat FILE_NAMES_SIMPLE_DATE_FORMAT = new SimpleDateFormat(FILE_NAMES_DATE_FORMAT, Locale.ENGLISH);

    /***
     * Standard date format used in records file
     */
    private static final String RECORDS_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    /***
     * Date format object for formatting dates existing in files content
     */
    public static final SimpleDateFormat RECORDS_SIMPLE_DATE_FORMAT = new SimpleDateFormat(RECORDS_DATE_FORMAT, Locale.ENGLISH);
}
