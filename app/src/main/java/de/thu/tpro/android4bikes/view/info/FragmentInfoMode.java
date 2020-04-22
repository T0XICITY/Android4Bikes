package de.thu.tpro.android4bikes.view.info;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
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
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Rating;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.positiontest.PositionProvider;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.view.MainActivity;

import static android.os.Looper.getMainLooper;
import static com.mapbox.mapboxsdk.style.expressions.Expression.all;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.gte;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.lt;
import static com.mapbox.mapboxsdk.style.expressions.Expression.toNumber;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;


public class FragmentInfoMode extends Fragment implements OnMapReadyCallback, PermissionsListener {

    private static final String LOG_TAG = "FragmentInfoMode";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 102;
    private static final String MAPFRAGMENT_TAG = "mapFragmentTAG";
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private static final String TAG = "DirectionsActivity";
    MainActivity parent;
    SymbolOptions marker;
    private SupportMapFragment mapFragment;
    private LocationChangeListeningActivityLocationCallback callback = new LocationChangeListeningActivityLocationCallback(this);
    private MapboxMap mapboxMap;
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationComponent locationComponent;
    private LatLng lastPos;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        return inflater.inflate(R.layout.fragment_info_mode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //we need the parent Activity to init our map
        parent = (MainActivity) this.getActivity();
        GlobalContext.setContext(parent.getApplicationContext());
        initMap(savedInstanceState);
    }

    private void initMap(Bundle savedInstanceState) {
        Mapbox.getInstance(parent, parent.getString(R.string.access_token));
        if (savedInstanceState == null) {

            // TODO replace with useful stuff
            MapboxMapOptions mapOptions = MapboxMapOptions.createFromAttributes(parent, null);
            mapOptions.camera(new CameraPosition.Builder()
                    .target(new LatLng(48.4046, 9.9815))
                    .zoom(15)
                    .build()
            );
            mapFragment = SupportMapFragment.newInstance(mapOptions);

            final FragmentTransaction transaction = parent.getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.map_container_info, mapFragment, MAPFRAGMENT_TAG);
            transaction.commit();


        } else {
            mapFragment = (SupportMapFragment) parent.getSupportFragmentManager()
                    .findFragmentByTag(MAPFRAGMENT_TAG);

        }

