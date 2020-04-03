package de.thu.tpro.android4bikes.view.driving;

import android.util.Log;

import de.thu.tpro.android4bikes.services.GpsLocation;

public class ViewModelDrivingMode {
    private static ViewModelDrivingMode singleton;
    private static final String LOG_TAG = "ViewModelDrivingMode";

    public static ViewModelDrivingMode getInstance(){
        if (singleton == null){
            singleton = new ViewModelDrivingMode();
        }
        return singleton;
    }

    private int counter;
    private long accumulatedSpeed;
    private float currSpeed;

    private ViewModelDrivingMode(){
        accumulatedSpeed = 0;
        counter = 0;
        currSpeed = 0f;
    }
    public int updateAverageSpeed(int currentSpeed){
        accumulatedSpeed += currentSpeed;
        if (counter == 0)return 0;
        return (int) accumulatedSpeed/ counter;
    }

    /**
     * Display current Speed into textViews
     *
     * @param location New Location set in onLocationChanged(...)
     */
    public int updateSpeed(GpsLocation location) {
        counter++;
        String loc = "";
        if (location != null) {
            currSpeed = location.getSpeed();
            loc = "Latitude " + location.getLatitude() +
                    "\nLongitude " + location.getLongitude();
        }
        //Log for
        Log.d(LOG_TAG + " Position",loc);
        return Math.round(currSpeed);

    }
}
