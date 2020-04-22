package de.thu.tpro.android4bikes.view.driving;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.camera.NavigationCamera;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.RouteRefresh;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.positiontest.PositionProvider;
import de.thu.tpro.android4bikes.services.GpsLocation;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.view.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Looper.getMainLooper;

public class FragmentDrivingMode extends Fragment implements LocationListener, PermissionsListener, OnNavigationReadyCallback {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String LOG_TAG = "FragmentDrivingMode";
    private static final String TAG = "FAB for Driving Mode";

    TextView txtCurrentSpeed;
    TextView txtAvgSpeed;
    TextView txtSpeedUnit;
    //TODO Delete after testing
    private ViewModelDrivingMode viewModel;
    private View viewDrivingMode;
    private CardView infoIcon;
    OvershootInterpolator interpolator;
    boolean isMenuOpen = false;
    Float translationY = 100f;
    GpsLocation location;
    private FloatingActionButton fab, fab1, fab2, fab3;
    private Date time;
    private List<Position> hazardAlert = new ArrayList<>();
    FloatingActionButton navFab;
    MainActivity parent;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private FragmentDrivingMode.LocationChangeListeningActivityLocationCallback callback = new FragmentDrivingMode.LocationChangeListeningActivityLocationCallback((MainActivity) this.getActivity());
    // Navigation related variables
    private LocationEngine locationEngine;
    private RouteRefresh routeRefresh;
    private boolean isRefreshing = false;
    private DirectionsRoute currentRoute;
    private LatLng lastPos;
    //private List<Position> bikeRacks = new ArrayList<>();
    //private List<Position> bikeTracks = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewDrivingMode = inflater.inflate(R.layout.fragment_driving_mode, container, false);
        txtCurrentSpeed = viewDrivingMode.findViewById(R.id.txtCurrentSpeed);
        txtSpeedUnit = viewDrivingMode.findViewById(R.id.txtSpeedUnit);
        txtAvgSpeed = viewDrivingMode.findViewById(R.id.txtAverageSpeed);
        infoIcon = viewDrivingMode.findViewById(R.id.cardView_Infoicon);


        initCardView();

        locationPermissions();
        viewModel = ViewModelDrivingMode.getInstance();
        txtCurrentSpeed.setText(viewModel.updateSpeed(null) + "");
        txtAvgSpeed.setText(viewModel.updateAverageSpeed(0) + "");

        return viewDrivingMode;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //we need the parent Activity to init our map
        parent = (MainActivity) this.getActivity();
        GlobalContext.setContext(parent.getApplicationContext());

