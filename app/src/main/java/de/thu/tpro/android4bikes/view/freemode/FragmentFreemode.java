package de.thu.tpro.android4bikes.view.freemode;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.tilequery.MapboxTilequery;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.openWeather.OpenWeatherObject;
import de.thu.tpro.android4bikes.services.PositionTracker;
import de.thu.tpro.android4bikes.util.ChartsUtil.ChartsUtil;
import de.thu.tpro.android4bikes.util.GeoFencing;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.util.GpsUtils;
import de.thu.tpro.android4bikes.util.mapbox.MapBoxUtils;
import de.thu.tpro.android4bikes.view.MainActivity;
import de.thu.tpro.android4bikes.viewmodel.ViewModelBikerack;
import de.thu.tpro.android4bikes.viewmodel.ViewModelHazardAlert;
import de.thu.tpro.android4bikes.viewmodel.ViewModelOwnBikerack;
import de.thu.tpro.android4bikes.viewmodel.ViewModelOwnHazardAlerts;
import de.thu.tpro.android4bikes.viewmodel.ViewModelTrack;
import de.thu.tpro.android4bikes.viewmodel.ViewModelWeather;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;

public class FragmentFreemode extends Fragment implements OnMapReadyCallback, PermissionsListener, Observer, androidx.lifecycle.Observer<OpenWeatherObject> {
    private static final String LOG_TAG = "FragmentFreeMode";
    private static final String MAPFRAGMENT_TAG = "mapFragmentTAG2";
    private static final int VELOCITY_UPDATE_INTERVAL = 250;
    private static LatLng latLng_lastcamerapos;
    //OWN ViewModels (=OWN DATA CREATED BY THIS USER!!!!)
    private ViewModelOwnBikerack vm_ownBikeRack;
    private ViewModelOwnHazardAlerts vm_ownHazards;
    private Style style;
    //Regular ViewModels (DATA FROM THE GEOFENCE!!!)
    private ViewModelBikerack vm_bikeRack;
    private ViewModelHazardAlert vm_Hazards;
    private ViewModelWeather vmWeather;
    private ViewModelTrack vm_Tracks;
    private MainActivity parent;
    private View viewFreemode;
    private Timer updateTimer;
    private SupportMapFragment mapFragment;
    private MapboxMap mapboxMap;
    private ExtendedFloatingActionButton fab_velocity;
    private FloatingActionButton fab_weather;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    //GeoFencing
    private GeoFencing geoFencing_bikeRacks;
    private GeoFencing geoFencing_hazardAlerts;
    private boolean fenceAlreadyRunning;
    private boolean fenceAlreadyStopped;
    private boolean hazardLayer_created;
    private boolean bikerackLayer_created;
    private ChartsUtil chartsUtil;
    private boolean locationenabled;
    private Observable observable_position;
    private int addable;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewFreemode = inflater.inflate(R.layout.fragment_freemode, container, false);

        // init ViewModels
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        //own ViewModels
        vm_ownBikeRack = provider.get(ViewModelOwnBikerack.class);
        vm_ownHazards = provider.get(ViewModelOwnHazardAlerts.class);

        //general ViewModels
        vm_Hazards = provider.get(ViewModelHazardAlert.class);
        vm_Tracks = provider.get(ViewModelTrack.class);
        vm_bikeRack = provider.get(ViewModelBikerack.class);
        vmWeather = provider.get(ViewModelWeather.class);

        return viewFreemode;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //we need the parent Activity to init our map
        parent = (MainActivity) this.getActivity();
        GlobalContext.setContext(parent.getApplicationContext());

        initweatherFab();
        chartsUtil = new ChartsUtil();

        chartsUtil.initalizeElevationChart(view, R.id.elevationChart, parent);



        vmWeather.getCurrentWeather().observe(getViewLifecycleOwner(), this);

        //start with GeoFencing:
        geoFencing_bikeRacks = vm_bikeRack.getGeoFencing_bikeRacks();
        geoFencing_hazardAlerts = vm_Hazards.getGeoFencing_hazardAlerts();

        fenceAlreadyRunning = false;
        fenceAlreadyStopped = true;
        locationenabled = false;
        hazardLayer_created = false;
        bikerackLayer_created = false;

