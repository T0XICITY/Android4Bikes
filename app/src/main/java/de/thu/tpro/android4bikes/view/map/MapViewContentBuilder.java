package de.thu.tpro.android4bikes.view.map;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.util.GlobalContext;

public class MapViewContentBuilder implements OnMapReadyCallback {
    private static final int ZOOMLEVEL = 15;
    private static final String TAG = "MapViewBuilder";
    private Location currentLocation;
    private FusedLocationProviderClient flpc;
    private Activity parent;
    public View v;
    private SupportMapFragment mapFragment;
    //private ClusterManager<MarkerItem> clusterManager;
    //private List<MarkerItem> items = new ArrayList<>();
    //boolean mapReady = false;
    private int verticalOffset;
    private GoogleMap googleMap;
    private LatLng latLng;
    private MarkerOptions marker;
    private MarkerOptions markerDR;
    //private BikeRackMarker bikeRackMarker;
    //private BikeTrackMarker bikeTrackMarker;
    // Constructor
    private HazardAlertMarker hazardAlertMarker;
    private BikeRackMarker bikeRackMarker;
    private BikeTrackMarker bikeTrackMarker;

    public MapViewContentBuilder(Activity parent) {
        //we need the parent Activity to init our map
        this.parent = parent;

        GlobalContext.setContext(parent.getApplicationContext());
        flpc = LocationServices.getFusedLocationProviderClient(parent);

    }


    public MapViewContentBuilder fetchLastLocation(Fragment container){
        Task<Location> task = flpc.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    Toast.makeText(parent.getApplicationContext(), currentLocation.getLatitude() +
                            " " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    mapFragment = (SupportMapFragment) container
                            .getChildFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapViewContentBuilder.this::onMapReady);
                }
            }
        });
        return this;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        //mapReady = true;
        googleMap = map;

        Task<Location> task = flpc.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    Toast.makeText(parent.getApplicationContext(), currentLocation.getLatitude() +
                            " " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                }
            }
        });

        latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());


        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOMLEVEL));

        // TODO: remove after testing
        drawcircle(latLng, 50000, googleMap);
        for (int i = 0; i < 15; i++) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latLng.latitude + (i / 20.0), latLng.longitude + (i / 20.0)))
                    .title("Marker" + i));
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latLng.latitude - (i / 20.0), latLng.longitude - (i / 20.0)))
                    .title("Marker" + i));
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latLng.latitude - (i / 20.0), latLng.longitude + (i / 20.0)))
                    .title("Marker" + i));
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latLng.latitude + (i / 20.0), latLng.longitude - (i / 20.0)))
                    .title("Marker" + i));
        }
        //googleMap.addMarker(hazardAlertMarker.chooseMarker());
        //googleMap.addMarker(bikeRackMarker.makeMarker());
        //googleMap.addMarker(bikeTrackMarker.makeMarker());

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setPadding(0,0,0,verticalOffset);
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        //clusterManager = new ClusterManager<MarkerItem>(GlobalContext.getContext(), googleMap);
        //googleMap.setOnCameraIdleListener(clusterManager);
        //googleMap.setOnMarkerClickListener(clusterManager);

        // Add custom markers for HazardAlert
        //googleMap.addMarker(hazardAlertMarker.chooseMarker());

        // Add custom markers for BikeRack
        //googleMap.addMarker(bikeRackMarker.makeMarker());

        // Add custom markers for BikeTrack
        //googleMap.addMarker(bikeTrackMarker.makeMarker());

        /*items.add(new MarkerItem(latLng));
        clusterManager.addItems(items);
        clusterManager.cluster();*/
    }

    // TODO: remove after testing
    public void drawcircle(LatLng latLng, int radius, GoogleMap googleMap) {
        Circle circle = googleMap.addCircle(new CircleOptions().center(latLng).radius(radius).strokeColor(Color.RED).fillColor(Color.TRANSPARENT));
    }

    public MapViewContentBuilder setVerticalOffset(int offset){
        verticalOffset=offset;
        return this;
    }

    public SupportMapFragment build(){
        return mapFragment;
    }
}
