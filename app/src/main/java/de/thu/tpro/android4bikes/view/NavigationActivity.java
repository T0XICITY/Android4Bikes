package de.thu.tpro.android4bikes.view;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.matching.v5.MapboxMapMatching;
import com.mapbox.api.matching.v5.models.MapMatchingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.RouteRefresh;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.positiontest.PositionProvider;
import de.thu.tpro.android4bikes.util.GlobalContext;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationActivity extends AppCompatActivity implements PermissionsListener, OnNavigationReadyCallback {
    FloatingActionButton navFab;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private NavigationActivity.LocationChangeListeningActivityLocationCallback callback = new NavigationActivity.LocationChangeListeningActivityLocationCallback(this);
    // Navigation related variables
    private LocationEngine locationEngine;
    private RouteRefresh routeRefresh;
    private boolean isRefreshing = false;
    private DirectionsRoute currentRoute;
    private NavigationView navigationView;
    private LatLng lastPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        GlobalContext.setContext(this.getApplicationContext());
        setContentView(R.layout.activity_navigation);


        routeRefresh = new RouteRefresh(getString(R.string.access_token));

        //Bind NavFab
        initNavFab();

        //Bind Navigationview
        navigationView = findViewById(R.id.navigation_view);
        navigationView.onCreate(savedInstanceState);
        navigationView.initialize(this::onNavigationReady);

    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        navigationView.retrieveNavigationMapboxMap().retrieveMap().setStyle(Style.DARK);
        generateCustomRoute(PositionProvider.getDummyPosition2elements());

    }

    private void initNavFab() {
        navFab = findViewById(R.id.start_nav);
        navFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidRoute = currentRoute != null;
                if (isValidRoute) {
                    Log.d("HELLO", "Succeessfullllll ");
                    // Hide the start button
                    navFab.setVisibility(View.INVISIBLE);


                    navigationView.startNavigation(NavigationViewOptions.builder()
                            .directionsRoute(currentRoute)
                            .locationEngine(locationEngine)
                            //.bannerInstructionsListener(instructions -> instructions)
                            //.progressChangeListener((location, routeProgress) -> Log.d("HELLO",
                            //"Location: "+location.getLatitude()+" "+location.getLongitude()
                            //+"Progress: "+routeProgress.distanceRemaining()))
                            //.shouldSimulateRoute(true)
                            .build());


                } else {
                    Log.d("HELLO", "Error current-route null ");
                }
            }
        });
    }

    private void flytoStart(LatLng startpoint) {
        navigationView.retrieveNavigationMapboxMap().retrieveMap().animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(startpoint)
                .zoom(15)
                .bearing(0)
                .build()), 3000);
    }

    //Custom Route
    private void generateCustomRoute(List<Position> finegrainedPositions) {
        MapboxMapMatching.builder()
                .accessToken(getString(R.string.access_token))
                .coordinates(convertPositionListToPointList(finegrainedPositions))
                .steps(true)
                .tidy(true)
                .voiceInstructions(true)
                .bannerInstructions(true)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .build()
                .enqueueCall(new Callback<MapMatchingResponse>() {

                    @Override
                    public void onResponse(Call<MapMatchingResponse> call, Response<MapMatchingResponse> response) {
                        if (response.isSuccessful()) {
                            currentRoute = response.body().matchings().get(0).toDirectionRoute();
                            //navigation.startNavigation(route);
                            // Draw the route on the map
                            //navigationMapRoute = new NavigationMapRoute(null, navigationView.getRootView().findViewById(R.), mapboxMap, R.style.NavigationMapRoute);
                            Log.d("HELLO", String.valueOf(currentRoute.distance()));
                            if (currentRoute != null) {
                                Log.d("HELLO", "Route initialized");
                            }
                            //Draw Route on Map
                            navigationView.drawRoute(currentRoute);
                            flytoStart(finegrainedPositions.get(0).toMapboxLocation());
                            navFab.setVisibility(View.VISIBLE);
                        } else {
                            Log.d("HELLO", "Response empty");
                        }
                    }

                    @Override
                    public void onFailure(Call<MapMatchingResponse> call, Throwable throwable) {

                    }
                });
    }

    private List<Point> convertPositionListToPointList(List<Position> finegrainedPositions) {
        List<Point> points = new ArrayList<>();
        finegrainedPositions.forEach(position -> points.add(Point.fromLngLat(position.getLongitude(), position.getLatitude())));
        return points;
    }

    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            // Activate with the LocationComponentActivationOptions object
            navigationView.retrieveNavigationMapboxMap().retrieveMap().getLocationComponent().activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            navigationView.retrieveNavigationMapboxMap().retrieveMap().getLocationComponent().setLocationComponentEnabled(true);

            // Set the component's camera mode
            navigationView.retrieveNavigationMapboxMap().retrieveMap().getLocationComponent().setCameraMode(CameraMode.TRACKING_GPS);

            // Set the component's render mode
            navigationView.retrieveNavigationMapboxMap().retrieveMap().getLocationComponent().setRenderMode(RenderMode.GPS);

            navigationView.retrieveNavigationMapboxMap().retrieveMap().getLocationComponent().zoomWhileTracking(15, 3000, new MapboxMap.CancelableCallback() {
                @Override
                public void onCancel() {
                }

                @Override
                public void onFinish() {
                }
            });

            initLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    //Location Stuff--------------------------------------------------------------

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(750)
                .setFastestInterval(750)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            navigationView.retrieveNavigationMapboxMap().retrieveMap().getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
        }
    }

    private static class MyBroadcastReceiver extends BroadcastReceiver {
        private final WeakReference<MapboxNavigation> weakNavigation;

        MyBroadcastReceiver(MapboxNavigation navigation) {
            this.weakNavigation = new WeakReference<>(navigation);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MapboxNavigation navigation = weakNavigation.get();
            navigation.stopNavigation();
        }
    }

    private static class LocationChangeListeningActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<NavigationActivity> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(NavigationActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            NavigationActivity activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                activity.lastPos = new LatLng(result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude());
                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.navigationView.retrieveNavigationMapboxMap().retrieveMap() != null && result.getLastLocation() != null) {
                    activity.navigationView.retrieveNavigationMapboxMap().retrieveMap().getLocationComponent().forceLocationUpdate(result.getLastLocation());
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
            NavigationActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


}

