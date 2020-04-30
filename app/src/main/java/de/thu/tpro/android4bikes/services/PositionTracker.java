package de.thu.tpro.android4bikes.services;

import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.view.MainActivity;

public class PositionTracker {

    private static Map<String, Object> map_position_speed;
    public static Position getLastPosition() {
        if (map_position_speed.get(CONSTANTS.POSITION.toText()) == null) {
            map_position_speed.put(CONSTANTS.POSITION.toText(), new Position());
        }

        return (Position) map_position_speed.get(CONSTANTS.POSITION.toText());
    }

    public static double getLastSpeed() {
        if (map_position_speed.get(CONSTANTS.SPEED.toText()) == null) {
            map_position_speed.put(CONSTANTS.SPEED.toText(), 0);
        }

        return (double) map_position_speed.get(CONSTANTS.POSITION.toText());
    }

    public enum CONSTANTS {
        POSITION("position"),
        SPEED("speed");

        private String name;

        CONSTANTS(String type) {
            this.name = type;
        }

        public String toText() {
            return name;
        }
    }

    public static class LocationChangeListeningActivityLocationCallback extends Observable
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MainActivity> activityWeakReference;

        public LocationChangeListeningActivityLocationCallback(MainActivity activity) {
            if (map_position_speed == null) {
                map_position_speed = new HashMap<>();
            }

            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            MainActivity activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }


                map_position_speed.put(CONSTANTS.POSITION.toText(), new Position(result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude()));
                map_position_speed.put(CONSTANTS.SPEED.toText(), location.getSpeed());
                setChanged();
                notifyObservers(map_position_speed);

                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.navigationView != null && activity.navigationView.retrieveNavigationMapboxMap() != null) {
                    if (activity.navigationView.retrieveNavigationMapboxMap().retrieveMap() != null && result.getLastLocation() != null) {
                        activity.navigationView.retrieveNavigationMapboxMap().retrieveMap().getLocationComponent().forceLocationUpdate(result.getLastLocation());
                    }
                }
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can't be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