        if (mapFragment != null) {
            mapFragment.getMapAsync(this::onMapReady);
        }

    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/and4bikes/ck93ydsyn2ovs1js95kx1nu4u"),
                style -> {
                    enableLocationComponent(style);

                    HashMap<FragmentInfoMode.MapBoxSymbols, Drawable> markerPool = new HashMap<>();
                    markerPool.put(FragmentInfoMode.MapBoxSymbols.BIKERACK, parent.getDrawable(R.drawable.mapbox_marker_icon_default));
                    markerPool.put(FragmentInfoMode.MapBoxSymbols.HAZARDALERT_GENERAL, parent.getDrawable(R.drawable.mapbox_marker_icon_default));
                    markerPool.put(FragmentInfoMode.MapBoxSymbols.TRACK, parent.getDrawable(R.drawable.mapbox_marker_icon_default));
                    initMarkerSymbols(mapboxMap, markerPool);
                    //generateCustomRoute(generateTrack().getFineGrainedPositions());
                    initPosFab();
                    ArrayList<BikeRack> bikeRacks = new ArrayList<>();
                    for (int i = 0; i < 15; i++) {
                        bikeRacks.add(generateTHUBikeRack(i));
                    }
                    addBikeRackOverlay(style, bikeRacks, "meineDaten");
                    //addHazardAlertOverlay();
                    //addTrackOverlay();

                    //new LoadGeoJson(FragmentInfoMode.this).execute();
                });
    }

    /**
     * Create Markers from Track List
     *
     * @param loadedMapStyle
     * @param tracks
     */
    private void addTrackOverlay(@NonNull Style loadedMapStyle, ArrayList<Track> tracks, String dataSourceID) {
        List<Feature> list_feature = new ArrayList<>();
        //Generate Markers from ArrayList
        for (Track track : tracks) {
            SymbolOptions marker = createMarker(track.getFineGrainedPositions().get(0).getLatitude(), track.getFineGrainedPositions().get(0).getLongitude(), FragmentInfoMode.MapBoxSymbols.TRACK);
            list_feature.add(Feature.fromGeometry(marker.getGeometry()));
        }
        //Create FeatureCollection from Feature List
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(list_feature);
        //Create unclustered symbol layer
        String id = addGeoJsonSource(loadedMapStyle, featureCollection, dataSourceID, true, 200);
        createUnclusteredSymbolLayer(loadedMapStyle, id);
        //Create clustered circle layer
        createClusteredCircleOverlay(loadedMapStyle, id);
    }

    /**
     * Create Markers from BikeRack List
     *
     * @param loadedMapStyle
     * @param bikeRacks
     */
    private void addBikeRackOverlay(@NonNull Style loadedMapStyle, ArrayList<BikeRack> bikeRacks, String dataSourceID) {
        List<Feature> list_feature = new ArrayList<>();
        //Generate Markers from ArrayList
        for (BikeRack bikeRack : bikeRacks) {
            SymbolOptions marker = createMarker(bikeRack.getPosition().getLatitude(), bikeRack.getPosition().getLongitude(), FragmentInfoMode.MapBoxSymbols.BIKERACK);
            list_feature.add(Feature.fromGeometry(marker.getGeometry()));
        }
        //Create FeatureCollection from Feature List
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(list_feature);
        //Create unclustered symbol layer
        String id = addGeoJsonSource(loadedMapStyle, featureCollection, dataSourceID, true, 200);
        createUnclusteredSymbolLayer(loadedMapStyle, id);
        //Create clustered circle layer
        createClusteredCircleOverlay(loadedMapStyle, id);
    }

    /**
     * Create markers from HazardAlert list
     *
     * @param loadedMapStyle
     * @param hazardAlerts
     */
    private void addHazardAlertOverlay(@NonNull Style loadedMapStyle, ArrayList<HazardAlert> hazardAlerts, String dataSourceID) {
        // Create Markers from data and set the 'cluster' option to true.
        List<Feature> list_feature = new ArrayList<>();
        //Generate Markers from ArrayList
        for (HazardAlert hazardAlert : hazardAlerts) {
            SymbolOptions marker = createMarker(hazardAlert.getPosition().getLatitude(), hazardAlert.getPosition().getLongitude(), FragmentInfoMode.MapBoxSymbols.HAZARDALERT_GENERAL);
            list_feature.add(Feature.fromGeometry(marker.getGeometry()));
        }
        //Create FeatureCollection from Feature List
        FeatureCollection featureCollection = FeatureCollection.fromFeatures(list_feature);
        //Create unclustered symbol layer
        String id = addGeoJsonSource(loadedMapStyle, featureCollection, dataSourceID, true, 200);
        createUnclusteredSymbolLayer(loadedMapStyle, id);
        //Create clustered circle layer
        createClusteredCircleOverlay(loadedMapStyle, id);
    }

    private String addGeoJsonSource(@NonNull Style loadedMapStyle, FeatureCollection featureCollection, String ID, boolean withCluster, int clusterRadius) {
        //New GeoJsonSource from FeatureCollection
        Source source = new GeoJsonSource(ID, featureCollection, new GeoJsonOptions()
                .withCluster(withCluster)
                .withClusterRadius(clusterRadius)
        );
        loadedMapStyle.addSource(source);
        return source.getId();
    }

    /**
     * Create unclustered SymbolLayer for small zoom level
     *
     * @param loadedMapStyle
     * @param sourceID
     */
    private void createUnclusteredSymbolLayer(@NonNull Style loadedMapStyle, String sourceID) {
        //Create Symbol Layer for unclustered data points
        SymbolLayer unclustered = new SymbolLayer("unclustered-points", sourceID);


        unclustered.setProperties(
                iconImage(FragmentInfoMode.MapBoxSymbols.BIKERACK.toString())/*,
                iconSize(
                        division(
                                get("mag"), literal(4.0f)
                        )
                ),
                iconColor(
                        interpolate(exponential(1), get("mag"),
                                stop(2.0, rgb(0, 255, 0)),
                                stop(4.5, rgb(0, 0, 255)),
                                stop(7.0, rgb(255, 0, 0))
                        )
                )*/
        );
        //unclustered.setFilter(has("mag"));
        loadedMapStyle.addLayer(unclustered);
    }

    /**
     * Create clustered CircleLayer for bigger zoom level
     *
     * @param loadedMapStyle
     * @param sourceID
     */
    private void createClusteredCircleOverlay(@NonNull Style loadedMapStyle, String sourceID) {

        // Each point range gets a different fill color.
        int[][] layers = new int[][]{
                new int[]{150, ContextCompat.getColor(parent, R.color.mapbox_blue)},
                new int[]{20, ContextCompat.getColor(parent, R.color.mapbox_blue)},
                new int[]{0, ContextCompat.getColor(parent, R.color.mapbox_blue)}
        };

        for (int i = 0; i < layers.length; i++) {
            //Add clusters' circles
            CircleLayer circles = new CircleLayer("cluster-" + i, sourceID);
            circles.setProperties(
                    circleColor(layers[i][1]),
                    circleRadius(30f)
            );

            Expression pointCount = toNumber(get("point_count"));

            // Add a filter to the cluster layer that hides the circles based on "point_count"
            circles.setFilter(
                    i == 0
                            ? all(has("point_count"),
                            gte(pointCount, literal(layers[i][0]))
                    ) : all(has("point_count"),
                            gte(pointCount, literal(layers[i][0])),
                            lt(pointCount, literal(layers[i - 1][0]))
                    )
            );
            loadedMapStyle.addLayer(circles);
        }

        //Add the count labels
        SymbolLayer count = new SymbolLayer("count", sourceID);
        count.setProperties(
                textField(Expression.toString(get("point_count"))),
                textSize(12f),
                textColor(Color.WHITE),
                textIgnorePlacement(true),
                textAllowOverlap(true)
        );
        loadedMapStyle.addLayer(count);
    }

    //Location Stuff--------------------------------------------------------------

    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(parent)) {

            // Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(parent, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            // Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            locationComponent.zoomWhileTracking(15, 3000, new MapboxMap.CancelableCallback() {
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

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(parent);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

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

    //Helper Methods------------------------------------------------------
    private void initPosFab() {
        FloatingActionButton FAB = parent.findViewById(R.id.fab_pos);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(lastPos)
                        .zoom(15)
                        .bearing(0)
                        .build()), 3000);
            }
        });
    }

    private void initMarkerSymbols(MapboxMap mapboxMap, HashMap<FragmentInfoMode.MapBoxSymbols, Drawable> markers) {
        //Check if marker HashMap is empty
        if (!markers.isEmpty()) {
            //Register Symbols in Mapbox
            markers.forEach((type, icon) -> mapboxMap.getStyle().addImage(type.toString(), icon));
        }
    }

    private SymbolOptions createMarker(double latitude, double longitude, FragmentInfoMode.MapBoxSymbols type) {
        /*if(mCurrLocationMarker!=null){
            mCurrLocationMarker.setPosition(latLng);
        }else{
            mCurrLocationMarker = map.addMarker(new MarkerOptions()
                    .position(latLng);
        }*/
        return new SymbolOptions()
                .withLatLng(new LatLng(latitude, longitude))
                .withIconImage(type.toString());
    }

    /**
     * generates a new instance of the class {@link de.thu.tpro.android4bikes.data.model.BikeRack} for test purposes
     *
     * @return instance of the class {@link de.thu.tpro.android4bikes.data.model.BikeRack}
     */

    private BikeRack generateTHUBikeRack(int i) {
        //create new BikeRack
        BikeRack bikeRack_THU = new BikeRack(
                "pfo4eIrvzrI0m363KF0K", new Position(48.408880 + i / 7000.0, 9.997507 + i / 7000.0), "THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true, false
        );
        return bikeRack_THU;
    }

    /**
     * generates a new instance of the class Track for test purposes
     *
     * @return instance of a track
     */
    private Track generateTrack() {

        Track track = new Track("nullacht15", new Rating(), "Heimweg", "Das ist meine super tolle Strecke",
                "siebenundvierzig11", 1585773516, 25,
                PositionProvider.getDummyPosition90elements(), new ArrayList<>(), true);
        return track;
    }

    private BikeRack generateTHUBikeRack() {
        //create new BikeRack
        BikeRack bikeRack_THU = new BikeRack(
                "pfo4eIrvzrI0m363KF0K", new Position(48.408880, 9.997507), "THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true, false
        );
        return bikeRack_THU;
    }

    private enum MapBoxSymbols {
        BIKERACK("BIKERACK"),
        HAZARDALERT_GENERAL("HAZARDALERT_GENERAL"),
        TRACK("TRACK");

        private String type;

        MapBoxSymbols(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }

    private static class LocationChangeListeningActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<FragmentInfoMode> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(FragmentInfoMode activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            FragmentInfoMode activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                activity.lastPos = new LatLng(result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude());
                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
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
            FragmentInfoMode activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity.parent, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Prevent leaks
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
    }


}
