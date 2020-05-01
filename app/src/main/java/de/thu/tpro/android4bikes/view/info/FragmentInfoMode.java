package de.thu.tpro.android4bikes.view.info;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.JsonElement;
import com.mapbox.android.core.location.LocationEngineCallback;
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
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
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
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
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
import java.util.Map;
import java.util.Set;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.services.PositionTracker;
import de.thu.tpro.android4bikes.util.GeoFencing;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.util.GpsUtils;
import de.thu.tpro.android4bikes.view.MainActivity;
import de.thu.tpro.android4bikes.viewmodel.ViewModelBikerack;
import de.thu.tpro.android4bikes.viewmodel.ViewModelHazardAlert;
import de.thu.tpro.android4bikes.viewmodel.ViewModelOwnBikerack;
import de.thu.tpro.android4bikes.viewmodel.ViewModelOwnHazardAlerts;
import de.thu.tpro.android4bikes.viewmodel.ViewModelOwnTracks;
import de.thu.tpro.android4bikes.viewmodel.ViewModelTrack;

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
    private static final String MAPFRAGMENT_TAG = "mapFragmentTAG";
    private static final String TAG = "DirectionsActivity";

    private static LatLng latLng_lastcamerapos;

    //OWN ViewModels (=OWN DATA CREATED BY THIS USER!!!!)
    private ViewModelOwnBikerack vm_ownBikeRack;
    private ViewModelOwnHazardAlerts vm_ownHazards;
    private ViewModelOwnTracks vm_ownTracks;
    private Style style;

    //Regular ViewModels (=DATA FROM THE GEOFENCE!!!)
    private ViewModelBikerack vm_bikeRack;
    private ViewModelHazardAlert vm_Hazards;
    private ViewModelTrack vm_Tracks;

    private MainActivity parent;
    private View viewInfo;
    private SupportMapFragment mapFragment;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private LatLng lastPos;
    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    SymbolManager symbolManager;

    //GeoFencing
    private GeoFencing geoFencing_bikeRacks;
    private GeoFencing geoFencing_hazardAlerts;
    private GeoFencing geoFencing_tracks;
    private boolean fenceAlreadyRunning;
    private boolean fenceAlreadyStopped;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewInfo = inflater.inflate(R.layout.fragment_info_mode, container, false);

        // init ViewModels
        ViewModelProvider provider = new ViewModelProvider(this);
        //own ViewModels
        vm_ownBikeRack = provider.get(ViewModelOwnBikerack.class);
        vm_ownHazards = provider.get(ViewModelOwnHazardAlerts.class);
        vm_ownTracks = provider.get(ViewModelOwnTracks.class);

        //general ViewModels
        vm_Hazards = provider.get(ViewModelHazardAlert.class);
        vm_Tracks = provider.get(ViewModelTrack.class);
        vm_bikeRack = provider.get(ViewModelBikerack.class);

        return viewInfo;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //we need the parent Activity to init our map
        parent = (MainActivity) this.getActivity();
        GlobalContext.setContext(parent.getApplicationContext());

        //start with GeoFencing:
        geoFencing_bikeRacks = vm_bikeRack.getGeoFencing_bikeRacks();
        geoFencing_hazardAlerts = vm_Hazards.getGeoFencing_hazardAlerts();
        geoFencing_tracks = vm_Tracks.getGeoFencing_tracks();

        fenceAlreadyRunning = false;
        fenceAlreadyStopped = true;

        initMap(savedInstanceState);
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
        updateHazardAlertOverlay(style, hazardList, GeoFencing.ConstantsGeoFencing.COLLECTION_HAZARDS.toString());
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
        updateBikeRackOverlay(style, bikeRackList, GeoFencing.ConstantsGeoFencing.COLLECTION_BIKERACKS.toString());
    }

    private void onChangedTracks(Map<Track, Profile> trackProfileMap) {
        List<Track> list_tracks = new ArrayList<>();
        for (Track t : trackProfileMap.keySet()) {
            list_tracks.add(t);
        }
        //TODO: delete overlay

        List<Track> cleared = new ArrayList<>();
        list_tracks.forEach(entry -> {
            if (entry.getStartPosition() != null) {
                cleared.add(entry);
            }
        });
        list_tracks = cleared;
        updateTrackOverlay(style, list_tracks, GeoFencing.ConstantsGeoFencing.COLLECTION_TRACKS.toString());
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
            Mapbox.getInstance(parent, parent.getString(R.string.access_token));
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
                    markerPool.put(FragmentInfoMode.MapBoxSymbols.BIKERACK, parent.getDrawable(R.drawable.ic_material_parking));
                    markerPool.put(FragmentInfoMode.MapBoxSymbols.HAZARDALERT_GENERAL, parent.getDrawable(R.drawable.ic_material_hazard));
                    markerPool.put(FragmentInfoMode.MapBoxSymbols.TRACK, parent.getDrawable(R.drawable.ic_flag_green_24dp));
                    initMarkerSymbols(mapboxMap, markerPool);
                    //generateCustomRoute(generateTrack().getFineGrainedPositions());
                    initPosFab();

                    //set class attribute "style" (purpose: adding new markers after update regarding ViewModels)
                    this.style = style;

                    //register for ViewModels after the map is ready:
                    vm_Hazards.getHazardAlerts().observe(getViewLifecycleOwner(), this::onChangedHazardAlerts);
                    vm_bikeRack.getList_bikeRacks_shown().observe(getViewLifecycleOwner(), this::onChangedBikeRacks);
                    vm_Tracks.getTracks().observe(getViewLifecycleOwner(), this::onChangedTracks);


                    //setup geofences
                    LatLng latlng_setuppos = mapboxMap.getCameraPosition().target;
                    GeoPoint geopoint_setuppos = new GeoPoint(latlng_setuppos.getLatitude(), latlng_setuppos.getLongitude());

                    geoFencing_bikeRacks.setupGeofence(geopoint_setuppos, 50);
                    geoFencing_hazardAlerts.setupGeofence(geopoint_setuppos, 50);
                    geoFencing_tracks.setupGeofence(geopoint_setuppos, 1000);



                    mapboxMap.addOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
                        @Override
                        public void onCameraMove() {
                            if (latLng_lastcamerapos == null) {
                                latLng_lastcamerapos = mapboxMap.getCameraPosition().target;
                            }
                            if (mapboxMap.getCameraPosition().zoom > 13) {
                                if (!fenceAlreadyRunning){
                                    geoFencing_bikeRacks.startGeoFenceListener();
                                    geoFencing_hazardAlerts.startGeoFenceListener();
                                    fenceAlreadyRunning = true;
                                    fenceAlreadyStopped = false;
                                }
                            } else {
                                if (!fenceAlreadyStopped){
                                    geoFencing_bikeRacks.stopGeoFenceListener();
                                    geoFencing_hazardAlerts.stopGeoFenceListener();
                                    mapboxMap.getStyle(style1 -> {
                                        deleteLayers(style1, GeoFencing.ConstantsGeoFencing.COLLECTION_BIKERACKS.toString());
                                        deleteLayers(style1, GeoFencing.ConstantsGeoFencing.COLLECTION_HAZARDS.toString());
                                    });
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

                            if (geoFencing_tracks != null && distance / 1000 > (geoFencing_tracks.getRadius() / 2)) {
                                geoFencing_tracks.updateCenter(geopoint_currPos);
                                latLng_lastcamerapos = latlng_currCameraPos;
                            }

                        }
                    });


                    mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                        @Override
                        public boolean onMapClick(@NonNull LatLng point) {
                            // Convert LatLng coordinates to screen pixel and only query the rendered features.
                            final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);

                            List<Feature> features = mapboxMap.queryRenderedFeatures(pixel);

                            // Get the first feature within the list if one exist
                            if (features.size() > 0) {
                                for (Feature feature : features) {
                                    if (feature.properties() != null) {
                                        for (Map.Entry<String, JsonElement> entry : feature.properties().entrySet()) {
                                            // Log all the properties
                                            if (entry.getKey().equals("ID")) {
                                                Set<Track> tracks = vm_Tracks.getTracks().getValue().keySet();
                                                Track result = tracks.stream()
                                                        .filter(track -> entry.getValue().getAsString().equals(track.getFirebaseID()))
                                                        .findFirst()
                                                        .orElse(null);
                                                if (result != null){
                                                    Log.d("HalloWelt","Trackname: "+result.getName());
                                                }else {
                                                    Log.d("HalloWelt","Track was null :(");
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                            return false;
                        }
                    });

                    geoFencing_tracks.startGeoFenceListener();
                    /*//Draw Route on Map
                    mapview.drawRoute(route);
                    showRoute(start,end);*/
                    //https://docs.mapbox.com/android/java/examples/show-directions-on-a-map/
                    //Feature directionsRouteFeature = Feature.fromGeometry(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                });
    }

    private SymbolOptions createMarker(double latitude, double longitude, FragmentInfoMode.MapBoxSymbols type) {
        return new SymbolOptions()
                .withLatLng(new LatLng(latitude, longitude))
                .withIconImage(type.toString());
    }

    private void showRoute(com.mapbox.geojson.Point start, com.mapbox.geojson.Point end) {
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng(start.latitude(), start.longitude())) // Northeast
                .include(new LatLng(end.latitude(), end.longitude())) // Southwest
                .build();
        mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);
    }

    /**
     * Create Markers from Track List
     *
     * @param loadedMapStyle
     * @param tracks
     */
    private void updateTrackOverlay(@NonNull Style loadedMapStyle, List<Track> tracks, String dataSourceID) {
        synchronized (this) {
            //Generate Data Source
            MapBoxSymbols type = MapBoxSymbols.TRACK;
            List<Feature> list_feature = new ArrayList<>();
            //Generate Markers from ArrayList
            for (Track track : tracks) {
                //getStringProperty
                SymbolOptions marker = createMarker(track.getStartPosition().getLatitude(), track.getStartPosition().getLongitude(), FragmentInfoMode.MapBoxSymbols.TRACK);
                Feature feature = Feature.fromGeometry(marker.getGeometry());
                feature.addStringProperty("ID", track.getFirebaseID());
                //TODO add info for Popup
                list_feature.add(feature);
            }
            //Create FeatureCollection from Feature List
            FeatureCollection featureCollection = FeatureCollection.fromFeatures(list_feature);
            //Create unclustered symbol layer
            String id = addGeoJsonSource(loadedMapStyle, featureCollection, dataSourceID, true, 50);
            createUnclusteredSymbolLayer(loadedMapStyle, id, type);
            //Create clustered circle layer
            createClusteredCircleOverlay(loadedMapStyle, id, type);
        }
    }

    /**
     * Create Markers from BikeRack List
     *
     * @param loadedMapStyle
     * @param bikeRacks
     */
    private void updateBikeRackOverlay(@NonNull Style loadedMapStyle, List<BikeRack> bikeRacks, String dataSourceID) {
        synchronized (this) {
            //Generate Data Source
            MapBoxSymbols type = MapBoxSymbols.BIKERACK;
            List<Feature> list_feature = new ArrayList<>();
            //Generate Markers from ArrayList
            for (BikeRack bikeRack : bikeRacks) {
                SymbolOptions marker = createMarker(bikeRack.getPosition().getLatitude(), bikeRack.getPosition().getLongitude(), FragmentInfoMode.MapBoxSymbols.BIKERACK);
                Feature feature = Feature.fromGeometry(marker.getGeometry());
                feature.addStringProperty("ID", bikeRack.getFirebaseID());
                //TODO add info for Popup
                list_feature.add(feature);
            }
            //Create FeatureCollection from Feature List
            FeatureCollection featureCollection = FeatureCollection.fromFeatures(list_feature);
            //Create unclustered symbol layer
            String id = addGeoJsonSource(loadedMapStyle, featureCollection, dataSourceID, true, 50);
            createUnclusteredSymbolLayer(loadedMapStyle, id, type);
            //Create clustered circle layer
            createClusteredCircleOverlay(loadedMapStyle, id, type);
        }
    }

    /**
     * Create markers from HazardAlert list
     *
     * @param loadedMapStyle
     * @param hazardAlerts
     */
    private void updateHazardAlertOverlay(@NonNull Style loadedMapStyle, List<HazardAlert> hazardAlerts, String dataSourceID) {
        synchronized (this) {
            //Generate Data Source

            // Create Markers from data and set the 'cluster' option to true.
            MapBoxSymbols type = MapBoxSymbols.HAZARDALERT_GENERAL;
            List<Feature> list_feature = new ArrayList<>();
            //Generate Markers from ArrayList
            for (HazardAlert hazardAlert : hazardAlerts) {
                SymbolOptions marker = createMarker(hazardAlert.getPosition().getLatitude(), hazardAlert.getPosition().getLongitude(), type);
                Feature feature = Feature.fromGeometry(marker.getGeometry());
                feature.addStringProperty("ID", hazardAlert.getFirebaseID());
                //TODO add info for Popup
                list_feature.add(feature);
            }
            //Create FeatureCollection from Feature List
            FeatureCollection featureCollection = FeatureCollection.fromFeatures(list_feature);
            //Create unclustered symbol layer
            String id = addGeoJsonSource(loadedMapStyle, featureCollection, dataSourceID, true, 50);
            createUnclusteredSymbolLayer(loadedMapStyle, id, type);
            //Create clustered circle layer
            createClusteredCircleOverlay(loadedMapStyle, id, type);
        }
    }

    private void deleteLayers(@NonNull Style loadedMapStyle, String ID) {
        for (Source source : loadedMapStyle.getSources()) {
            if (source.getId().equals(ID)) {

                loadedMapStyle.removeLayer("unclustered_" + ID);
                loadedMapStyle.removeLayer("clustered_" + ID + 0);
                loadedMapStyle.removeLayer("clustered_" + ID + 1);
                loadedMapStyle.removeLayer("clustered_" + ID + 2);
                loadedMapStyle.removeLayer("count_" + ID);
                loadedMapStyle.removeSource(ID);
            }
        }
    }
    private String addGeoJsonSource(@NonNull Style loadedMapStyle, FeatureCollection featureCollection, String ID, boolean withCluster, int clusterRadius) {
        //TODO: ONLY ONCE FOR PRESENTATION PURPOSE

        //Check if Source is initialized
        deleteLayers(loadedMapStyle, ID);
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
    private void createUnclusteredSymbolLayer(@NonNull Style loadedMapStyle, String sourceID, MapBoxSymbols type) {
        //Create Symbol Layer for unclustered data points
        SymbolLayer unclustered = new SymbolLayer("unclustered_" + sourceID, sourceID);


        unclustered.setProperties(
                iconImage(type.toString())/*,
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
    private void createClusteredCircleOverlay(@NonNull Style loadedMapStyle, String sourceID, MapBoxSymbols type) {
        int color = R.color.mapbox_blue; //default
        switch (type) {
            case BIKERACK:
                color = R.color.Blue400Dark;
                break;
            case HAZARDALERT_GENERAL:
                color = R.color.Amber800Light;
                break;
            case TRACK:
                color = R.color.Green800Primary;
                break;
        }
        // Each point range gets a different fill color.
        int[][] layers = new int[][]{
                new int[]{150, ContextCompat.getColor(parent, color)},
                new int[]{20, ContextCompat.getColor(parent, color)},
                new int[]{0, ContextCompat.getColor(parent, color)}
        };

        for (int i = 0; i < layers.length; i++) {
            //Add clusters' circles
            CircleLayer circles = new CircleLayer("clustered_" + sourceID + i, sourceID);
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
        SymbolLayer count = new SymbolLayer("count_" + sourceID, sourceID);
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

            //Check if GPS is enabled on device
            //parent.checkLocationEnabled();
            new GpsUtils(getActivity()).turnGPSOn(new GpsUtils.onGpsListener() {
                @Override
                public void gpsStatus(boolean isGPSEnable) {
                    // turn on GPS
                    //isGPS = isGPSEnable;
                }
            });

            // Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(parent, loadedMapStyle)
                            .useDefaultLocationEngine(true)
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
            parent.initLocationEngine();
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

    public void showTrackFeedback() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        dialogBuilder.setTitle("Store your Track!");
        dialogBuilder.setView(R.layout.dialog_feedback_track);
        dialogBuilder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Snackbar.make(viewInfo.findViewById(R.id.map_container_info), "Store into Firestore", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
            }
        });

        dialogBuilder.setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Snackbar.make(viewInfo.findViewById(R.id.map_container_info), "Don´t store ", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
            }
        });
        dialogBuilder.show();
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
                                Snackbar.make(viewInfo.findViewById(R.id.map_container_info), "default", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
                        }
                    }
                })
                .create();
        markerDialog.show();
        markerDialog.setCanceledOnTouchOutside(false);
    }

    private void submit_hazard() {
        AlertDialog hazardDialog = new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.submit_hazard)
                .setView(R.layout.dialog_hazard)
                .setPositiveButton(R.string.submit, null)
                .setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Snackbar.make(viewInfo.findViewById(R.id.map_container_info), "Dismiss", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
                    }
                })
                .create();
        hazardDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                hazardDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary, parent.getTheme()));
                hazardDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary, parent.getTheme()));
            }
        });
        hazardDialog.show();
        hazardDialog.setCanceledOnTouchOutside(false);

        MapView hazardMap = hazardDialog.findViewById(R.id.hazardMap);
        hazardMap.onCreate(hazardDialog.onSaveInstanceState());

        hazardMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMapRack) {

                mapboxMapRack.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        if (PositionTracker.getLastPosition() != null) {
                            mapboxMapRack.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                    .target(PositionTracker.getLastPosition().toMapboxLocation())
                                    .zoom(17)
                                    .bearing(0)
                                    .build()), 1000);
                        }
                    }
                });

            }
        });

        Button btnPos = hazardDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Spinner spinnerHazard = hazardDialog.findViewById(R.id.sp_hazards);
        btnPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = spinnerHazard.getSelectedItemPosition();
                // create hazard alert from entered info
                HazardAlert newHazard = new HazardAlert();
                newHazard.setPosition(PositionTracker.getLastPosition()); // TODO is setPosition() or setGeoPoint() correct?
                newHazard.setType(HazardAlert.HazardType.getByType(i + 1)); // i+1 since we start counting on 1

                // submit hazard alert to ViewModel
                vm_ownHazards.addOwnHazard(newHazard);
                hazardDialog.dismiss();
            }
        });

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
                mapboxMap.getStyle(new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                    }
                });
                if (PositionTracker.getLastPosition() != null) {
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(PositionTracker.getLastPosition().toMapboxLocation())
                            .zoom(15)
                            .bearing(0)
                            .build()), 3000);
                }
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

    /**
     * generates a new instance of the class {@link de.thu.tpro.android4bikes.data.model.BikeRack} for test purposes
     *
     * @return instance of the class {@link de.thu.tpro.android4bikes.data.model.BikeRack}
     */

    private BikeRack generateTHUBikeRack(int i) {
        //create new BikeRack
        BikeRack bikeRack_THU = new BikeRack(
                "pfo4eIrvzrI0m363KF0K" + i, new Position(48.408880 + i / 7000.0, 9.997507 + i / 7000.0), "THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
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
    }

    public void submit_Rack() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Submit rack");
        builder.setView(R.layout.dialog_rack);
        builder.setPositiveButton("Submit", null);
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Snackbar.make(viewInfo.findViewById(R.id.map_container_info), "Dismiss", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary, parent.getTheme()));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary, parent.getTheme()));
            }
        });
        dialog.show();

        MapView rackMap = dialog.findViewById(R.id.rackMap);
        rackMap.onCreate(dialog.onSaveInstanceState());

        rackMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMapRack) {

                mapboxMapRack.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        if (PositionTracker.getLastPosition() != null) {
                            mapboxMapRack.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                    .target(PositionTracker.getLastPosition().toMapboxLocation())
                                    .zoom(17)
                                    .bearing(0)
                                    .build()), 1000);
                        }
                    }
                });

            }
        });

        EditText editRack = dialog.findViewById(R.id.edit_rack_name);
        Spinner spCapacity = dialog.findViewById(R.id.sp_capacity);
        CheckBox cbEBike = dialog.findViewById(R.id.chBx_ebike);
        CheckBox cbCovered = dialog.findViewById(R.id.chBx_covered);
        Button btnPos = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNeg = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btnPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editRack.getText().toString().trim().equals("")) {
                    Snackbar.make(viewInfo.findViewById(R.id.map_container_info), "Pleas fill in rack name", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
                } else {
                    BikeRack newRack = new BikeRack();

                    newRack.setName(editRack.getText().toString());
                    newRack.setCapacity(BikeRack.ConstantsCapacity.valueOf(
                            spCapacity.getSelectedItem().toString().toUpperCase())
                    );
                    newRack.setHasBikeCharging(cbEBike.isChecked());
                    newRack.setCovered(cbCovered.isChecked());

                    Log.d(LOG_TAG, newRack.toString());
                    vm_ownBikeRack.addOwnBikeRack(newRack);
                    dialog.dismiss();
                }
            }
        });
    }
}
