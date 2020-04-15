package de.thu.tpro.android4bikes.view.driving;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
    TextView txtSpeedUnit;
    //TODO Delete after testing
    private ViewModelDrivingMode viewModel;
    private View viewDrivingMode;
    private CardView infoIcon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewDrivingMode = inflater.inflate(R.layout.fragment_driving_mode, container, false);
        txtCurrentSpeed = viewDrivingMode.findViewById(R.id.txtCurrentSpeed);
        txtSpeedUnit = viewDrivingMode.findViewById(R.id.txtSpeedUnit);
        txtAvgSpeed = viewDrivingMode.findViewById(R.id.txtAverageSpeed);
        infoIcon = viewDrivingMode.findViewById(R.id.cardView_Infoicon);

        initCardView();

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
     * initiates the top cardView and sets the correct sizes for all elements
     */
    private void initCardView(){
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);

        int infoIconHeight = calculateSize(size.y,0.2);
        int currentSpeedHeight = calculateSize(size.y ,0.1);
        int currentSpeedWidth = calculateSize(size.x,0.2);
        int unitWidth = calculateSize(size.x,0.4);
        int avgSpeedHeight = calculateSize(size.y,0.05);
        int avgSpeedWidth =calculateSize(size.x,0.6);

        setViewDimension(infoIcon,infoIconHeight,infoIconHeight);
        setViewDimension(txtCurrentSpeed,currentSpeedWidth,currentSpeedHeight);
        setViewDimension(txtSpeedUnit,unitWidth,currentSpeedHeight);
        setViewDimension(txtAvgSpeed,avgSpeedWidth,avgSpeedHeight);
    }

    /**
     * sets the size for the current view
     * @param view
     * @param width
     * @param height
     */
   private void setViewDimension(View view, int width, int height){
       ViewGroup.LayoutParams params = view.getLayoutParams();
       params.height = height;
       params.width = width;
       view.setLayoutParams(params);
   }

    /**
     * calculates the size of a view element
     * @param base
     * @param factor
     * @return base*factor roundet to int
     */
    private int calculateSize(int base,double factor){
        return (int)Math.rint(base*factor);
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
