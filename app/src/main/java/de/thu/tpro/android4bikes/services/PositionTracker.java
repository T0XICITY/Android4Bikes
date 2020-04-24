package de.thu.tpro.android4bikes.services;

import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;

import java.lang.ref.WeakReference;

import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.view.MainActivity;

public class PositionTracker {

    private static Position lastPosition;
    private static double lastSpeed;

    public static Position getLastPosition() {
        return lastPosition;
    }

    public static double getLastSpeed() {
        return lastSpeed;
    }

    public static class LocationChangeListeningActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MainActivity> activityWeakReference;

        public LocationChangeListeningActivityLocationCallback(MainActivity activity) {
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

                lastPosition = new Position(result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude());
                lastSpeed = location.getSpeed();

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
