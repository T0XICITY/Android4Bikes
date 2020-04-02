package de.thu.tpro.android4bikes.view;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.view.driving.FragmentDrivingMode;
import de.thu.tpro.android4bikes.view.info.FragmentInfoMode;

/**
 * @author stlutz
 * This activity acts as a container for all fragments
 */
public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";
    /**
     * currentFragment is saving the fragment, that is currently shown on the screen
     */
    private BottomAppBar bottomBar;
    private FragmentTransaction fragTransaction;
    private Fragment fragDriving, fragInfo, currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragments();
        initNav();
        initFAB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottomappbar_navigation, menu);
        return true;
    }

    //Choose selected Fragment
    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
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
                //currentFragment = new ThirdFragment();
                break;
            case R.id.menu_hazard:
                Log.d(LOG_TAG, "Clicked menu_hazard!");
                break;
            default:
                Log.d(LOG_TAG, "Default case");
        }
        updateFragment();
        return true;
    }

    /**
     * Initiates the BottomAppBar
     */
    private void initNav() {
        bottomBar = findViewById(R.id.bottomAppBar);
        setSupportActionBar(bottomBar);
        currentFragment = new FragmentInfoMode();
        updateFragment();
    }

    /**
     * Initiates Floating Action Button
     */
    private void initFAB() {
        FloatingActionButton fab = findViewById(R.id.fab_switchMode);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Mitte", "Clicked mitte");
                //TODO Change Mode
                //createSnackbar();
                switchInfoDriving();

            }
        });
    }

    /**
     * Creates a Snackbar to test the floating action button
     */
    private void createSnackbar() {
        int i = currentFragment.getId();
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
        } else {
            currentFragment = fragDriving;
        }
        fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.fragment_container, currentFragment);
        fragTransaction.commit();
    }

    /**
     * Create Fragments
     */
    private void initFragments() {
        fragDriving = new FragmentDrivingMode();
        fragInfo = new FragmentInfoMode();
    }


}
