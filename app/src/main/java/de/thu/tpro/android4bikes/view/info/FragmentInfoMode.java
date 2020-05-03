package de.thu.tpro.android4bikes.view.info;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
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
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
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

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.expressions.Expression.all;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.gte;
import static com.mapbox.mapboxsdk.style.expressions.Expression.has;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.not;
import static com.mapbox.mapboxsdk.style.expressions.Expression.toNumber;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;


public class FragmentInfoMode extends Fragment implements OnMapReadyCallback, PermissionsListener, Observer {

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
    private boolean hazardLayer_created;
    private boolean bikerackLayer_created;
    private boolean trackLayer_created;
    private boolean routeLayer_created;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewInfo = inflater.inflate(R.layout.fragment_info_mode, container, false);

        // init ViewModels
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
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

        hazardLayer_created = false;
        bikerackLayer_created = false;
        routeLayer_created = false;
        trackLayer_created = false;

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
        updateHazardAlertOverlay(style, hazardList);
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
        updateBikeRackOverlay(style, bikeRackList);
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
        updateTrackOverlay(style, list_tracks);
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
                    markerPool.put(FragmentInfoMode.MapBoxSymbols.BIKERACK, parent.getDrawable(R.drawable.ic_material_bikerack));
                    markerPool.put(FragmentInfoMode.MapBoxSymbols.HAZARDALERT_GENERAL, parent.getDrawable(R.drawable.ic_material_hazard));
                    markerPool.put(FragmentInfoMode.MapBoxSymbols.TRACK, parent.getDrawable(R.drawable.ic_flag_green_24dp));
                    markerPool.put(MapBoxSymbols.TRACK_FINISH, parent.getDrawable(R.drawable.flag_finish_green_24dp));
                    initMarkerSymbols(mapboxMap, markerPool);
                    //generateCustomRoute(generateTrack().getFineGrainedPositions());
                    initPosFab();

                    //set class attribute "style" (purpose: adding new markers after update regarding ViewModels)
                    this.style = style;

