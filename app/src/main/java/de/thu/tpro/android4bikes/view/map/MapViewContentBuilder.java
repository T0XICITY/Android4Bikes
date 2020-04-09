package de.thu.tpro.android4bikes.view.map;

import android.app.Activity;
import android.location.Location;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.util.GlobalContext;

public class MapViewContentBuilder implements OnMapReadyCallback {
    private static final int ZOOMLEVEL = 15;

    private int verticalOffset;
    private Location currentLocation;
    private GoogleMap googleMap;
    private FusedLocationProviderClient flpc;
    private Activity parent;
    private SupportMapFragment mapFragment;
    private ClusterManager<HazardAlertItem> clusterManager;
    private List<HazardAlertItem> items = new ArrayList<>();

    public MapViewContentBuilder(Activity parent) {
        //we need the parent Activity to init our map
        this.parent = parent;

        GlobalContext.setContext(parent.getApplicationContext());
        flpc = LocationServices.getFusedLocationProviderClient(parent);
    }

    /**
     * Fetch the last location
     * @param container the surrounding Fragment
     */
    public MapViewContentBuilder fetchLastLocation(Fragment container){
        mapFragment = (SupportMapFragment) container
                .getChildFragmentManager()
                .findFragmentById(R.id.map);

        Task<Location> task = flpc.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;

                    Toast.makeText(parent.getApplicationContext(), currentLocation.getLatitude() +
                            " " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();

                    // set callback listener on Google Map ready
                    mapFragment.getMapAsync(MapViewContentBuilder.this::onMapReady);
                }
            }
        });
        return this;
    }

    /**
     * Method to call the map when is ready to use
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOMLEVEL));

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setPadding(0,0,0,verticalOffset);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        clusterManager = new ClusterManager<HazardAlertItem>(GlobalContext.getContext(), googleMap);
        /*
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_haimage))
                .title("HazardAlert")
        );
        */

        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);


        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                items.add(new HazardAlertItem(latLng));
                clusterManager.addItems(items);
                clusterManager.cluster();
            }


            /*items.add(new MyItem(latLng1));
            clusterManager.addItems(items);
            clusterManager.cluster();*/
        });
    }

    /*private MarkerOptions createMarker(LatLng latLng,String markerName){
        //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.material_bike);
        //TODO: make markercolor/icon diferent, depending on Markertype
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(markerName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        return markerOptions;
    }*/


    public MapViewContentBuilder setVerticalOffset(int offset){
        verticalOffset=offset;
        return this;
    }

    public SupportMapFragment build(){

        return mapFragment;
    }


}


