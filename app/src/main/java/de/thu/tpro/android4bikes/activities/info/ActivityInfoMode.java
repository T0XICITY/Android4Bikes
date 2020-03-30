package de.thu.tpro.android4bikes.activities.info;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.thu.tpro.android4bikes.R;

import de.thu.tpro.android4bikes.activities.login.ActivityLogin;
import de.thu.tpro.android4bikes.data.model.HazardAlert;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.achievements.KmAchievement;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;

import de.thu.tpro.android4bikes.util.GlobalContext;

public class ActivityInfoMode extends AppCompatActivity {

    ///Temporary variables just for testing///
    //Todo: Delete after testing
    private TextView tv_Test;
    TextView name, mail;
    Button logout;
    /////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<Achievement> achievements= new ArrayList<>();
        achievements.add(new KmAchievement("a",2,2,2,2));

        Profile profile = new Profile("Olaf","Olafsen","00x13dxxx",10,1,achievements);

        FirebaseConnection.addProfileToFirestore(profile);

        setContentView(R.layout.activity_info_mode);
        GlobalContext.setContext(getApplicationContext());
        determineAllViews();
        //HazardAlert hazardAlert = new HazardAlert(HazardAlert.HazardType.ICY_ROAD);
        //tv_Test.setText(hazardAlert.getType());
        testLogOut();
        a();
        b();
    }

    private void determineAllViews() {
    }

    private void a() {

    }

    private void b() {

    }

    ///Temporary method for logout testing///
    //Todo: Delete after testing
    private void testLogOut() {
        tv_Test = findViewById(R.id.tv_Test);
        logout = findViewById(R.id.logout);
        name = findViewById(R.id.name);
        mail = findViewById(R.id.mail);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            name.setText(signInAccount.getDisplayName());
            mail.setText(signInAccount.getEmail());
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intent);
            }
        });

    }
}
