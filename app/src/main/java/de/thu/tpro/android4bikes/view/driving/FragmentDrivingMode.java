package de.thu.tpro.android4bikes.view.driving;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.camera.NavigationCamera;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.navigation.RouteRefresh;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.openWeather.OpenWeatherObject;
import de.thu.tpro.android4bikes.services.GpsLocation;
import de.thu.tpro.android4bikes.services.PositionTracker;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.util.Navigation.DirectionRouteHelper;
import de.thu.tpro.android4bikes.view.MainActivity;
import de.thu.tpro.android4bikes.viewmodel.ViewModelTrack;
import de.thu.tpro.android4bikes.viewmodel.ViewModelWeather;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDrivingMode extends Fragment implements PermissionsListener,
        OnNavigationReadyCallback, Observer<OpenWeatherObject> {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String LOG_TAG = "FragmentDrivingMode";
    private static final String TAG = "FAB for Driving Mode";

    TextView txtCurrentSpeed;
    TextView txtAvgSpeed;
    TextView txtSpeedUnit;
    //TODO Delete after testing

    private ViewModelDrivingMode viewModel;
    private ViewModelWeather vmWeather;

    private View viewDrivingMode;
    private CardView infoIcon;
    OvershootInterpolator interpolator;
    boolean isMenuOpen = false;
    Float translationY = 100f;
    GpsLocation location;
    private FloatingActionButton fab_weather;
    private Date time;

    MainActivity parent;
    private PermissionsManager permissionsManager;
    // Navigation related variables
    private DirectionsRoute reroute;

    //ViewModels
    ViewModelTrack vm_track;

    //TODO refactor
    private Track track_for_navigation;

    private List<Position> registeredHazardPositions;

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
        fab_weather = viewDrivingMode.findViewById(R.id.weatherFAB);

        vmWeather = new ViewModelProvider(requireActivity()).get(ViewModelWeather.class);
        vmWeather.getCurrentWeather().observe(getViewLifecycleOwner(), this);

        registeredHazardPositions = new LinkedList<>();

        initCardView();

        //locationPermissions();
        viewModel = ViewModelDrivingMode.getInstance();
        txtCurrentSpeed.setText(viewModel.updateSpeed(null) + "");
        txtAvgSpeed.setText(viewModel.updateAverageSpeed(0) + "");
        return viewDrivingMode;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm_track = new ViewModelProvider(requireActivity()).get(ViewModelTrack.class);

        //we need the parent Activity to init our map
        parent = (MainActivity) this.getActivity();
        GlobalContext.setContext(parent.getApplicationContext());

        initNavigation(view, savedInstanceState);
    }

    @Override
    public void onChanged(OpenWeatherObject weatherObject) {
        String weatherIconName = vmWeather.getCurrentWeather().getValue().getForecastList().get(0)
                .getWeather().get(0).getIcon().substring(0,2);

        Log.d(LOG_TAG, "Weather: "+weatherIconName+", "+weatherObject.getCity().getName());

        int weatherDrawableId = 0;
        switch (weatherIconName) {
            case "01": // sunny
                weatherDrawableId = R.drawable.weather_clear_sky;
                break;
            case "02":
                weatherDrawableId = R.drawable.weather_few_clouds;
                break;
            case "03":
                weatherDrawableId = R.drawable.weather_scattered_clouds;
                break;
            case "04":
                weatherDrawableId = R.drawable.weather_broken_cloud;
                break;
            case "09":
            case "10":
                weatherDrawableId = R.drawable.weather_rain;
                break;
            case "11":
                weatherDrawableId = R.drawable.weather_thunderstom;
                break;
            case "13":
                weatherDrawableId = R.drawable.weather_snow;
                break;
            case "50":
                weatherDrawableId = R.drawable.weather_mist;
                break;
        }
        fab_weather.setImageDrawable(getResources().getDrawable(weatherDrawableId, parent.getTheme()));
    }

    private void initNavigation(View view, Bundle savedInstanceState) {
        Mapbox.getInstance(parent, getString(R.string.access_token));
        RouteRefresh routeRefresh = new RouteRefresh(getString(R.string.access_token));

        //Bind Navigationview
        parent.navigationView = view.findViewById(R.id.navigation_view);
        parent.navigationView.onCreate(savedInstanceState);
        parent.navigationView.initialize(this::onNavigationReady);
    }

    @Override
    public void onNavigationReady(boolean isRunning) {
        parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().setStyle(new Style.Builder().fromUri("mapbox://styles/and4bikes/ck95tpr8r06uj1ipim24tfy6o"), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                if (PositionTracker.getLastPosition() != null) {
                    parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().setCameraPosition(new CameraPosition.Builder()
                            .target(PositionTracker.getLastPosition().toMapboxLocation())
                            .zoom(15)
                            .build());
                }
                parent.navigationView.findViewById(R.id.feedbackFab).setVisibility(View.GONE);
                parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
                        //Implement Code
                        return false;
                    }
                });

                //Get Track from Viewmodel
                //track =
                //Start Navigation
                track_for_navigation = vm_track.getNavigationTrack().getValue();
                if (track_for_navigation != null) {
                    startNavigation();
                } else {
                    //start free mode
                /*TrackRecorder trackRecorder = new TrackRecorder();
                trackRecorder.start();
                //Abfage User input
                trackRecorder.stop(trackname,...);
                //todo: new Longclicklistener
                registeredHazardPositions.add(PositionTracker.getLastPosition());
                Snackbar.make(getView(), R.string.register_hazard, Snackbar.LENGTH_LONG).show();
                Log.d("addHazard", "List: " + registeredHazardPositions);
                return true;
                */
                }
            }
        });
    }

    private void initspeedFAB(View view) {
        //TODO
    }

    private void startNavigation() {
        boolean isValidRoute = track_for_navigation.getRoute() != null;
        if (isValidRoute) {
            parent.navigationView.startNavigation(NavigationViewOptions.builder()
                    .directionsRoute(track_for_navigation.getRoute())
                    .locationEngine(parent.locationEngine)
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
                            Log.d("HELLO", String.valueOf(PositionTracker.getLastSpeed()));

                        }
                    })
                    .routeListener(new RouteListener() {
                        @Override
                        public boolean allowRerouteFrom(com.mapbox.geojson.Point offRoutePoint) {
                            Log.d("HELLO", "Rerouting");
                            // Fetch new route with MapboxMapMatching
                            List<com.mapbox.geojson.Point> points = new ArrayList<>();
                            points.add(offRoutePoint);
                            points.add(com.mapbox.geojson.Point.fromLngLat(track_for_navigation.getStartPosition().getLatitude(), track_for_navigation.getStartPosition().getLongitude()));

                            NavigationRoute.builder(parent)
                                    .accessToken(getString(R.string.access_token))
                                    .origin(points.get(0))
                                    .destination(points.get(1))
                                    .addWaypointIndices(0, points.size() - 1)
                                    .profile(DirectionsCriteria.PROFILE_CYCLING)
                                    .build()
                                    .getRoute(new Callback<DirectionsResponse>() {
                                        @Override
                                        public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                                            if (response.isSuccessful()) {
                                                Log.d("HELLO", "CODE: " + response.code() + " with Message:\n" + response.message());
                                                Log.d("HELLO", "SIZE: " + response.body().routes().size());
                                                Log.d("HELLO", "Body Message: " + response.body().message());
                                                if (response.body().routes().size() > 0) {
                                                    reroute = response.body().routes().get(0);

                                                    Log.d("HELLO", String.valueOf(reroute.distance()));
                                                    if (reroute != null) {
                                                        Log.d("HELLO", "reroute initialized");
                                                    }
                                                    track_for_navigation.setRoute(DirectionRouteHelper.appendRoute(reroute, track_for_navigation.getRoute()));
                                                    parent.navigationView.startNavigation(NavigationViewOptions.builder()
                                                            .directionsRoute(track_for_navigation.getRoute())
                                                            .locationEngine(parent.locationEngine)
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
                                                                    Log.d("HELLO", String.valueOf(PositionTracker.getLastSpeed()));
                                                                    //fab.setImageBitmap(textAsBitmap("OK", 40, Color.WHITE));

                                                                }
                                                            }).build());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                                        }
                                    });


                            // Ignore internal routing, allowing MapboxMapMatching call
                            Log.d("HELLO", "Reroute ended");
                            return false;
                        }

                        @Override
                        public void onOffRoute(com.mapbox.geojson.Point offRoutePoint) {
                            Log.d("HELLO", "onOffRoute called");
                        }

                        @Override
                        public void onRerouteAlong(DirectionsRoute directionsRoute) {
                            Log.d("HELLO", "onRerouteAlong called");
                        }

                        @Override
                        public void onFailedReroute(String errorMessage) {
                            Log.d("HELLO", "onFailedReroute called");
                        }

                        @Override
                        public void onArrival() {

                        }
                    })
                    //.shouldSimulateRoute(true)
                    .build());

            parent.navigationView.retrieveNavigationMapboxMap().updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_GPS);

        } else {
            Log.d("HELLO", "Error current-route null ");
        }
    }


    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(parent)) {

            //Check if GPS is enabled on device

            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(parent, loadedMapStyle)
                            .useDefaultLocationEngine(true)
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
            if (parent.locationEngine == null) {
                parent.initLocationEngine();
            }
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(parent);
        }
    }

    //Location Stuff--------------------------------------------------------------

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
        if (parent.navigationView!=null) {
            parent.navigationView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (parent.navigationView!=null) {
            parent.navigationView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (parent.navigationView!=null) {
            parent.navigationView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (parent.navigationView!=null) {
            parent.navigationView.onStop();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (parent.navigationView!=null) {
            parent.navigationView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (parent.navigationView!=null){
            parent.navigationView.onDestroy();
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (parent.navigationView!=null) {
            parent.navigationView.onLowMemory();
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

    public List<Position> getRegisteredHazardPositions() {
        return registeredHazardPositions;
    }


    /*//TODO: handle permission in a central class
    private void locationPermissions() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
    }*/

    /*
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

    }*/


}
