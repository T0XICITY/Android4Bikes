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
import de.thu.tpro.android4bikes.data.model.GeoPosition;
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
        GeoPosition geoPosition = new GeoPosition();
        String id = "test667";

        // HS ULM 48.408713, 9.997848 -> center



        for (int i = 0; i < 5; i++) {
            GeoPoint geoPoint = new GeoPoint(50.112945 + (i / 100.0), 8.681731 + (i / 100.0));
            // geoPosition.setLocation(id +i, geoPoint);
        }
        //geoPosition.setLocation(id, new GeoPoint(50.112945, 8.681731));

        /*
        // Mensa
        geoPosition.setLocation("mensa",new GeoPoint(48.409189, 9.998831));
        // Fbau
        geoPosition.setLocation("fbau",new GeoPoint(48.408713, 9.997848));
        // 48.400032, 9.982409 Bahnhof
        geoPosition.setLocation("HBF",new GeoPoint(48.400032, 9.982409));
        // 48.427255, 9.958914 Sporthalle Eselsberg
        geoPosition.setLocation("HalleEselsberg",new GeoPoint(48.427255, 9.958914));
        // 48.415427, 9.905595 Blaustein
        geoPosition.setLocation("Blaustein",new GeoPoint(48.415427, 9.905595));
        // 48.392422, 9.935436 edge innnerhalb
        geoPosition.setLocation("EdgeInnerhalb",new GeoPoint(48.392422, 9.935436));
        //48.390289, 9.933048 ausserhalb 5,2km
        geoPosition.setLocation("EdgeAußerhalb",new GeoPoint(48.390289, 9.933048));
*/
        // 48.392422, 9.935436 edge 6.6 außer
        geoPosition.setLocation("EdgeAuß", new GeoPoint(48.395916, 9.909769));
        geoPosition.geoQuery(new GeoPoint(48.408713, 9.997848), 3);


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


    // TODO: clean up methods below

    private void determineAllViews() {
    }

    private void a() {

    }

    private void b() {

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
