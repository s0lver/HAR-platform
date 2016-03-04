package tamps.cinvestav.s0lver.HAR_platform.mobility.staypointdetector.entities;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StayPoint{
    private float latitude;
    private float longitude;
    private Date arrivalTime;
    private Date departureTime;
    private int amountFixesInvolved;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);

    private StayPoint(){}

    public StayPoint(float latitude, float longitude, Date arrivalTime, Date departureTime, int amountFixesInvolved) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.amountFixesInvolved = amountFixesInvolved;
    }

    public static StayPoint createStayPoint(ArrayList<Location> list, int i, int j) {
        int sizeOfListPortion = j - i + 1;

        if (sizeOfListPortion == 0) {
            throw new RuntimeException("List provided is empty");
        }

        double sumLat = 0.0, sumLng = 0.0;
        for (int h = i; h <= j; h++) {
            sumLat += list.get(h).getLatitude();
            sumLng += list.get(h).getLongitude();
        }

        StayPoint pointOfInterest = new StayPoint();
        pointOfInterest.setLatitude((float) (sumLat / sizeOfListPortion));
        pointOfInterest.setLongitude((float) (sumLng / sizeOfListPortion));
        pointOfInterest.setArrivalTime(new Date(list.get(i).getTime()));
        pointOfInterest.setDepartureTime(new Date(list.get(j).getTime()));
        pointOfInterest.setAmountFixesInvolved(sizeOfListPortion);

        return pointOfInterest;
    }

    public static StayPoint createStayPoint(double sigmaLatitude, double sigmaLongitude, Date arrivalTime, Date departureTime, int amountFixes) {
        StayPoint stayPoint = new StayPoint();
        stayPoint.setAmountFixesInvolved(amountFixes);
        stayPoint.setLatitude((float) (sigmaLatitude / amountFixes));
        stayPoint.setLongitude((float) (sigmaLongitude / amountFixes));
        stayPoint.setArrivalTime(arrivalTime);
        stayPoint.setDepartureTime(departureTime);

        return stayPoint;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
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
}