        initMap(savedInstanceState);
        fade(viewFreemode);
    }


    public void fade(View view) {
        ImageView image = view.findViewById(R.id.imageView_animation);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(image, "alpha", 1f, 0f);
        fadeOut.setDuration(1000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(image, "alpha", 0f, 1f);
        fadeIn.setDuration(2000);

        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.play(fadeIn).after(fadeOut);

        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimationSet.start();
            }
        });
        mAnimationSet.start();
    }



    private void initMap(Bundle savedInstanceState) {
        Mapbox.getInstance(parent, parent.getString(R.string.access_token));
        if (savedInstanceState == null) {

            // TODO replace with useful stuff
            MapboxMapOptions mapOptions = MapboxMapOptions.createFromAttributes(parent, null);

            mapFragment = SupportMapFragment.newInstance(mapOptions);

            final FragmentTransaction transaction = parent.getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.map_container_freemode, mapFragment, MAPFRAGMENT_TAG);
            transaction.commit();


        } else {
            Mapbox.getInstance(parent, parent.getString(R.string.access_token));
            mapFragment = (SupportMapFragment) parent.getSupportFragmentManager()
                    .findFragmentByTag(MAPFRAGMENT_TAG);

        }

        if (mapFragment != null) {
            mapFragment.getMapAsync(this::onMapReady);
        }
    }

    private void initLayers(@NonNull Style loadedMapStyle) {
        if (!hazardLayer_created) {
            //createInfoWindowLayer(style,,);
            //Create unclustered symbol layer
            MapBoxUtils.createUnclusteredSymbolLayer(loadedMapStyle, GeoFencing.ConstantsGeoFencing.COLLECTION_HAZARDS.toString(), MapBoxUtils.MapBoxSymbols.HAZARDALERT_GENERAL);
            //Create clustered circle layer
            MapBoxUtils.createClusteredCircleOverlay(loadedMapStyle, GeoFencing.ConstantsGeoFencing.COLLECTION_HAZARDS.toString(), MapBoxUtils.MapBoxSymbols.HAZARDALERT_GENERAL);
            hazardLayer_created = true;
        }
        if (!bikerackLayer_created) {
            //createInfoWindowLayer(style,,);
            MapBoxUtils.createUnclusteredSymbolLayer(loadedMapStyle, GeoFencing.ConstantsGeoFencing.COLLECTION_BIKERACKS.toString(), MapBoxUtils.MapBoxSymbols.BIKERACK);
            //Create clustered circle layer
            MapBoxUtils.createClusteredCircleOverlay(loadedMapStyle, GeoFencing.ConstantsGeoFencing.COLLECTION_BIKERACKS.toString(), MapBoxUtils.MapBoxSymbols.BIKERACK);
            bikerackLayer_created = true;
        }
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/and4bikes/ck93ydsyn2ovs1js95kx1nu4u"),
                style -> {
                    initUserPosition(style);
                    mapboxMap.getLocationComponent().activateLocationComponent(LocationComponentActivationOptions
                            .builder(parent, style)
                            .useDefaultLocationEngine(true)
                            .locationEngineRequest(new LocationEngineRequest.Builder(750)
                                    .setFastestInterval(750)
                                    .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                                    .build())
                            .build());
                    mapboxMap.getUiSettings().setCompassEnabled(false);
                    mapboxMap.getLocationComponent().setLocationComponentEnabled(true);
                    mapboxMap.getLocationComponent().setRenderMode(RenderMode.GPS);
                    mapboxMap.getLocationComponent().setCameraMode(CameraMode.TRACKING_GPS);
                    mapboxMap.getLocationComponent().zoomWhileTracking(17, 3000);
                    mapboxMap.getUiSettings().setAllGesturesEnabled(false);
                    enableLocationComponent(style);

                    MapBoxUtils.initMarkerSymbols(mapboxMap, MapBoxUtils.generateMarkerPool(parent));
                    initLayers(style);
                    initVelocityFAB();

                    //set class attribute "style" (purpose: adding new markers after update regarding ViewModels)
                    this.style = style;

                    //register for ViewModels after the map is ready:
                    vm_Hazards.getHazardAlerts().observe(getViewLifecycleOwner(), this::onChangedHazardAlerts);
                    vm_bikeRack.getList_bikeRacks_shown().observe(getViewLifecycleOwner(), this::onChangedBikeRacks);

                    //setup geofences
                    LatLng latlng_setuppos = mapboxMap.getCameraPosition().target;
                    GeoPoint geopoint_setuppos = new GeoPoint(latlng_setuppos.getLatitude(), latlng_setuppos.getLongitude());

                    geoFencing_bikeRacks.setupGeofence(geopoint_setuppos, 10);
                    geoFencing_hazardAlerts.setupGeofence(geopoint_setuppos, 10);

                    mapboxMap.addOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
                        @Override
                        public void onCameraMove() {
                            if (latLng_lastcamerapos == null) {
                                latLng_lastcamerapos = mapboxMap.getCameraPosition().target;
                            }
                            if (mapboxMap.getCameraPosition().zoom > 13) {
                                if (!fenceAlreadyRunning) {
                                    geoFencing_bikeRacks.startGeoFenceListener();
                                    geoFencing_hazardAlerts.startGeoFenceListener();
                                    fenceAlreadyRunning = true;
                                    fenceAlreadyStopped = false;
                                }
                            } else {
                                if (!fenceAlreadyStopped) {
                                    geoFencing_bikeRacks.stopGeoFenceListener();
                                    geoFencing_hazardAlerts.stopGeoFenceListener();
                                    fenceAlreadyRunning = false;
                                    fenceAlreadyStopped = true;
                                }
                            }

                            LatLng latlng_currCameraPos = mapboxMap.getCameraPosition().target;
                            double distance = latlng_currCameraPos.distanceTo(latLng_lastcamerapos);

                            GeoPoint geopoint_currPos = new GeoPoint(latlng_currCameraPos.getLatitude(), latlng_currCameraPos.getLongitude());
                            if (mapboxMap.getCameraPosition().zoom > 13 && geoFencing_bikeRacks != null && distance / 1000 > (geoFencing_bikeRacks.getRadius() / 2)) {
                                geoFencing_bikeRacks.updateCenter(geopoint_currPos);
                                latLng_lastcamerapos = latlng_currCameraPos;
                            }

                            if (mapboxMap.getCameraPosition().zoom > 13 && geoFencing_hazardAlerts != null && distance / 1000 > (geoFencing_hazardAlerts.getRadius() / 2)) {
                                geoFencing_hazardAlerts.updateCenter(geopoint_currPos);
                                latLng_lastcamerapos = latlng_currCameraPos;
                            }

                        }
                    });
                    //Generate Point at Location every 5sec
                    observable_position = PositionTracker.LocationChangeListeningActivityLocationCallback.getInstance(parent);
                    observable_position.addObserver(this);
                });
    }

    private void initUserPosition(@NonNull Style loadedmapstyle) {
        if (PositionTracker.getLastPosition().isValid()) {
            mapboxMap.setCameraPosition(new CameraPosition.Builder()
                    .tilt(60)
                    .build());
            enableLocationComponent(loadedmapstyle);
        } else {
            Position germany_center = new Position(51.163361111111, 10.447683333333);
            mapboxMap.setCameraPosition(new CameraPosition.Builder()
                    .target(germany_center.toMapboxLocation())
                    .zoom(4)
                    .tilt(60)
                    .build());
        }
    }

    //Location Stuff--------------------------------------------------------------

    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(parent)) {

            //Check if GPS is enabled on device
            //parent.checkLocationEnabled();
            new GpsUtils(getActivity()).turnGPSOn(new GpsUtils.onGpsListener() {
                @Override
                public void gpsStatus(boolean isGPSEnable) {
                    // turn on GPS
                    //isGPS = isGPSEnable;
                }
            });

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(parent);
        }
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    private boolean isAccessLocationPermissionGranted() {
        return ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void submitMarker() {
        AlertDialog markerDialog = new MaterialAlertDialogBuilder(getContext())
                .setTitle("Submit")
                .setItems(R.array.marker, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                submit_Rack();
                                break;
                            case 1:
                                submit_hazard();
                                break;
                            default:
                                Snackbar.make(viewFreemode.findViewById(R.id.map_container_freemode), "default", 1000).setAnchorView(viewFreemode.findViewById(R.id.bottomAppBar)).show();
                        }
                    }
                })
                .create();
        markerDialog.show();
        markerDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * submits a Hazard Alert on the current position
     */
    private void submit_hazard() {
        submit_hazard(PositionTracker.getLastPosition());
    }

    /**
     * submits a Hazard Alert on the given position
     *
     * @param hazardPosition position of the hazard to report
     */
    public void submit_hazard(Position hazardPosition) {
        AlertDialog hazardDialog = new MaterialAlertDialogBuilder(parent)
                .setTitle(R.string.submit_hazard)
                .setView(R.layout.dialog_hazard)
                .setPositiveButton(R.string.submit, null)
                .setNegativeButton(R.string.discard, (dialogInterface, i) ->
                        Snackbar.make(viewFreemode.findViewById(R.id.map_container_freemode), R.string.dismiss, 1000)
                                .setAnchorView(viewFreemode.findViewById(R.id.bottomAppBar))
                                .show())
                .create();
        hazardDialog.setOnShowListener(dialogInterface -> {
            hazardDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary, parent.getTheme()));
            hazardDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary, parent.getTheme()));
        });
        hazardDialog.show();
        hazardDialog.setCanceledOnTouchOutside(false);

        MapView hazardMap = hazardDialog.findViewById(R.id.hazardMap);
        hazardMap.onCreate(hazardDialog.onSaveInstanceState());

        Position lastPosition = PositionTracker.getLastPosition();
        hazardMap.getMapAsync(mapboxMapHazard -> {
            SymbolOptions marker = MapBoxUtils.createMarker(lastPosition.getLatitude(), lastPosition.getLongitude(), MapBoxUtils.MapBoxSymbols.HAZARDALERT_GENERAL);
            List<Feature> features = new ArrayList<>();
            features.add(Feature.fromGeometry(marker.getGeometry()));
            mapboxMapHazard.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/streets-v11")
                    .withSource(new GeoJsonSource("HazardTest", FeatureCollection.fromFeatures(features)))
                    .withImage(MapBoxUtils.MapBoxSymbols.HAZARDALERT_GENERAL.toString(), parent.getDrawable(R.drawable.ic_material_hazard))
                    .withLayer(new SymbolLayer("Layer", "HazardTest")
                            .withProperties(PropertyFactory.iconImage(MapBoxUtils.MapBoxSymbols.HAZARDALERT_GENERAL.toString()),
                                    iconAllowOverlap(true)
                            ))
            );
            if (lastPosition != null) {
                mapboxMapHazard.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(lastPosition.toMapboxLocation())
                        .zoom(17)
                        .bearing(0)
                        .build()), 1000);
            }
        });

        Button btnPos = hazardDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Spinner spinnerHazard = hazardDialog.findViewById(R.id.sp_hazards);
        btnPos.setOnClickListener(view -> {
            int i = spinnerHazard.getSelectedItemPosition() + 1;
            HazardAlert.HazardType type = HazardAlert.HazardType.getByType(i);
            int distanceOfInterest = 10;

            // create hazard alert from entered info
            HazardAlert newHazard = new HazardAlert(type, hazardPosition, distanceOfInterest, true);

            // submit hazard alert to ViewModel
            vm_ownHazards.addOwnHazard(newHazard);
            hazardDialog.dismiss();
        });
        hazardDialog.show();
    }

    /**
     * Fake Observer to be called, when ViewModelHazardAlerts has changed (called by Lambda expression)
     *
     * @param hazardList
     */
    private void onChangedHazardAlerts(List<HazardAlert> hazardList) {
        // TODO display HazardAlert as Marker
        //String snackText = String.format("%d new Hazard Alerts found!", hazardList.size());
        //Snackbar.make(parent.findViewById(R.id.map_container_info), snackText, 2500).show();
        //TODO: delete overlay
        List<HazardAlert> cleared = new ArrayList<>();
        hazardList.forEach(entry -> {
            if (entry.getPosition() != null) {
                cleared.add(entry);
            }
        });
        hazardList = cleared;
        MapBoxUtils.updateHazardAlertOverlay(style, hazardList);
    }

    /**
     * Fake Observer to be called, when ViewModelBikeRack has changed (called by Lambda expression)
     *
     * @param bikeRackList
     */
    private void onChangedBikeRacks(List<BikeRack> bikeRackList) {
        // TODO display BikeRack as Marker
        //String snackText = String.format("%d new Bike Racks found!", bikeRackList.size());
        //Snackbar.make(parent.findViewById(R.id.map_container_info), snackText, 2500).show();
        //TODO: delete overlay

        List<BikeRack> cleared = new ArrayList<>();
        bikeRackList.forEach(entry -> {
            if (entry.getPosition() != null) {
                cleared.add(entry);
            }
        });
        bikeRackList = cleared;
        MapBoxUtils.updateBikeRackOverlay(style, bikeRackList);
    }

    @Override
    public void onChanged(OpenWeatherObject weatherObject) {
        String weatherIconName = vmWeather.getCurrentWeather().getValue().getForecastList().get(0)
                .getWeather().get(0).getIcon().substring(0, 2);

        Log.d(LOG_TAG, "Weather: " + weatherIconName + ", " + weatherObject.getCity().getName());

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

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof PositionTracker.LocationChangeListeningActivityLocationCallback) {
            if (o instanceof Map && !((Map) o).keySet().isEmpty()) {
                Map map = (Map) o;
                if (map.get(PositionTracker.CONSTANTS.POSITION.toText()) != null) {
                    Position last_position = (Position) map.get(PositionTracker.CONSTANTS.POSITION.toText());
                    if (last_position.isValid()) {
                        if (style != null && !locationenabled) {
                            enableLocationComponent(style);
                            locationenabled = true;
                        }
                        makeElevationRequestToTilequeryApi(style, last_position.toMapboxLocation());
                    }
                }
            }
        }
    }

    /**
     * Use the Java SDK's MapboxTilequery class to build a API request and use the API response
     *
     * @param point where the Tilequery API should query Mapbox's "mapbox.mapbox-terrain-v2" tileset
     *              for elevation data.
     */
    private void makeElevationRequestToTilequeryApi(@NonNull final Style style, @NonNull LatLng point) {
        MapboxTilequery elevationQuery = MapboxTilequery.builder()
                .accessToken(getString(R.string.access_token))
                .mapIds("mapbox.mapbox-terrain-v2")
                .query(Point.fromLngLat(point.getLongitude(), point.getLatitude()))
                .geometry("polygon")
                .layers("contour")
                .build();

        elevationQuery.enqueueCall(new Callback<FeatureCollection>() {
            @Override
            public void onResponse(Call<FeatureCollection> call, Response<FeatureCollection> response) {

                if (response.body().features() != null) {
                    List<Feature> featureList = response.body().features();

                    int highest_ele = 0;
                    // Build a list of the elevation numbers in the response.
                    for (Feature singleFeature : featureList) {
                        int current_ele = Integer.valueOf(singleFeature.getStringProperty("ele"));
                        if (current_ele > highest_ele) {
                            highest_ele = current_ele;
                        }
                    }

                    Log.d("TESTST", String.valueOf(highest_ele));
                    chartsUtil.feedNewData(Float.valueOf(String.valueOf(highest_ele)));
                } else {
                }
            }

            @Override
            public void onFailure(Call<FeatureCollection> call, Throwable throwable) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
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
    public void onDestroy() {
        super.onDestroy();
    }

    public void submit_Rack() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(parent);
        builder.setTitle("Submit rack");
        builder.setView(R.layout.dialog_rack);
        builder.setPositiveButton("Submit", null);
        builder.setNegativeButton("Dismiss", (dialogInterface, i) -> Snackbar.make(viewFreemode.findViewById(R.id.map_container_info), "Dismiss", 1000).setAnchorView(viewFreemode.findViewById(R.id.bottomAppBar)).show());
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary, parent.getTheme()));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary, parent.getTheme()));
        });
        dialog.show();

        MapView rackMap = dialog.findViewById(R.id.rackMap);
        rackMap.onCreate(dialog.onSaveInstanceState());
        Position lastPosition = PositionTracker.getLastPosition();

        rackMap.getMapAsync(mapboxMapRack -> {
            SymbolOptions marker = MapBoxUtils.createMarker(lastPosition.getLatitude(), lastPosition.getLongitude(), MapBoxUtils.MapBoxSymbols.BIKERACK);
            List<Feature> features = new ArrayList<>();
            features.add(Feature.fromGeometry(marker.getGeometry()));
            mapboxMapRack.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/streets-v11")
                    .withSource(new GeoJsonSource("RackTest", FeatureCollection.fromFeatures(features)))
                    .withImage(MapBoxUtils.MapBoxSymbols.BIKERACK.toString(), parent.getDrawable(R.drawable.ic_material_bikerack_24dp))
                    .withLayer(new SymbolLayer("Layer", "RackTest")
                            .withProperties(PropertyFactory.iconImage(MapBoxUtils.MapBoxSymbols.BIKERACK.toString()),
                                    iconAllowOverlap(true)
                            ))
            );
            if (lastPosition != null) {
                mapboxMapRack.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(PositionTracker.getLastPosition().toMapboxLocation())
                        .zoom(17)
                        .bearing(0)
                        .build()), 1000);
            }
        });

        /*rackMap.getMapAsync(mapboxMapRack -> mapboxMapRack.setStyle(Style.MAPBOX_STREETS, style -> {
            if (lastPosition != null) {
                mapboxMapRack.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(PositionTracker.getLastPosition().toMapboxLocation())
                        .zoom(17)
                        .bearing(0)
                        .build()), 1000);

                SymbolOptions marker = createMarker(lastPosition.getLatitude(), lastPosition.getLongitude(), MapBoxSymbols.BIKERACK);
                List<Feature> features = new ArrayList<>();
                features.add(Feature.fromGeometry(marker.getGeometry()));

                FeatureCollection collection = FeatureCollection.fromFeatures(features);
                addGeoJsonSource(mapboxMapRack.getStyle(), collection, "test", false);
                createUnclusteredSymbolLayer(mapboxMapRack.getStyle(), "test", MapBoxSymbols.BIKERACK);
            }
        })
        );*/

        EditText editRack = dialog.findViewById(R.id.edit_rack_name);
        Spinner spCapacity = dialog.findViewById(R.id.sp_capacity);
        CheckBox cbEBike = dialog.findViewById(R.id.chBx_ebike);
        CheckBox cbCovered = dialog.findViewById(R.id.chBx_covered);
        Button btnPos = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNeg = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        TextInputLayout nameLayout = dialog.findViewById(R.id.txt_rack_name_layout);
        btnPos.setOnClickListener(view -> {
            if (editRack.getText().toString().trim().equals("")) {
                nameLayout.setError(parent.getText(R.string.rack_error));
                //Snackbar.make(viewInfo.findViewById(R.id.map_container_info), "Pleas fill in rack name", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
            } else {
                Position currLastPos = PositionTracker.getLastPosition();
                if (currLastPos != null) {
                    BikeRack newRack = new BikeRack(currLastPos,
                            editRack.getText().toString(),
                            BikeRack.ConstantsCapacity.valueOf(spCapacity.getSelectedItem().toString().toUpperCase()),
                            cbEBike.isChecked(),
                            true,
                            cbCovered.isChecked()
                    );
                    Log.d(LOG_TAG, newRack.toString());
                    vm_ownBikeRack.addOwnBikeRack(newRack);
                }
                dialog.dismiss();
            }
        });
        editRack.setOnFocusChangeListener((view, b) -> nameLayout.setError(null));
    }

    private void initweatherFab() {
        fab_weather = viewFreemode.findViewById(R.id.weatherFAB_freemode);
    }

    private void initVelocityFAB() {
        fab_velocity = viewFreemode.findViewById(R.id.velocityFAB_freemode);
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

    public void cancelUpdateTimer() {
        updateTimer.cancel();
    }
}
