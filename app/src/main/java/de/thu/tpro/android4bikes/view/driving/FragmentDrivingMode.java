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

    TextView txtCurrentSpeed;
    TextView txtAvgSpeed;
    //TODO Delete after testing
    private ViewModelDrivingMode viewModel;
    private View viewDrivingMode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewDrivingMode = inflater.inflate(R.layout.fragment_driving_mode, container, false);
        txtCurrentSpeed = viewDrivingMode.findViewById(R.id.txtCurrentSpeed);
        txtAvgSpeed = viewDrivingMode.findViewById(R.id.txtAverageSpeed);
        locationPermissions();
        viewModel = ViewModelDrivingMode.getInstance();
        txtCurrentSpeed.setText(viewModel.updateSpeed(null) + "");
        txtAvgSpeed.setText(viewModel.updateAverageSpeed(0) + "");

        return viewDrivingMode;
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
        Log.d(LOG_TAG, verticalOffest + "");
        MapViewContentBuilder builder = new MapViewContentBuilder(getActivity());
        builder.setVerticalOffset(verticalOffest).fetchLastLocation(this).build();
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
        GpsLocation myLocation = new GpsLocation(location);
        int currentSpeed = viewModel.updateSpeed(myLocation);
        txtCurrentSpeed.setText(currentSpeed + "");
        txtAvgSpeed.setText("Ø " + viewModel.updateAverageSpeed(currentSpeed) + " Km/h");
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