        initNavigation(view, savedInstanceState);

    }

    private void initNavigation(View view, Bundle savedInstanceState) {
        Mapbox.getInstance(parent, getString(R.string.access_token));
        RouteRefresh routeRefresh = new RouteRefresh(getString(R.string.access_token));

        //Bind NavFab
        initNavFab(view);

        //Bind Navigationview
        parent.navigationView = view.findViewById(R.id.navigation_view);
        parent.navigationView.onCreate(savedInstanceState);
        parent.navigationView.initialize(this::onNavigationReady);
    }


    @Override
    public void onNavigationReady(boolean isRunning) {
        parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().setStyle(new Style.Builder().fromUri("mapbox://styles/and4bikes/ck95tpr8r06uj1ipim24tfy6o"));
        parent.navigationView.findViewById(R.id.feedbackFab).setVisibility(View.GONE);
        generateCustomRoute(PositionProvider.getDummyPosition2elements());

    }

    private void initNavFab(View view) {
        navFab = view.findViewById(R.id.start_nav);
        navFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidRoute = currentRoute != null;
                if (isValidRoute) {
                    Log.d("HELLO", "Succeessfullllll ");
                    // Hide the start button
                    navFab.setVisibility(View.INVISIBLE);


                    parent.navigationView.startNavigation(NavigationViewOptions.builder()
                            .directionsRoute(currentRoute)
                            .locationEngine(locationEngine)
                            .navigationListener(new NavigationListener() {
                                @Override
                                public void onCancelNavigation() {
                                    Log.d("HELLO", "OK, switch back to INFO MDOE");
                                    parent.navigationView.stopNavigation();
                                }

                                @Override
                                public void onNavigationFinished() {
                                    Log.d("HELLO", "OK, switch back to INFO MDOE");
                                    parent.navigationView.stopNavigation();

                                }

                                @Override
                                public void onNavigationRunning() {
                                    Log.d("HELLO", String.valueOf(parent.lastSpeed));
                                }
                            })
                            .shouldSimulateRoute(true)
                            .build());

                    parent.navigationView.retrieveNavigationMapboxMap().updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_GPS);

                } else {
                    Log.d("HELLO", "Error current-route null ");
                }
            }
        });
    }

    private void showRoute(LatLng start, LatLng end) {
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(start) // Northeast
                .include(end) // Southwest
                .build();
        parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);
    }

    //Custom Route
    private void generateCustomRoute(List<Position> finegrainedPositions) {
        MapboxMapMatching.builder()
                .accessToken(getString(R.string.access_token))
                .coordinates(convertPositionListToPointList(finegrainedPositions))
                .steps(true)
                .tidy(true)
                .voiceUnits(DirectionsCriteria.METRIC)
                .voiceInstructions(true)
                .bannerInstructions(true)
                .language(Locale.GERMAN)
                .profile(DirectionsCriteria.PROFILE_CYCLING)
                .build()
                .enqueueCall(new Callback<MapMatchingResponse>() {

                    @Override
                    public void onResponse(Call<MapMatchingResponse> call, Response<MapMatchingResponse> response) {
                        if (response.isSuccessful()) {
                            currentRoute = response.body().matchings().get(0).toDirectionRoute();

                            Log.d("HELLO", String.valueOf(currentRoute.distance()));
                            if (currentRoute != null) {
                                Log.d("HELLO", "Route initialized");
                            }
                            //Draw Route on Map
                            parent.navigationView.drawRoute(currentRoute);
                            showRoute(finegrainedPositions.get(0).toMapboxLocation(), finegrainedPositions.get(finegrainedPositions.size() - 1).toMapboxLocation());
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

    private List<com.mapbox.geojson.Point> convertPositionListToPointList(List<Position> finegrainedPositions) {
        List<com.mapbox.geojson.Point> points = new ArrayList<>();
        finegrainedPositions.forEach(position -> points.add(com.mapbox.geojson.Point.fromLngLat(position.getLongitude(), position.getLatitude())));
        return points;
    }

    private List<com.mapbox.geojson.Point> getStartAndEnd(List<Position> finegrainedPositions) {
        List<com.mapbox.geojson.Point> points = new ArrayList<>();
        points.add(com.mapbox.geojson.Point.fromLngLat(finegrainedPositions.get(0).toMapboxLocation().getLongitude(), finegrainedPositions.get(0).toMapboxLocation().getLatitude()));
        points.add(com.mapbox.geojson.Point.fromLngLat(finegrainedPositions.get(finegrainedPositions.size() - 1).toMapboxLocation().getLongitude(), finegrainedPositions.get(finegrainedPositions.size() - 1).toMapboxLocation().getLatitude()));
        return points;
    }


    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(parent)) {

            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(parent, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            // Activate with the LocationComponentActivationOptions object
            parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().getLocationComponent().activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().getLocationComponent().setLocationComponentEnabled(true);

            // Set the component's camera mode
            parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().getLocationComponent().setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().getLocationComponent().setRenderMode(RenderMode.GPS);

            parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().getLocationComponent().zoomWhileTracking(8, 3000, new MapboxMap.CancelableCallback() {
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
            permissionsManager.requestLocationPermissions(parent);
        }
    }

    //Location Stuff--------------------------------------------------------------

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(parent);

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
        Toast.makeText(parent, R.string.user_location_permission_explanation,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(parent, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        parent.navigationView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        parent.navigationView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        parent.navigationView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        parent.navigationView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        parent.navigationView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Prevent leaks
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        parent.navigationView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        parent.navigationView.onLowMemory();
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

        private final WeakReference<MainActivity> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(MainActivity activity) {
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

                activity.lastPos = new LatLng(result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude());
                activity.lastSpeed = location.getSpeed();
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
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }


    }

    /**
     * initiates the top cardView and sets the correct sizes for all elements
     */
    private void initCardView() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);

        int infoIconHeight = calculateSize(size.y, 0.2);
        int currentSpeedHeight = calculateSize(size.y, 0.1);
        int currentSpeedWidth = calculateSize(size.x, 0.2);
        int unitWidth = calculateSize(size.x, 0.4);
        int avgSpeedHeight = calculateSize(size.y, 0.05);
        int avgSpeedWidth = calculateSize(size.x, 0.6);

        setViewDimension(infoIcon, infoIconHeight, infoIconHeight);
        setViewDimension(txtCurrentSpeed, currentSpeedWidth, currentSpeedHeight);
        setViewDimension(txtSpeedUnit, unitWidth, currentSpeedHeight);
        setViewDimension(txtAvgSpeed, avgSpeedWidth, avgSpeedHeight);
    }

    /**
     * sets the size for the current view
     *
     * @param view
     * @param width
     * @param height
     */
    private void setViewDimension(View view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = height;
        params.width = width;
        view.setLayoutParams(params);
    }

    /**
     * calculates the size of a view element
     *
     * @param base
     * @param factor
     * @return base*factor roundet to int
     */
    private int calculateSize(int base, double factor) {
        return (int) Math.rint(base * factor);
    }


    //TODO: handle permission in a central class
    private void locationPermissions() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
    }


    @Override
    public void onLocationChanged(Location location) {
        GpsLocation myLocation = new GpsLocation(location);
        int currentSpeed = viewModel.updateSpeed(myLocation);
        txtCurrentSpeed.setText(currentSpeed + "");
        txtAvgSpeed.setText("Ø " + viewModel.updateAverageSpeed(currentSpeed) + " Km/h");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


}
