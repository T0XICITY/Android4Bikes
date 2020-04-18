package de.thu.tpro.android4bikes.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.os.AsyncTask;
import androidx.annotation.Nullable;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import de.thu.tpro.android4bikes.positiontest.PositionProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
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
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.util.GlobalContext;

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

/**
 * Use the Mapbox Core Library to receive updates when the device changes location.
 */
public class ActivityMapBoxTest extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener {

    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationChangeListeningActivityLocationCallback callback = new LocationChangeListeningActivityLocationCallback(this);
    private LatLng lastPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalContext.setContext(this.getApplicationContext());

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_map_box_test);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.OUTDOORS, //todo:Embedd own style here
                style -> {
                    enableLocationComponent(style);

                    HashMap<MapBoxSymbols, Drawable> markerPool = new HashMap<>();
                    markerPool.put(MapBoxSymbols.BIKERACK, getDrawable(R.drawable.mapbox_marker_icon_default));
                    markerPool.put(MapBoxSymbols.HAZARDALERT_GENERAL, getDrawable(R.drawable.mapbox_marker_icon_default));
                    markerPool.put(MapBoxSymbols.TRACK, getDrawable(R.drawable.mapbox_marker_icon_default));
                    initMarkerSymbols(mapboxMap, markerPool);

                    initPosFab();
                    ArrayList<BikeRack> bikeRacks = new ArrayList<>();
                    for (int i = 0; i < 15; i++) {
                        bikeRacks.add(generateTHUBikeRack(i));
                    }
                    addBikeRackOverlay(style, bikeRacks, "meineDaten");
                    //addHazardAlertOverlay();
                    //addTrackOverlay();

                    new LoadGeoJson(ActivityMapBoxTest.this).execute();
                });
    }

    private void drawLines(@NonNull FeatureCollection featureCollection) {
        if (mapboxMap != null) {
            mapboxMap.getStyle(style -> {
                if (featureCollection.features() != null) {
                    if (featureCollection.features().size() > 0) {
                        style.addSource(new GeoJsonSource("line-source", featureCollection));

                        style.addLayer(new LineLayer("linelayer", "line-source")
                                .withProperties(PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                                        PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                                        PropertyFactory.lineOpacity(.7f),
                                        PropertyFactory.lineWidth(7f),
                                        PropertyFactory.lineColor(Color.parseColor("#00ff00"))));
                    }
                }
            });
        }
    }

    private static class LoadGeoJson extends AsyncTask<Void, Void, FeatureCollection> {
        private WeakReference<ActivityMapBoxTest> weakReference;
        LoadGeoJson(ActivityMapBoxTest activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        protected FeatureCollection doInBackground(Void... voids) {
            try {
                ActivityMapBoxTest activity = weakReference.get();
                if (activity != null) {
                    List<Point> routeCoordinates = new ArrayList<>();
                    PositionProvider.getDummyPosition().forEach(position -> routeCoordinates.add(Point.fromLngLat(position.getLongitude(),position.getLatitude())));
                    return FeatureCollection.fromFeatures(new Feature[] {Feature.fromGeometry(LineString.fromLngLats(routeCoordinates))});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(@Nullable FeatureCollection featureCollection) {
            super.onPostExecute(featureCollection);
            ActivityMapBoxTest activity = weakReference.get();
            if (activity != null && featureCollection != null) {
                activity.drawLines(featureCollection);
            }
        }
    }

    /**
     * generates a new instance of the class {@link de.thu.tpro.android4bikes.data.model.BikeRack} for test purposes
     *
     * @return instance of the class {@link de.thu.tpro.android4bikes.data.model.BikeRack}
     */
    private BikeRack generateTHUBikeRack(int i) {
        //create new BikeRack
        BikeRack bikeRack_THU = new BikeRack(
                "pfo4eIrvzrI0m363KF0K", new Position(48.408880 + i / 1000.0, 9.997507 + i / 1000.0), "THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true, false
        );
        return bikeRack_THU;
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
            SymbolOptions marker = createMarker(track.getFineGrainedPositions().get(0).getLatitude(), track.getFineGrainedPositions().get(0).getLongitude(), MapBoxSymbols.TRACK);
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
            SymbolOptions marker = createMarker(bikeRack.getPosition().getLatitude(), bikeRack.getPosition().getLongitude(), MapBoxSymbols.BIKERACK);
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
            SymbolOptions marker = createMarker(hazardAlert.getPosition().getLatitude(), hazardAlert.getPosition().getLongitude(), MapBoxSymbols.HAZARDALERT_GENERAL);
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
                iconImage(MapBoxSymbols.BIKERACK.toString())/*,
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
                new int[]{150, ContextCompat.getColor(this, R.color.mapbox_blue)},
                new int[]{20, ContextCompat.getColor(this, R.color.mapbox_blue)},
                new int[]{0, ContextCompat.getColor(this, R.color.mapbox_blue)}
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

    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
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
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

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
        Toast.makeText(this, R.string.user_location_permission_explanation,
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
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Prevent leaks
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void initPosFab() {
        FloatingActionButton FAB = findViewById(R.id.fab_pos);
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

    private void initMarkerSymbols(MapboxMap mapboxMap, HashMap<MapBoxSymbols, Drawable> markers) {
        //Check if marker HashMap is empty
        if (!markers.isEmpty()) {
            //Register Symbols in Mapbox
            markers.forEach((type, icon) -> mapboxMap.getStyle().addImage(type.toString(), icon));
        }
    }

    private SymbolOptions createMarker(double latitude, double longitude, MapBoxSymbols type) {
        return new SymbolOptions()
                .withLatLng(new LatLng(latitude, longitude))
                .withIconImage(type.toString());
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

        private final WeakReference<ActivityMapBoxTest> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(ActivityMapBoxTest activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            ActivityMapBoxTest activity = activityWeakReference.get();

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
            ActivityMapBoxTest activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}