package de.thu.tpro.android4bikes.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.achievements.KmAchievement;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Rating;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchWriteBuffer;
import de.thu.tpro.android4bikes.database.WriteBuffer;
import de.thu.tpro.android4bikes.services.UploadWorker;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.view.driving.FragmentDrivingMode;
import de.thu.tpro.android4bikes.view.info.FragmentInfoMode;
import de.thu.tpro.android4bikes.view.login.ActivityLogin;
import de.thu.tpro.android4bikes.view.menu.roadsideAssistance.FragmentRoadsideAssistance;
import de.thu.tpro.android4bikes.view.menu.settings.FragmentSettings;
import de.thu.tpro.android4bikes.view.menu.showProfile.FragmentShowProfile;
import de.thu.tpro.android4bikes.view.menu.trackList.FragmentTrackList;
import de.thu.tpro.android4bikes.viewmodel.ViewModelInternetConnection;
import de.thu.tpro.android4bikes.viewmodel.ViewModelOwnProfile;

//import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @author stlutz
 * This activity acts as a container for all fragments
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , View.OnClickListener{
    private ViewModelOwnProfile model_profile;
    private static final String LOG_TAG = "MainActivity";
    private static final String TAG = "CUSTOM_MARKER";

    private BottomAppBar bottomBar;
    FloatingActionButton fab, fab1, fab2, fab3, fab4, fab5; //TODO: Should this be like this ? not private? strange identifier names
    private MaterialToolbar topAppBar;
    private ImageButton btn_tracks;
    private ImageButton btn_community;
    private DrawerLayout dLayout;
    private NavigationView drawer;
    private FragmentTransaction fragTransaction;
    private Fragment fragDriving, fragInfo, fragAssistance, fragTrackList, fragProfile, fragSettings, currentFragment;
    private ImageView imageView;

    private boolean toolbarHidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkFirebaseAuth();

        setContentView(R.layout.activity_main);
        GlobalContext.setContext(this.getApplicationContext());
        //dialog = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Dialog);

        initFragments();
        initNavigationDrawer();
        initTopBar();
        initBottomNavigation();
        initFragments();
        initFAB();

        onCreateClickShowProfile();
        currentFragment = fragInfo;
        updateFragment();

        observeInternet();
        scheduleUploadTask();

        testWorkManager();
    }

    private void testWorkManager() {
        WriteBuffer writeBuffer = CouchWriteBuffer.getInstance();
        for (int i = 0; i < 51; i++) {
            writeBuffer.addToUtilization(new Position(40.000+i/200.0,9+i/200.0));
        }
        writeBuffer.storeTrack(generateTrack());
    }

    public void observeInternet() {
        ViewModelInternetConnection model_internet = new ViewModelProvider(this).get(ViewModelInternetConnection.class);
        model_internet.startObserving();
        model_internet.getConnectedToWifi().observe(this, connectedToWifi -> {
            toastShortInMiddle("Wifi connection state: " + connectedToWifi);
            Log.d("HalloWelt2", "Wifi connection state: " + connectedToWifi);
        });
        model_internet.getConnectedToMobile().observe(this, connectedToMobile -> {
            toastShortInMiddle("Mobile connection state: " + connectedToMobile);
            Log.d("HalloWelt2", "Mobile connection state: " + connectedToMobile);
        });

    }

    /**
     * Defines a task that uploads not synchronized data.
     */
    public void scheduleUploadTask() {

        Log.d("HalloWelt", "Started at: " + new Date());

        //constraints regarding when a task should be scheduled
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //Define the request: How often should the task be scheduled
        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(UploadWorker.class, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build();

        //schedule task
        WorkManager.getInstance(GlobalContext.getContext())
                .enqueue(saveRequest);
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

    public void onCreateClickShowProfile() {

        View header = drawer.getHeaderView(0);
        imageView = header.findViewById(R.id.imageView_profile);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
                closeContextMenu();
            }
        });
    }

    //Choose selected Fragment
    @Override
    public boolean onNavigationItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.menu_community:
                Log.d(LOG_TAG, "Clicked menu_community!");
                //currentFragment = new SecondFragment();
                break;
            case R.id.menu_emergencyCall:
                Log.d(LOG_TAG, "Clicked menu_emergencyCall!");
                openRoadsideAssistance();
                break;
            case R.id.menu_setting:
                Log.d(LOG_TAG, "Clicked menu_setting!");
                openSettings();
                break;
            case R.id.menu_logout:
                Log.d(LOG_TAG, "Clicked menu_logout!");
                goToLoginActivity();
                break;
            default:
                Log.d(LOG_TAG, "Default case");
        }
        toggleNavigationDrawer();
        return true;
    }

    private void goToLoginActivity() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, ActivityLogin.class);
        startActivity(intent);
    }


    /**
     * Initiates the BottomAppBar and set listeners to nav buttons
     */
    private void initBottomNavigation() {
        bottomBar = findViewById(R.id.bottomAppBar);
        btn_community = findViewById(R.id.imagebutton_community);
        btn_tracks = findViewById(R.id.imagebutton_tracks);

        btn_tracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Clicked menu_tracks!");
                openTrackList();
            }
        });
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
/*
    private void showTracksDialog() {
        dialog.setView(R.layout.fragment_track_list);
        dialog.create().show();
    }
*/

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
        checkFirebaseAuth();
        fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.fragment_container, currentFragment);
        fragTransaction.commit();
    }

    /**
     * Switch between Info and driving mode
     */
    private void switchInfoDriving() {
        if (currentFragment.equals(fragDriving)) {
            openInfoMode();
        } else {
            openDrivingMode();
        }
    }

    /**
     * Create Fragments
     */
    private void initFragments() {
        fragDriving = new FragmentDrivingMode();
        fragInfo = new FragmentInfoMode();
        fragAssistance = new FragmentRoadsideAssistance();
        fragProfile = new FragmentShowProfile();
        fragTrackList = new FragmentTrackList();
        fragSettings = new FragmentSettings();
    }

    private void initTopBar() {
        toolbarHidden = false;
        topAppBar = findViewById(R.id.topAppBar);
        // Clicking Navigation Button ("Back Arrow") sends you back to InfoMode
        topAppBar.setNavigationOnClickListener(view -> openInfoMode());
        hideToolbar();
    }

    //https://stackoverflow.com/questions/2592037/is-there-a-default-back-keyon-device-listener-in-android#2592161@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // return to InfoMode
            openInfoMode();
            return true;
        }
        return super.onKeyDown(keyCode, event); //handles other keys
    }

    private void openInfoMode() {
        currentFragment = fragInfo;
        hideSoftKeyboard();
        hideToolbar();
        animateFabIconChange();

        updateFragment();
        showBottomBar();
        dLayout.closeDrawers();
    }

    private void openDrivingMode() {
        currentFragment = fragDriving;
        hideSoftKeyboard();
        hideToolbar();
        animateFabIconChange();

        updateFragment();
        //just the bottom bar should be hidden, not the FAB
        bottomBar.performHide();
        dLayout.closeDrawers();
    }

    private void openRoadsideAssistance() {
        currentFragment = fragAssistance;
        hideBottomBar();
        showToolbar();
        topAppBar.setTitle(R.string.title_telnumbers);
        updateFragment();
    }

    private void openTrackList() {
        currentFragment = fragTrackList;
        hideBottomBar();
        showToolbar();
        topAppBar.setTitle(R.string.title_tracks);
        updateFragment();
    }

    private void openProfile() {
        currentFragment = fragProfile;
        hideBottomBar();
        showToolbar();
        topAppBar.setTitle(R.string.title_profile);
        updateFragment();
    }

    private void openSettings() {
        currentFragment = fragSettings;
        hideBottomBar();
        showToolbar();
        topAppBar.setTitle(R.string.settings);
        updateFragment();
    }

    /*
     * https://stackoverflow.com/questions/26539623/android-lollipop-toolbar-how-to-hide-show-the-toolbar-while-scrolling
     */
    private void hideToolbar() {
        // only perform animation when currently shown
        if (toolbarHidden)
            return;

        toolbarHidden = true;
        topAppBar.animate().translationY(-topAppBar.getBottom())
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> topAppBar.setVisibility(View.GONE)).start();
    }

    private void showToolbar() {
        // only perform animation when Toolbar is shown
        if (!toolbarHidden)
            return;
        toolbarHidden = false;
        topAppBar.setVisibility(View.VISIBLE);
        topAppBar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
    }

    private void hideBottomBar() {
        fab.hide();
        bottomBar.performHide();
    }

    private void showBottomBar() {
        fab.show();
        bottomBar.performShow();
    }

    // https://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void animateFabIconChange() {
        // shrink X to middle
        fab.animate().scaleX(0).withEndAction(() -> {
            // change icon
            if (currentFragment.equals(fragInfo))
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_material_bike));
            else
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_material_information));
            // grow back to original X
            fab.animate().scaleX(1).start();
        }).start();
    }

    private void checkFirebaseAuth() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            goToLoginActivity();
        }
    }

    @Override
    public void onClick(View view) {

    }
    //################################ T E S T I N G ###############################################
    /**
     * generates a new instance of the class BikeRack for test purposes
     *
     * @return instance of a bike rack
     */
    private BikeRack generateTHUBikeRack() {
        //create new BikeRack
        BikeRack bikeRack_THU = new BikeRack(
                "pfo4eIrvzrI0m363KF0K", new Position(48.408880, 9.997507), "THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true, false
        );
        return bikeRack_THU;
    }

    /**
     * generates a new instance of the class HazardAlert for test purposes
     *
     * @return instance of a hazard alert
     */
    private HazardAlert generateHazardAlert() {
        HazardAlert hazardAlert_thu = new HazardAlert(
                HazardAlert.HazardType.GENERAL, new Position(48.408880, 9.997507), 120000, 5, "12345", true
        );
        return hazardAlert_thu;
    }

    /**
     * generates a new instance of the class Track for test purposes
     * @return instance of a track
     * */
    private Track generateTrack(){
        List<Position> positions = new ArrayList<>();
        positions.add(new Position(48.408880, 9.997507));
        Track track = new Track("nullacht15",new Rating(),"Heimweg","Das ist meine super tolle Strecke",
                "siebenundvierzig11",1585773516,25,
                positions,new ArrayList<>(),true);
        return track;
    }

    private Track generateDifferentTrack(String name){
        Track track = generateTrack();
        track.setDistance_km(100);
        track.setName(name);
        track.setDescription("Das ist schön. Das ist wunderschön!");
        return track;
    }

    /**
     * generates a new instance of the class {@link de.thu.tpro.android4bikes.data.model.Profile} for test purposes
     * */
    private Profile createProfile() {
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new KmAchievement("First Mile", 1, 1, 1, 2));
        achievements.add(new KmAchievement("From Olympia to Corinth", 2, 40, 7, 119));

        return new Profile("Kostas", "Kostidis", "00x15dxxx", 10, 250, achievements);
    }
}