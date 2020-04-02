package de.thu.tpro.android4bikes.view.driving;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.services.GpsLocation;
import de.thu.tpro.android4bikes.view.map.MapViewContentBuilder;

public class FragmentDrivingMode extends Fragment implements LocationListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String LOG_TAG = "FragmentDrivingMode";

    //TODO Delete after testing
    private int cntr = 0;
    private View viewDriving;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewDriving = inflater.inflate(R.layout.fragment_driving_mode, container, false);
        locationPermissions();
        updateSpeed(null);
        return viewDriving;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateMap();
    }

    /**
     * Init Map content with MapViewContentBuilder
     */
    private void populateMap() {
        Log.d(LOG_TAG, "Init Map called");
        //to adjust the Map Controls position TODO: define offset programmatically. Problem height = wrap_content return 0
        int verticalOffest = 0;
        Log.d(LOG_TAG,verticalOffest+"");
        MapViewContentBuilder builder = new MapViewContentBuilder(getActivity());
        builder.setVerticalOffset(verticalOffest).fetchLastLocation(this).build();
    }



    /**
     * Display current Speed into textViews
     *
     * @param location New Location set in onLocationChanged(...)
     */
    private void updateSpeed(GpsLocation location) {
        TextView txtCurrentSpeed = (TextView) viewDriving.findViewById(R.id.txtCurrentSpeed);
        TextView txtLocation = (TextView) viewDriving.findViewById(R.id.txtLocation);
        TextView txtCnt = (TextView) viewDriving.findViewById(R.id.txtCntr);
        float currSpeed = 0f;
        String loc = "";
        if (location != null) {
            currSpeed = location.getSpeed();
            loc = "Latitude " + Double.toString(location.getLatitude()) +
                    "\nLongitude " + Double.toString(location.getLongitude());
        }
        int iSpeed = Math.round(currSpeed);

        txtCurrentSpeed.setText(Integer.toString(iSpeed) + "\nKm/h");
        txtCnt.setText(Integer.toString(cntr));
        txtLocation.setText(loc);
    }
        //TODO: handle permission in a central class
    private void locationPermissions() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        cntr++;
        GpsLocation myLocation = new GpsLocation(location);
        this.updateSpeed(myLocation);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
