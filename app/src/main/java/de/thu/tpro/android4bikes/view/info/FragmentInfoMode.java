package de.thu.tpro.android4bikes.view.info;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.thu.tpro.android4bikes.R;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.achievements.KmAchievement;
import de.thu.tpro.android4bikes.view.login.ActivityLogin;

import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;

import de.thu.tpro.android4bikes.util.GlobalContext;

public class FragmentInfoMode extends Fragment {

    ///Temporary variables just for testing///
    //Todo: Delete after testing
    private TextView tv_Test;
    TextView name, mail;
    Button logout;
    /////////////////////////////////////////


    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FirebaseConnection firebaseConnection = FirebaseConnection.getInstance();
        super.onCreate(savedInstanceState);
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
        b();
        return inflater.inflate(R.layout.fragment_info_mode,container,false);
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