                    //register for ViewModels after the map is ready:
                    vm_Hazards.getHazardAlerts().observe(getViewLifecycleOwner(), this::onChangedHazardAlerts);
                    vm_bikeRack.getList_bikeRacks_shown().observe(getViewLifecycleOwner(), this::onChangedBikeRacks);
                    vm_Tracks.getTracks().observe(getViewLifecycleOwner(), this::onChangedTracks);
                    Log.d("HalloWeltAUA", "InfoMode:" + vm_Tracks.toString());


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
                                                Track track_result = tracks.stream()
                                                        .filter(track -> entry.getValue().getAsString().equals(track.getFirebaseID()))
                                                        .findFirst()
                                                        .orElse(null);
                                                if (track_result != null) {
                                                    //set track for navigation mode
                                                    vm_Tracks.setNavigationTrack(track_result);

                                                    addRoutetoMap(style, track_result);
                                                    showRoutewithCamera(track_result.getStartPosition().getAsPoint(), track_result.getEndPosition().getAsPoint());
                                                    parent.fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(parent, R.color.Green800Primary)));

                                                }else {
                                                    //Was Bikerack or HazardALert
                                                    vm_Tracks.setNavigationTrack(null);

                                                    List<BikeRack> racks = vm_bikeRack.getList_bikeRacks_shown().getValue();
                                                    BikeRack rack_result = racks.stream()
                                                            .filter(rack -> entry.getValue().getAsString().equals(rack.getFirebaseID()))
                                                            .findFirst()
                                                            .orElse(null);
                                                    if (rack_result != null){
                                                        Toast toast = Toast.makeText(getContext(),"Bike rack:\t\t\t\t\t\t\t\t"+rack_result.getName()
                                                                        +"\n"+"Capacity:\t\t\t\t\t\t\t\t"+rack_result.getCapacity().name()
                                                                        +"\n"+"e-Bike charging:\t\t"+(rack_result.hasBikeCharging() ? "available" : "not available")
                                                                        +"\n"+"Is it covered:\t\t\t\t\t"+(rack_result.isCovered()? "yes" : "no")
                                                                ,Toast.LENGTH_LONG);
                                                        toast.setGravity(Gravity.TOP,0,50);
                                                        toast.show();
                                                    }else {
                                                        List<HazardAlert> hazards = vm_Hazards.getHazardAlerts().getValue();
                                                        HazardAlert hazard_result = hazards.stream()
                                                                .filter(hazard -> entry.getValue().getAsString().equals(hazard.getFirebaseID()))
                                                                .findFirst()
                                                                .orElse(null);
                                                        if (hazard_result != null){
                                                            Toast toast = Toast.makeText(getContext(),"Hazard type: "+HazardAlert.HazardType.getByType(hazard_result.getType()),Toast.LENGTH_LONG);
                                                            toast.setGravity(Gravity.TOP,0,50);
                                                            toast.show();
                                                        }
                                                    }
                                                    //return handleClickIcon(pixel);
                                                }
                                            } else {
                                                vm_Tracks.setNavigationTrack(null);
                                                //TODO: remove drawn track from map
                                            }
                                        }
                                    }
                                }
                            }
                            return false;
                        }
                    });
                    if (PositionTracker.getLastPosition().isValid()) {
                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                .target(PositionTracker.getLastPosition().toMapboxLocation())
                                .zoom(15)
                                .build());
                    } else {
                        Position germany_center = new Position(51.163361111111, 10.447683333333);
                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                .target(germany_center.toMapboxLocation())
                                .zoom(4)
                                .build());
                        //Observe Position
                        PositionTracker.LocationChangeListeningActivityLocationCallback.getInstance(parent).addObserver(this);
                    }
                    geoFencing_tracks.startGeoFenceListener();
                    /*//Draw Route on Map
                    mapview.drawRoute(route);
                    showRoute(start,end);*/
                    //https://docs.mapbox.com/android/java/examples/show-directions-on-a-map/
                    //Feature directionsRouteFeature = Feature.fromGeometry(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                });
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof PositionTracker.LocationChangeListeningActivityLocationCallback) {
            if (o instanceof Map && !((Map) o).keySet().isEmpty()) {
                Map map = (Map) o;
                if (map.get(PositionTracker.CONSTANTS.POSITION.toText()) != null) {
                    Position last_position = (Position) map.get(PositionTracker.CONSTANTS.POSITION.toText());
                    if (last_position.isValid()) {
                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(last_position.toMapboxLocation())
                                .zoom(15)
                                .bearing(0)
                                .build()), 3000);
                        observable.deleteObserver(this);
                    }

                }

            }
        }
    }

    private SymbolOptions createMarker(double latitude, double longitude, FragmentInfoMode.MapBoxSymbols type) {
        return new SymbolOptions()
                .withLatLng(new LatLng(latitude, longitude))
                .withIconImage(type.toString());
    }

    private void showRoutewithCamera(com.mapbox.geojson.Point start, com.mapbox.geojson.Point end) {
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng(start.latitude(), start.longitude())) // Northeast
                .include(new LatLng(end.latitude(), end.longitude())) // Southwest
                .build();
        mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);
    }

    private void addRoutetoMap(@NonNull Style loadedMapStyle, Track track) {
        if (!routeLayer_created) {
            createRouteLayer(loadedMapStyle, GeoFencing.ConstantsGeoFencing.COLLECTION_ROUTE.toString());
            createFinishLayer(loadedMapStyle, GeoFencing.ConstantsGeoFencing.FINISH_FLAG.toString());
            routeLayer_created = true;
        }
        addGeoJsonSource(loadedMapStyle, LineString.fromPolyline(track.getRoute().geometry(), PRECISION_6),
                GeoFencing.ConstantsGeoFencing.COLLECTION_ROUTE.toString());
        addGeoJsonSource(loadedMapStyle, track.getEndPosition().getAsPoint(), GeoFencing.ConstantsGeoFencing.FINISH_FLAG.toString());
    }
    /**
     * Create Markers from Track List
     *
     * @param loadedMapStyle
     * @param tracks
     */
    private void updateTrackOverlay(@NonNull Style loadedMapStyle, List<Track> tracks) {
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
            addGeoJsonSource(loadedMapStyle, featureCollection, GeoFencing.ConstantsGeoFencing.COLLECTION_TRACKS.toString(), true);
            if (!trackLayer_created) {
                createUnclusteredSymbolLayer(loadedMapStyle, GeoFencing.ConstantsGeoFencing.COLLECTION_TRACKS.toString(), type);
                //Create clustered circle layer
                createClusteredCircleOverlay(loadedMapStyle, GeoFencing.ConstantsGeoFencing.COLLECTION_TRACKS.toString(), type);
                trackLayer_created = true;
            }

        }
    }

    /**
     * Create Markers from BikeRack List
     *
     * @param loadedMapStyle
     * @param bikeRacks
     */
    private void updateBikeRackOverlay(@NonNull Style loadedMapStyle, List<BikeRack> bikeRacks) {
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
            for (Feature singleFeature : featureCollection.features()) {
                singleFeature.addBooleanProperty("selected", false);
            }
            //Create unclustered symbol layer
            addGeoJsonSource(loadedMapStyle, featureCollection, GeoFencing.ConstantsGeoFencing.COLLECTION_BIKERACKS.toString(), true);
            //new GenerateViewIconTask(parent).execute(featureCollection);
            if (!bikerackLayer_created) {
                //createInfoWindowLayer(style,,);
                createUnclusteredSymbolLayer(loadedMapStyle, GeoFencing.ConstantsGeoFencing.COLLECTION_BIKERACKS.toString(), type);
                //Create clustered circle layer
                createClusteredCircleOverlay(loadedMapStyle, GeoFencing.ConstantsGeoFencing.COLLECTION_BIKERACKS.toString(), type);
                bikerackLayer_created = true;
            }

        }
    }

    /**
     * Create markers from HazardAlert list
     *
     * @param loadedMapStyle
     * @param hazardAlerts
     */
    private void updateHazardAlertOverlay(@NonNull Style loadedMapStyle, List<HazardAlert> hazardAlerts) {
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
            for (Feature singleFeature : featureCollection.features()) {
                singleFeature.addBooleanProperty("selected", false);
            }
            addGeoJsonSource(loadedMapStyle, featureCollection, GeoFencing.ConstantsGeoFencing.COLLECTION_HAZARDS.toString(), true);
            //new GenerateViewIconTask(parent).execute(featureCollection);
            if (!hazardLayer_created) {
                //createInfoWindowLayer(style,,);
                //Create unclustered symbol layer
                createUnclusteredSymbolLayer(loadedMapStyle, GeoFencing.ConstantsGeoFencing.COLLECTION_HAZARDS.toString(), type);
                //Create clustered circle layer
                createClusteredCircleOverlay(loadedMapStyle, GeoFencing.ConstantsGeoFencing.COLLECTION_HAZARDS.toString(), type);
                hazardLayer_created = true;
            }

        }
    }

    private void addGeoJsonSource(@NonNull Style loadedMapStyle, FeatureCollection featureCollection, String ID, boolean withCluster) {
        //Check if Source is initialized
        GeoJsonSource mapStyleSource = loadedMapStyle.getSourceAs(ID);
        if (mapStyleSource != null) {
            mapStyleSource.setGeoJson(featureCollection);
        } else {
            if (withCluster) {
                if (ID.equals(GeoFencing.ConstantsGeoFencing.COLLECTION_BIKERACKS.toString()) || ID.equals(GeoFencing.ConstantsGeoFencing.COLLECTION_HAZARDS.toString())) {
                    GeoJsonSource geoJsonSource = new GeoJsonSource(ID, featureCollection, new GeoJsonOptions()
                            .withCluster(true)
                            .withClusterRadius(50)
                            //.withClusterMaxZoom(10)
                            .withMinZoom(13)
                    );
                    loadedMapStyle.addSource(geoJsonSource);
                } else {
                    GeoJsonSource geoJsonSource = new GeoJsonSource(ID, featureCollection, new GeoJsonOptions()
                            .withCluster(true)
                            .withClusterRadius(50)
                            //.withClusterMaxZoom(10)
                            .withMaxZoom(13)
                            .withMinZoom(4)
                    );
                    loadedMapStyle.addSource(geoJsonSource);
                }


            } else {
                GeoJsonSource geoJsonSource = new GeoJsonSource(ID, featureCollection);
                loadedMapStyle.addSource(geoJsonSource);
            }

        }

    }

    private void addGeoJsonSource(@NonNull Style loadedMapStyle, Geometry geometry, String ID) {
        //Check if Source is initialized
        GeoJsonSource mapStyleSource = loadedMapStyle.getSourceAs(ID);
        if (mapStyleSource != null) {
            mapStyleSource.setGeoJson(geometry);
        } else {
            GeoJsonSource geoJsonSource = new GeoJsonSource(ID, FeatureCollection.fromFeatures(new Feature[]{
                    Feature.fromGeometry(geometry)}));
            loadedMapStyle.addSource(geoJsonSource);
        }
    }

    private void createRouteLayer(@NonNull Style loadedMapStyle, String ID) {
        LineLayer routeLayer = new LineLayer("Tracks_" + ID, ID);

        // Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(ContextCompat.getColor(parent, R.color.Green800Primary))
        );
        loadedMapStyle.addLayer(routeLayer);

    }

    private void createFinishLayer(@NonNull Style loadedMapStyle, String ID) {
        // Add the Finish SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer("Finish_" + ID, ID).withProperties(
                iconImage(MapBoxSymbols.TRACK_FINISH.toString()),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[]{0f, -9f})));
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
                iconImage(type.toString())
        );
        // Add a filter to the cluster layer that hides the circles based on "point_count"
        unclustered.setFilter(all(not(has("point_count"))));
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
            //Add clusters' circles
        CircleLayer circles = new CircleLayer("clustered_" + sourceID, sourceID);
            circles.setProperties(
                    circleOpacity(0.6f),
                    circleColor(ContextCompat.getColor(parent, color)),
                    circleRadius(15f)
            );

            Expression pointCount = toNumber(get("point_count"));

            // Add a filter to the cluster layer that hides the circles based on "point_count"
            circles.setFilter(
                    all(has("point_count"),
                            gte(pointCount, literal(0)
                            )
                    ));

            loadedMapStyle.addLayer(circles);

        //Add the count labels
        SymbolLayer count = new SymbolLayer("count_" + sourceID, sourceID);
        count.setProperties(
                textField(Expression.toString(get("point_count"))),
                textSize(12f),
                textColor(Color.WHITE),
                textIgnorePlacement(true),
                textAllowOverlap(false)
        );
        loadedMapStyle.addLayer(count);
    }

    /*private void createInfoWindowLayer(@NonNull Style loadedStyle, String ID, MapBoxSymbols type) {
        loadedStyle.addLayer(new SymbolLayer("info_"+ID, ID) //todo: 2nd param should be maybe the id of the source of the already existing markers
                .withProperties(
                        ///* show image with id title based on the value of the name feature property
                        iconImage("{name}"), //todo: check if this is right
                        ///* set anchor of icon to bottom-left
                        iconAnchor(ICON_ANCHOR_BOTTOM),
                        ///* all info window and marker image to appear at the same time
                        iconAllowOverlap(true),
                        ///* offset the info window to be above the marker
                        iconOffset(new Float[] {-2f, -28f})
                )
                ///* add a filter to show only when selected feature property is true
                .withFilter(eq((get("selected")), literal(true))));
    }*/

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
        btnPos.setOnClickListener(view -> {
            Position curr = PositionTracker.getLastPosition();
            if (curr != null) {
                HazardAlert newHazard = new HazardAlert(HazardAlert.HazardType.getByType(spinnerHazard.getSelectedItemPosition() + 1), // i+1 since we start counting on 1
                        curr,
                        10,
                        true
                );
                vm_ownHazards.addOwnHazard(newHazard);
            }
            hazardDialog.dismiss();
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


    private enum MapBoxSymbols {
        BIKERACK("BIKERACK"),
        HAZARDALERT_GENERAL("HAZARDALERT_GENERAL"),
        TRACK("TRACK"),
        TRACK_FINISH("TRACK_FINISH"),
        INFO("INFO");

        private String type;

        MapBoxSymbols(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
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
        builder.setNegativeButton("Dismiss", (dialogInterface, i) -> Snackbar.make(viewInfo.findViewById(R.id.map_container_info), "Dismiss", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show());
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary, parent.getTheme()));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary, parent.getTheme()));
        });
        dialog.show();

        MapView rackMap = dialog.findViewById(R.id.rackMap);
        rackMap.onCreate(dialog.onSaveInstanceState());

        rackMap.getMapAsync(mapboxMapRack -> mapboxMapRack.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
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
        }));

        EditText editRack = dialog.findViewById(R.id.edit_rack_name);
        Spinner spCapacity = dialog.findViewById(R.id.sp_capacity);
        CheckBox cbEBike = dialog.findViewById(R.id.chBx_ebike);
        CheckBox cbCovered = dialog.findViewById(R.id.chBx_covered);
        Button btnPos = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNeg = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btnPos.setOnClickListener(view -> {
            if (editRack.getText().toString().trim().equals("")) {
                Snackbar.make(viewInfo.findViewById(R.id.map_container_info), "Pleas fill in rack name", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
            } else {
                Position currLastPos = PositionTracker.getLastPosition();
                if (currLastPos != null) {
                    BikeRack newRack = new BikeRack(currLastPos, editRack.getText().toString(),
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
    }
}
