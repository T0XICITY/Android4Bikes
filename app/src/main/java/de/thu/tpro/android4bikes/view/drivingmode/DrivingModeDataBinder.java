package de.thu.tpro.android4bikes.view.drivingmode;

import java.util.Timer;
import java.util.TimerTask;

import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.services.PositionTracker;

public class DrivingModeDataBinder {
    private static DrivingModeDataBinder singleton;
    private static final String LOG_TAG = "ViewModelDrivingMode";

    public static DrivingModeDataBinder getInstance() {
        if (singleton == null) {
            singleton = new DrivingModeDataBinder();
        }
        return singleton;
    }

    private int counter;
    private Position lastPosition;
    private long accumulatedSpeed;
    private float currSpeed;

    private DrivingModeDataBinder() {
        accumulatedSpeed = 0;
        counter = 0;
        currSpeed = 0f;
    }

    /**
     * updates the average speed while driving
     * @param currentSpeed
     * @return average speed
     */
    public float updateAverageSpeed(int currentSpeed) {
        accumulatedSpeed += currentSpeed;
        if (counter == 0) return 0;
        return accumulatedSpeed / counter;
    }

    /**
     * update current speed and increase counter
     */
    public int updateSpeed() {
        counter++;
        currSpeed = PositionTracker.getLastSpeed();
        return Math.round(currSpeed);
    }

    /**
     * schedules a timer to update the speed
     * @param interval amount of milliseconds between every action
     * @param task action to execute after interval has passed
     */
    public void scheduleSpeedCalculationTimer(int interval, TimerTask task) {
        Timer timer  = new Timer();
        timer.schedule(task, 0, interval);
    }
}