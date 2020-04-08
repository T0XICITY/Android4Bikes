package de.thu.tpro.android4bikes.view;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Iterator;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.warning.DWDwarning;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.view.driving.FragmentDrivingMode;
import de.thu.tpro.android4bikes.view.info.FragmentInfoMode;
import de.thu.tpro.android4bikes.view.menu.roadsideAssistance.FragmentRoadsideAssistance;
import de.thu.tpro.android4bikes.viewmodel.ViewModelProfile;
import de.thu.tpro.android4bikes.viewmodel.ViewModelWeather;
import de.thu.tpro.android4bikes.viewmodel.ViewModelWeatherWarning;

/**
 * @author stlutz
 * This activity acts as a container for all fragments
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ViewModelProfile model_profile;
    private static final String LOG_TAG = "MainActivity";
    //The App will start with this Fragment shown first
    private final Fragment STARTFRAGMENT = new FragmentInfoMode();
    /**
     * currentFragment is saving the fragment, that is currently shown on the screen
     */
    private BottomAppBar bottomBar;
    FloatingActionButton fab;
    private ImageButton btn_tracks;
    private ImageButton btn_community;
    private DrawerLayout dLayout;
    private NavigationView drawer;
    private FragmentTransaction fragTransaction;
    private Fragment fragDriving, fragInfo, currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalContext.setContext(this.getApplicationContext());

        /*
        Database database = DatabaseConnection.getInstance();

        database.getLastPosition();
        database.readTracks("89610");
        */

        initFragments();
        initNavigationDrawer();
        initBottomNavigation();
        initFAB();

        currentFragment = fragInfo;
        updateFragment();

        model_profile = new ViewModelProvider(this).get(ViewModelProfile.class);
        model_profile.getMyProfile().observe(this, myCurrProfile -> {
            // Update the UI
            toastShortInMiddle(myCurrProfile.toString());
        });

        ViewModelWeather model_weather = new ViewModelProvider(this).get(ViewModelWeather.class);
        model_weather.getCurrentWeather().observe(this, newWeather ->{
            if(newWeather != null){
                toastShortInMiddle(newWeather.toString());
            }
        });

        ViewModelWeatherWarning model_warning = new ViewModelProvider(this).get(ViewModelWeatherWarning.class);
        model_warning.getWeatherWarnings().observe(this,newWarnings->{
            if (newWarnings != null){
                Iterator<DWDwarning> iter = newWarnings.iterator();
                if (iter.hasNext()){
                    toastShortInMiddle(iter.next().toString());
                }
            }
        });

    }

    private void toastShortInMiddle(String text){
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.getView().setBackgroundColor(Color.parseColor("#90ee90"));
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    //Choose selected Fragment
    @Override
    public boolean onNavigationItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.menu_assistance:
                Log.d(LOG_TAG, "Clicked menu_assistance!");
                //currentFragment = new FirstFragment();

                break;
            case R.id.menu_community:
                Log.d(LOG_TAG, "Clicked menu_community!");
                //currentFragment = new SecondFragment();
                break;
            case R.id.menu_emergencyCall:
                Log.d(LOG_TAG, "Clicked menu_emergencyCall!");
                currentFragment = new FragmentRoadsideAssistance();
                break;
            case R.id.menu_hazard:
                Log.d(LOG_TAG, "Clicked menu_hazard!");
                break;
            default:
                Log.d(LOG_TAG, "Default case");
        }
        toggleNavigationDrawer();
        updateFragment();
        return true;
    }

    /**
     * Initiates the BottomAppBar and set listeners to nav buttons
     */
    private void initBottomNavigation() {
        bottomBar = findViewById(R.id.bottomAppBar);
        btn_community = findViewById(R.id.imagebutton_community);
        btn_tracks = findViewById(R.id.imagebutton_tracks);

        btn_community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "clicked community");
                toggleNavigationDrawer();
            }
        });
    }

    /**
     * Initiates Floating Action Button
     */
    private void initFAB() {
        fab = findViewById(R.id.fab_switchMode);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchInfoDriving();
                updateFragment();
                Log.d("Mitte", "Clicked mitte");
            }
        });
    }

    private void initNavigationDrawer() {
        dLayout = findViewById(R.id.drawerLayout);
        Log.d("FragmentInfoMode", dLayout.toString());
        dLayout.closeDrawer(GravityCompat.END);
        drawer = findViewById(R.id.navigationDrawer);
        drawer.setNavigationItemSelectedListener(this);
    }

    public void toggleNavigationDrawer() {
        if (dLayout.isDrawerOpen(GravityCompat.END)) {
            dLayout.closeDrawer(GravityCompat.END);
        } else {
            dLayout.openDrawer(GravityCompat.END);
        }

    }

    /**
     * Creates a Snackbar to test the floating action button
     */
    private void createSnackbar() {
        Snackbar.make(findViewById(R.id.fragment_container), currentFragment.getId(), 1000).setAnchorView(bottomBar).show();
    }

    /**
     * Replaces the displayed fragment with the {@link #currentFragment}
     */
    private void updateFragment() {
        fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.fragment_container, currentFragment);
        fragTransaction.commit();
    }

    /**
     * Switch between Info and driving mode
     */
    private void switchInfoDriving() {
        if (currentFragment.equals(fragDriving)) {
            currentFragment = fragInfo;
            bottomBar.performShow();
        } else {
            dLayout.closeDrawers();
            currentFragment = fragDriving;
            bottomBar.performHide();
        }
    }

    /**
     * Create Fragments
     */
    private void initFragments() {
        fragDriving = new FragmentDrivingMode();
        fragInfo = new FragmentInfoMode();
    }
}