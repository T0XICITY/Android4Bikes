package de.thu.tpro.android4bikes.view.info;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.view.map.MapViewContentBuilder;


public class FragmentInfoMode extends Fragment {

    private static final String LOG_TAG = "FragmentInfoMode";
    private View viewInfo;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 102;
    ViewGroup container;
    MapViewContentBuilder builder;
    int chosenMarkerId;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewInfo = inflater.inflate(R.layout.fragment_info_mode, container, false);
        return viewInfo;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GlobalContext.setContext(this.getContext());

        // check if location access is granted
        if (isAccessLocationPermissionGranted()) {
            // if Yes, continue with map initialization
            populateMap();
            addMarkers();

        } else {
            // if No, request user for permission and continue later in onRequestPermissionsResult
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Request for the permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    populateMap();
                }
                // add cases, when more than 1 permission is need
        }
    }

    /**
     * Init Map content with MapViewContentBuilder
     */
    private void populateMap() {
        Log.d(LOG_TAG, "Init Map called");
        //to adjust the Map Controls position TODO: define offset programmatically. Problem height = wrap_content returns 0
        int verticalOffest = 250;

        builder = new MapViewContentBuilder(getActivity());
        builder.setVerticalOffset(verticalOffest)
                .fetchLastLocation(this)
                .build();
    }

    private void addMarkers() {
        builder = new MapViewContentBuilder(getActivity());

    }

    /**
     * Checks, if permission is granted to access location
     *
     * @return <code>true</code>, if permission is granted<br/><code>false</code> otherwise
     */
    private boolean isAccessLocationPermissionGranted() {
        return ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void showTrackFeedback(Context context) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        dialogBuilder.setTitle("Store your Track!");
        dialogBuilder.setView(R.layout.dialog_track_feedback);
        dialogBuilder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Snackbar.make(viewInfo.findViewById(R.id.fragment_container), "Store into Firestore", 1000).setAnchorView(viewInfo.findViewById(R.id.map)).show();
            }
        });

        dialogBuilder.setNegativeButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Snackbar.make(viewInfo.findViewById(R.id.fragment_container), "Don´t store ", 1000).setAnchorView(viewInfo.findViewById(R.id.map)).show();
            }
        });
        dialogBuilder.show();
    }

    public void submitMarker() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getContext());
        dialogBuilder.setTitle(R.string.submit);
        String[] s = getResources().getStringArray(R.array.marker);
        dialogBuilder.setItems(R.array.marker, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        submitt_rack();
                        break;
                    case 1:
                        submit_hazard();
                        break;
                    default:
                        Snackbar.make(viewInfo.findViewById(R.id.map), "default", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
                }
            }
        });
        dialogBuilder.show();
    }

    private void submit_hazard() {
        MaterialAlertDialogBuilder dia_hazardBuilder = new MaterialAlertDialogBuilder(getContext());
        dia_hazardBuilder.setTitle(R.string.submit_hazard);
        dia_hazardBuilder.setItems(R.array.hazards, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        Snackbar.make(viewInfo.findViewById(R.id.map), "default", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
                        //Damaged Road
                        break;
                    case 1:
                        Snackbar.make(viewInfo.findViewById(R.id.map), "Icy road", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
                        //Icy road
                        break;
                    case 2:
                        Snackbar.make(viewInfo.findViewById(R.id.map), "slippery road", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
                        //slippery road
                        break;
                    case 3:
                        Snackbar.make(viewInfo.findViewById(R.id.map), "Roadkill", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
                        //Roadkill
                        break;
                    case 4:
                        Snackbar.make(viewInfo.findViewById(R.id.map), "Rockfall", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
                        //Rockfall
                        break;
                    case 5:
                        Snackbar.make(viewInfo.findViewById(R.id.map), "General", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
                        //General
                        break;
                    default:
                        Snackbar.make(viewInfo.findViewById(R.id.map), "default", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
                }
            }
        });

        dia_hazardBuilder.show();
    }

    private void submitt_rack() {
        MaterialAlertDialogBuilder rack_builder = new MaterialAlertDialogBuilder(getContext());
        rack_builder.setTitle("Submit rack");
        rack_builder.setView(R.layout.dialog_rack);
        rack_builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO store Rack
                Snackbar.make(viewInfo.findViewById(R.id.map), "Store into FireStore", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
            }
        });

        rack_builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO discard Rack
                Snackbar.make(viewInfo.findViewById(R.id.map), "Don't store", 1000).setAnchorView(viewInfo.findViewById(R.id.bottomAppBar)).show();
            }
        });
        rack_builder.show();
    }
}
