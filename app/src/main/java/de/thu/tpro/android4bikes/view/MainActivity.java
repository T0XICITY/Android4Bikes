package de.thu.tpro.android4bikes.view;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.view.menu.createTrack.FragmentCreateTrack;
import de.thu.tpro.android4bikes.view.menu.roadsideAssistance.Custom_Listview;
import de.thu.tpro.android4bikes.view.menu.roadsideAssistance.FragmentRoadsideAssistance;

/**
 * @author stlutz
 * This activity acts as a container for all fragments
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    /**
     * currentFragment is saving the fragment, that is currently shown on the screen
     */
    private Fragment currentFragment;
    private BottomAppBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


    private void initAssistanceRoad(){
        /**
         * showing Listview on RoadsideAssistent  / author: Elias
         */
        lv_road_assistance = (ListView) findViewById(R.id.lv_layout);
        Custom_Listview custom_listview = new Custom_Listview(this,tv_institutions,iv_institutions,ib_call);
        lv_road_assistance.setAdapter(custom_listview);
    }



    private void initNav() {
        bottomBar = findViewById(R.id.bottomAppBar);
        setSupportActionBar(bottomBar);
        currentFragment = new FragmentCreateTrack();
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
                currentFragment = new FragmentRoadsideAssistance();
                updateFragment();
                Log.d("Mitte", "Clicked mitte");
                //TODO Change Mode
                createSnackbar();
            }
        });
    }

    /**
     * Creates a Snackbar to test the floating action button
     */
    private void createSnackbar() {
        Snackbar.make(findViewById(R.id.fragment_container), R.string.title_switchMode, 1000).setAnchorView(bottomBar).show();
    }

    /**
     * Replaces the displayed fragment with the {@link #currentFragment}
     */
    private void updateFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
    }


    /**
     * @author Elias
     * Implement Listview with all necessary information -> Imageview, Textview, Imagebutton
     *
      */
    ListView lv_road_assistance;

    // Texview
    String[] tv_institutions = {"Ambulance service", "Fire department", "Police", "Roadride assistance"
            , "Youth Helpline", "Manual input"};
    //Imageview
    Integer[] iv_institutions ={R.drawable.ic_emergency__hospital,R.drawable.emergency_fire_department,
    R.drawable.emergency_police,R.drawable.emergency_roadside_assistance,R.drawable.ic_emergency_child,R.drawable.ic_emergency_phone};

    //Imagebutton
    Integer[] ib_call = {R.drawable.ic_emergency__call,R.drawable.ic_emergency__call,R.drawable.ic_emergency__call,
            R.drawable.ic_emergency__call,R.drawable.ic_emergency__call,R.drawable.ic_emergency__call};

}
