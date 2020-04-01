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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.util.GlobalContext;

public class MapViewContentBuilder implements OnMapReadyCallback {


    private Location currentLocation;
    private GoogleMap googleMap;
    private FusedLocationProviderClient flpc;
    private Activity parent;
    private SupportMapFragment mapFragment;

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

                    // set callback listener on Google Map ready
                    mapFragment.getMapAsync(MapViewContentBuilder.this::onMapReady);
                }
            }
        });
        return this;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My current location");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        googleMap.addMarker(markerOptions);
    }
    public SupportMapFragment build(){
        return mapFragment;
    }
}
