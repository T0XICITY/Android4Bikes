package de.thu.tpro.android4bikes.view.info;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.view.map.MapFragmentBuilder;

public class FragmentInfoMode extends Fragment {

    ///Temporary variables just for testing///
    //Todo: Delete after testing
    private TextView tv_Test;
    TextView name, mail;
    Button logout;
    /////////////////////////////////////////
    //Map Variables
    private static final int REQUEST_CODE = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 102;

    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: uncomment code.
/*        FirebaseConnection firebaseConnection = FirebaseConnection.getInstance();
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new KmAchievement("A", 1, 1, 1, 1));
        achievements.add(new KmAchievement("B", 2, 2, 2, 2));

        Profile profile = new Profile("Olaf", "Olafsen", "00x13dxxx", 10, 1, achievements);
        firebaseConnection.storeProfileToFireStoreAndLocalDB(profile);

        GlobalContext.setContext(getActivity().getApplicationContext());
        determineAllViews();

        firebaseConnection.readBikeRacksFromFireStoreAndStoreItToLocalDB("89075");

        //HazardAlert hazardAlert = new HazardAlert(HazardAlert.HazardType.ICY_ROAD);
        //tv_Test.setText(hazardAlert.getType());
        //testLogOut();
        a();
        b();*/
        return inflater.inflate(R.layout.fragment_info_mode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO: in Util Klasse auslagern
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        } else {
            initMap();
        }
    }


    /**
     * Request for the permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMap();
                }
                break;
        }
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
    }

    private void initMap() {
        MapFragmentBuilder builder = new MapFragmentBuilder(getActivity());
        builder.fetchLastLocation(this).build();
    }

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
