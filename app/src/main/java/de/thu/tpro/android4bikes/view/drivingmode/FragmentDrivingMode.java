package de.thu.tpro.android4bikes.view.drivingmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.data.openWeather.OpenWeatherObject;
import de.thu.tpro.android4bikes.services.PositionTracker;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.util.Navigation.DirectionRouteHelper;
import de.thu.tpro.android4bikes.view.MainActivity;
import de.thu.tpro.android4bikes.viewmodel.ViewModelTrack;
import de.thu.tpro.android4bikes.viewmodel.ViewModelWeather;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDrivingMode extends Fragment implements OnNavigationReadyCallback, Observer<OpenWeatherObject>, java.util.Observer, RouteListener, NavigationListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String LOG_TAG = "FragmentDrivingMode";
    private static final String TAG = "FAB for Driving Mode";
    private static final int VELOCITY_UPDATE_INTERVAL = 250;
    MainActivity parent;
    //ViewModels
    ViewModelTrack vm_track;
    private DrivingModeDataBinder dataBinder;
    private ViewModelWeather vmWeather;
    private View viewDrivingMode;
    private CardView infoIcon;
    private FloatingActionButton fab_weather;
    private ExtendedFloatingActionButton fab_velocity;
    private Timer updateTimer;
    // Navigation related variables
    private DirectionsRoute reroute;
    private boolean reroute_user;

    //TODO refactor
    private Track track_for_navigation;

    private List<Position> registeredHazardPositions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewDrivingMode = inflater.inflate(R.layout.fragment_driving_mode, container, false);

        initweatherFab();

        vmWeather = new ViewModelProvider(requireActivity()).get(ViewModelWeather.class);
        vm_track = new ViewModelProvider(requireActivity()).get(ViewModelTrack.class);

        dataBinder = DrivingModeDataBinder.getInstance();

        registeredHazardPositions = new LinkedList<>();

        return viewDrivingMode;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vmWeather.getCurrentWeather().observe(getViewLifecycleOwner(), this);

        //we need the parent Activity to init our map
        parent = (MainActivity) this.getActivity();
        GlobalContext.setContext(parent.getApplicationContext());

        initNavigation(view, savedInstanceState);
        initVelocityFAB();
        track_for_navigation = vm_track.getNavigationTrack().getValue();
    }

    @Override
    public void onChanged(OpenWeatherObject weatherObject) {
        String weatherIconName = vmWeather.getCurrentWeather().getValue().getForecastList().get(0)
                .getWeather().get(0).getIcon().substring(0, 2);

        //Log.d(LOG_TAG, "Weather: " + weatherIconName + ", " + weatherObject.getCity().getName());

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
        //Log.d("HalloWeltAUA", "NavigationReady");
        //parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().setStyle(Style.MAPBOX_STREETS, style -> {
        //Log.d("HalloWeltAUA", "style loaded");
        parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().getUiSettings().setAllGesturesEnabled(false);
        if (PositionTracker.getLastPosition().isValid()) {
            parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().setCameraPosition(new CameraPosition.Builder()
                    .target(PositionTracker.getLastPosition().toMapboxLocation())
                    .zoom(15)
                    .build());
            FragmentDrivingMode.this.startNavigation();
        } else {
            Position germany_center = new Position(51.163361111111, 10.447683333333);
            parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().setCameraPosition(new CameraPosition.Builder()
                    .target(germany_center.toMapboxLocation())
                    .zoom(4)
                    .build());
            //Observe Position
            PositionTracker.LocationChangeListeningActivityLocationCallback.getInstance(parent).addObserver(FragmentDrivingMode.this);
        }
        parent.navigationView.findViewById(R.id.feedbackFab).setVisibility(View.GONE);
        parent.navigationView.findViewById(R.id.soundFab).setVisibility(View.GONE);
        parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().addOnMapLongClickListener(click -> {
            registeredHazardPositions.add(PositionTracker.getLastPosition());
            Snackbar.make(FragmentDrivingMode.this.getView(), R.string.register_hazard, Snackbar.LENGTH_LONG).show();
            //Log.d("addHazard", "List: " + registeredHazardPositions);
            return true;
        });
    }

    private void initVelocityFAB() {
        fab_velocity = viewDrivingMode.findViewById(R.id.velocityFAB);
        // schedule a timer to update velocity periodically
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // update TextViews from UI Thread only
                parent.runOnUiThread(() -> {
                    int currentVelocity = Math.round(PositionTracker.getLastSpeed());
                    String velocityText = String.format(getString(R.string.speed), currentVelocity);
                    fab_velocity.setText(velocityText);
                });
            }
        }, 0, VELOCITY_UPDATE_INTERVAL);
    }

    private void initweatherFab() {
        fab_weather = viewDrivingMode.findViewById(R.id.weatherFAB);
    }

    public void cancelUpdateTimer() {
        updateTimer.cancel();
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof PositionTracker.LocationChangeListeningActivityLocationCallback) {
            if (o instanceof Map && !((Map) o).keySet().isEmpty()) {
                Map map = (Map) o;
                if (map.get(PositionTracker.CONSTANTS.POSITION.toText()) != null) {
                    Position last_position = (Position) map.get(PositionTracker.CONSTANTS.POSITION.toText());
                    if (last_position.isValid()) {
                        parent.navigationView.retrieveNavigationMapboxMap().retrieveMap().animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(last_position.toMapboxLocation())
                                .zoom(15)
                                .bearing(0)
                                .build()), 3000);
                        startNavigation();
                        observable.deleteObserver(this);
                    }

                }

            }
        }
    }

    private void startNavigation() {
        boolean isValidRoute = track_for_navigation.getRoute() != null;
        if (isValidRoute) {
            parent.navigationView.drawRoute(track_for_navigation.getRoute());
            parent.navigationView.startNavigation(NavigationViewOptions.builder()
                    .directionsRoute(track_for_navigation.getRoute())
                    .locationEngine(parent.locationEngine)
                    .navigationListener(new NavigationListener() {
                        @Override
                        public void onCancelNavigation() {
                            parent.navigationView.stopNavigation();
                        }

                        @Override
                        public void onNavigationFinished() {
                            parent.navigationView.stopNavigation();
                        }

                        @Override
                        public void onNavigationRunning() {
                            //Log.d("HELLO", String.valueOf(PositionTracker.getLastSpeed()));

                        }
                    })
                    .routeListener(this)
                    //.shouldSimulateRoute(true)
                    .build());

            parent.navigationView.retrieveNavigationMapboxMap().updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_GPS);

        } else {
            //Log.d("HELLO", "Error current-route null ");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (parent.navigationView != null) {
            parent.navigationView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (parent.navigationView != null) {
            parent.navigationView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (parent.navigationView != null) {
            parent.navigationView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (parent.navigationView != null) {
            parent.navigationView.onStop();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (parent.navigationView != null) {
            parent.navigationView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (parent.navigationView != null) {
            parent.navigationView.onDestroy();
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (parent.navigationView != null) {
            parent.navigationView.onLowMemory();
        }
    }

    //Route listener
    @Override
    public boolean allowRerouteFrom(Point offRoutePoint) {
        //Log.d("HELLO", "Rerouting");
        if (!reroute_user) {
            reroute_user = true;
            // Fetch new route with MapboxMapMatching
            List<com.mapbox.geojson.Point> points = new ArrayList<>();
            points.add(offRoutePoint);
            //Todo get next Point based on Track
            points.add(track_for_navigation.getStartPosition().getAsPoint());

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
                                //Log.d("HELLO", "CODE: " + response.code() + " with Message:\n" + response.message());
                                //Log.d("HELLO", "SIZE: " + response.body().routes().size());
                                //Log.d("HELLO", "Body Message: " + response.body().message());
                                if (response.body().routes().size() > 0) {
                                    reroute = response.body().routes().get(0);

                                    //Log.d("HELLO", String.valueOf(reroute.distance()));
                                    if (reroute != null) {
                                        //Log.d("HELLO", "reroute initialized");
                                    }
                                    track_for_navigation.setRoute(DirectionRouteHelper.appendRoute(reroute, track_for_navigation.getRoute()));
                                    parent.navigationView.startNavigation(NavigationViewOptions.builder()
                                            .directionsRoute(track_for_navigation.getRoute())
                                            .locationEngine(parent.locationEngine)
                                            .navigationListener(FragmentDrivingMode.this)
                                            .routeListener(FragmentDrivingMode.this)
                                            .build());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                        }
                    });
            // Ignore internal routing, allowing MapboxMapMatching call
            //Log.d("HELLO", "Reroute ended");
            return false;
        }
        return false;
    }

    @Override
    public void onOffRoute(Point offRoutePoint) {

    }

    @Override
    public void onRerouteAlong(DirectionsRoute directionsRoute) {

    }


    @Override
    public void onFailedReroute(String errorMessage) {

    }


    //Navigation listener###########################################################################
    @Override
    public void onArrival() {
        //Log.d("HalloWelt", "Navigation: Arrived");
        parent.navigationView.stopNavigation();
    }

    @Override
    public void onCancelNavigation() {
        //Log.d("HalloWelt", "Navigation onCancel");
        parent.navigationView.stopNavigation();
    }

    @Override
    public void onNavigationFinished() {
        //Log.d("HalloWelt", "Navigation Finished");
    }

    @Override
    public void onNavigationRunning() {
        //Log.d("HalloWelt", "Navigation Running");
    }
    //##############################################################################################

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
