package de.thu.tpro.android4bikes.view.map;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

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
    private ClusterManager<HazardAlertItem> clusterManager;
    private List<HazardAlertItem> items = new ArrayList<>();
    //boolean mapReady = false;
    private int verticalOffset;
    private GoogleMap googleMap;
    private LatLng latLng;
    private MarkerOptions marker;
    private MarkerOptions markerDR;
    private HazardAlertMarker hazAlrMarker;

    // Constructor
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

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setPadding(0, 0, 0, verticalOffset);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        clusterManager = new ClusterManager<HazardAlertItem>(GlobalContext.getContext(), googleMap);

        Log.i("Hello googleMAP", " WOW" + googleMap.toString());
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                googleMap.addMarker(hazAlrMarker.chooseMarker());
                Log.i(TAG, "GoogleMAp " + googleMap.toString());
                items.add(new HazardAlertItem(latLng));
                clusterManager.addItems(items);
                clusterManager.cluster();

            }


        });

    }


    public MapViewContentBuilder setVerticalOffset(int offset){
        verticalOffset=offset;
        return this;
    }

    public SupportMapFragment build(){
        return mapFragment;
    }


}


