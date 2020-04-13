package de.thu.tpro.android4bikes.view.info;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.GeoPoint;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;
import de.thu.tpro.android4bikes.util.GeoFencing;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.view.map.MapViewContentBuilder;


public class FragmentInfoMode extends Fragment {

    private static final String LOG_TAG = "FragmentInfoMode";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 102;

    ///Temporary variables just for testing///
    //Todo: Delete after testing
    private TextView tv_Test;
    TextView name, mail;
    Button logout;
    /////////////////////////////////////////

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_info_mode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //////TODO: REMOVE AFTER TESTING////////////

        /**
         * New GeoFencing instance
         */
        GlobalContext.setContext(this.getContext());
        GeoPoint center = new GeoPoint(48.403498, 9.978170);
        double radius_in_km = 14.9d;
        GeoFencing geoFencing_hazards = new GeoFencing(GeoFencing.ConstantsGeoFencing.COLLECTION_HAZARDS);
        //FirebaseConnection.getInstance().submitHazardAlertToFireStore(new HazardAlert(
        //HazardAlert.HazardType.GENERAL, new Position(9.997507, 48.408880), 120000, 5, "12345"
        //));


        BikeRack bikeRack_THU = new BikeRack(
                "pfo4eIrvzrI0m363KF0K", new Position(9.997507, 48.408880), "THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true, false);
        FirebaseConnection.getInstance().submitBikeRackToFireStore(bikeRack_THU);

        /**
         * Register positions in Firestore using GeoFirestore
         */

        /*
        PositionProvider.get50kmRadiusPositionstest().forEach(geoFencing_bikeRacks::registerDocument);

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/




        /**
         * Listen to Geofence
         */
        geoFencing_hazards.setupGeofence(center, radius_in_km);
        geoFencing_hazards.startGeoFenceListener();

        // check if location access is granted
        if (isAccessLocationPermissionGranted()) {
            // if Yes, continue with map initialization
            populateMap();
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
        Log.d(LOG_TAG, verticalOffest + "");
        MapViewContentBuilder builder = new MapViewContentBuilder(getActivity());
        builder.setVerticalOffset(verticalOffest).fetchLastLocation(this).build();
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

/*    ///Temporary method for logout testing///
    //Todo: Delete after testing
    private void testLogOut() {
        tv_Test = getActivity().findViewById(R.id.tv_Test);
        logout = getActivity().findViewById(R.id.logout);
        name = getActivity().findViewById(R.id.name);
        mail = getActivity().findViewById(R.id.mail);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (signInAccount != null) {
            name.setText(signInAccount.getDisplayName());
            mail.setText(signInAccount.getEmail());
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getActivity().getApplicationContext(), ActivityLogin.class);
//                startActivity(intent);
            }
        });

    }*/


}
