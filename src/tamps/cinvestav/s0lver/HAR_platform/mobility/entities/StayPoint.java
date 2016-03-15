package tamps.cinvestav.s0lver.HAR_platform.mobility.entities;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/***
 * Models a StayPoint, which is a geographical region of a determined size, where the user spents a given amount of time.
 * It is also known as a frequent location, place-point of interest (POI).
 */
public class StayPoint{
    private double latitude;
    private double longitude;
    private Date arrivalTime;
    private Date departureTime;
    private int amountFixesInvolved;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);

    private StayPoint(){}

    public StayPoint(double latitude, double longitude, Date arrivalTime, Date departureTime, int amountFixesInvolved) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.amountFixesInvolved = amountFixesInvolved;
    }

    /***
     * Builds a stay point from the i to j positions of the given list of Locations
     * @param list    An ArrayList of locations
     * @param i       The initial fix to consider
     * @param j       The last fix to consider
     * @return A StayPoint calculated from the specified parameters.
     * @throws RuntimeException If the portion of the list is not valid
     */
    public static StayPoint createStayPoint(ArrayList<Location> list, int i, int j) {
        int amountFixes = j - i + 1;

        if (amountFixes == 0) {
            throw new RuntimeException("List provided is empty");
        }

        double sigmaLatitude = 0.0, sigmaLongitude = 0.0;
        for (int h = i; h <= j; h++) {
            sigmaLatitude += list.get(h).getLatitude();
            sigmaLongitude += list.get(h).getLongitude();
        }

        Date arrivalTime = new Date(list.get(i).getTime());
        Date departureTime = new Date(list.get(j).getTime());

        return createStayPoint(sigmaLatitude, sigmaLongitude, arrivalTime, departureTime, amountFixes);
    }

    /***
     * Builds a StayPoint from the accumulated data
     * @param sigmaLatitude     The sum of the latitude values of a list of locations
     * @param sigmaLongitude    The sum of the longitude values of a list of locations
     * @param arrivalTime       The timestamp of the first Location object
     * @param departureTime     The timestamp of the last location object
     * @param amountFixes       The size of the list of locations
     * @return A StayPoint build from the specified parameters
     */
    public static StayPoint createStayPoint(double sigmaLatitude, double sigmaLongitude, Date arrivalTime, Date departureTime, int amountFixes) {
        StayPoint stayPoint = new StayPoint();
        stayPoint.setLatitude((float) (sigmaLatitude / amountFixes));
        stayPoint.setLongitude((float) (sigmaLongitude / amountFixes));
        stayPoint.setArrivalTime(arrivalTime);
        stayPoint.setDepartureTime(departureTime);
        stayPoint.setAmountFixesInvolved(amountFixes);

        return stayPoint;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public int getAmountFixesInvolved() {
        return amountFixesInvolved;
    }

    public void setAmountFixesInvolved(int amountFixesInvolved) {
        this.amountFixesInvolved = amountFixesInvolved;
    }

    public String toString(){
        return String.format("Lat: %f, Long: %f, Arr: %s, Dep: %s, Amnt: %d", getLatitude(), getLongitude(),
                sdf.format(getArrivalTime()), sdf.format(getDepartureTime()),
                getAmountFixesInvolved());
    }

    public String toCSV(){
        return String.format("%f,%f,%s,%s,%d", getLatitude(), getLongitude(),
                sdf.format(getArrivalTime()), sdf.format(getDepartureTime()),
                getAmountFixesInvolved());
    }

    /***
     * Converts the StayPoint to a Location object.
     * @return A Location instance geographically equivalent to the StayPoint
     * @see Location
     */
    public Location convertStayPointToLocation() {
        Location location = new Location("Custom");
        location.setLatitude(this.getLatitude());
        location.setLongitude(this.getLongitude());
        location.setTime(this.getArrivalTime().getTime());

        return location;
    }

    @Override
    public boolean equals(Object stayPoint) {
        if (!(stayPoint instanceof StayPoint)) {
            throw new RuntimeException("I can compare only against other StayPoint dude");
        }else{
            StayPoint theStayPoint = (StayPoint) stayPoint;
            if (this.getLatitude() != theStayPoint.getLatitude()) return false;
            if (this.getLongitude() != theStayPoint.getLongitude()) return false;
            if (this.getArrivalTime().getTime() != theStayPoint.getArrivalTime().getTime()) return false;
            if (this.getDepartureTime().getTime() != theStayPoint.getDepartureTime().getTime()) return false;
        }
        return true;
    }
}