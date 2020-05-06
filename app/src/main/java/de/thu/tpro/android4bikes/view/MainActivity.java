package de.thu.tpro.android4bikes.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Rating;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDB;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.database.CouchWriteBuffer;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;
import de.thu.tpro.android4bikes.services.PositionTracker;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.util.GpsUtils;
import de.thu.tpro.android4bikes.util.Navigation.TrackRecorder;
import de.thu.tpro.android4bikes.util.Processor;
import de.thu.tpro.android4bikes.util.ProfilePictureUtil;
import de.thu.tpro.android4bikes.util.WorkManagerHelper;
import de.thu.tpro.android4bikes.view.drivingmode.FragmentDrivingMode;
import de.thu.tpro.android4bikes.view.freemode.FragmentFreemode;
import de.thu.tpro.android4bikes.view.infomode.FragmentInfoMode;
import de.thu.tpro.android4bikes.view.login.ActivityLogin;
import de.thu.tpro.android4bikes.view.menu.roadsideAssistance.FragmentRoadsideAssistance;
import de.thu.tpro.android4bikes.view.menu.settings.FragmentSettings;
import de.thu.tpro.android4bikes.view.menu.showProfile.FragmentShowProfile;
import de.thu.tpro.android4bikes.view.menu.trackList.FragmentTrackList;
import de.thu.tpro.android4bikes.viewmodel.ViewModelBtBtn;
import de.thu.tpro.android4bikes.viewmodel.ViewModelInternetConnection;
import de.thu.tpro.android4bikes.viewmodel.ViewModelOwnProfile;
import de.thu.tpro.android4bikes.viewmodel.ViewModelOwnTracks;
import de.thu.tpro.android4bikes.viewmodel.ViewModelTrack;

//import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @author stlutz
 * This activity acts as a container for all fragments
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String LOG_TAG = "MainActivity";
    private static final String TAG = "CUSTOM_MARKER";

    private ViewModelOwnProfile vmOwnProfile;
    private ViewModelOwnTracks vmOwnTracks;
    private ViewModelTrack vm_track;

    public static final int GPS_REQUEST = 97;
    public com.mapbox.services.android.navigation.ui.v5.NavigationView navigationView;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private BottomAppBar bottomBar;
    public FloatingActionButton fab;
    private MaterialToolbar topAppBar;
    private ImageButton btn_tracks;
    private ImageButton btn_community;
    private DrawerLayout dLayout;
    private NavigationView drawer;
    private FragmentTransaction fragTransaction;
    private Fragment fragAssistance, fragProfile, fragSettings, currentFragment;
    private FragmentInfoMode fragInfo;
    private FragmentDrivingMode fragDriving;
    private FragmentTrackList fragTrackList;
    private TextView tv_headerName;
    private TextView tv_headerMail;
    private CircleImageView civ_profile;
    private boolean isGPS;
    public TrackRecorder trackRecorder;
    private ViewModelBtBtn vm_BtBtn;
    public LocationEngine locationEngine;
    public PositionTracker.LocationChangeListeningActivityLocationCallback callback;
    private boolean toolbarHidden;
    private FragmentFreemode fragFreemode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        GlobalContext.setContext(this.getApplicationContext());

        checkFirebaseAuth();

        // init View Models
        ViewModelProvider provider = new ViewModelProvider(this);
        vmOwnProfile = provider.get(ViewModelOwnProfile.class);
        vmOwnTracks = provider.get(ViewModelOwnTracks.class);
        vm_BtBtn = provider.get(ViewModelBtBtn.class);
        vm_track = provider.get(ViewModelTrack.class);

        setContentView(R.layout.activity_main);
        //debugWriteBuffer();

        initFragments();
        initNavigationDrawer();
        initNavigationDrawerHeader();
        initTopBar();
        initBottomNavigation();
        initFragments(); //TODO necessary?
        initFAB();


        trackRecorder = new TrackRecorder();
        currentFragment = fragInfo;
        updateFragment();

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        //testWorkManager();
        //observeInternet();
        //WorkManagerHelper.scheduleUploadTaskWithWorkManager();
        //scheduleUploadTaskWithTaskSchedule();

        //will be started after first attempt to read:
        if (savedInstanceState == null){
            WorkManagerHelper.stopUploadTaskWithWorkManager();
        }

        //init Location Engine
        this.callback = PositionTracker.LocationChangeListeningActivityLocationCallback.getInstance(this);
        vm_BtBtn.getBtnEvent().observe(this,newValue->{
            if (currentFragment == fragDriving){
                Toast.makeText(getApplicationContext(),"BtBtn was clicked",Toast.LENGTH_SHORT).show();
                //todo: clarify distance of interest
                HazardAlert alert = new HazardAlert(HazardAlert.HazardType.GENERAL,PositionTracker.getLastPosition(),10,true);
                CouchWriteBuffer.getInstance().submitHazardAlerts(alert);
            }
        });

        vm_track.getNavigationTrack().observe(this, newValue -> {
            if (newValue == null) {
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark)));
            } else {
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.Green800Primary)));
            }
        });
        refreshProfile();
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    public void initLocationEngine() {
        if (locationEngine == null) {
            locationEngine = LocationEngineProvider.getBestLocationEngine(this);

            LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                    .setFastestInterval(DEFAULT_MAX_WAIT_TIME)
                    .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                    .build();

            locationEngine.requestLocationUpdates(request, callback, getMainLooper());
            locationEngine.getLastLocation(callback);
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //onCreate(savedInstanceState);
    }

    /*private void testWorkManager() {
        WriteBuffer writeBuffer = CouchWriteBuffer.getInstance();
        for (int i = 0; i < 55; i++) {
            writeBuffer.addToUtilization(new Position(40.000 + i / 200.0, 9 + i / 200.0));
        }

        for (BikeRack bikeRack : TestObjectsGenerator.generateRandomBikeRackList()) {
            writeBuffer.submitBikeRack(bikeRack);
        }
        //writeBuffer.storeTrack(TestObjectsGenerator.generateTrack());
        //
        //writeBuffer.submitHazardAlerts(TestObjectsGenerator.generateHazardAlert());
    }*/

    public void observeInternet() {
        ViewModelInternetConnection model_internet = new ViewModelProvider(this).get(ViewModelInternetConnection.class);
        model_internet.getConnectedToWifi().observe(this, connectedToWifi -> {
            toastShortInMiddle("Wifi connection state: " + connectedToWifi);
            //Log.d("HalloWelt2", "Wifi connection state: " + connectedToWifi);
        });
        model_internet.getConnectedToMobile().observe(this, connectedToMobile -> {
            toastShortInMiddle("Mobile connection state: " + connectedToMobile);
            //Log.d("HalloWelt2", "Mobile connection state: " + connectedToMobile);
        });
        model_internet.startObserving();
    }

    /**
     * schedules UploadRunnable by using a TimerTask.
     */
    public void scheduleUploadTaskWithTaskSchedule() {
        Processor.getInstance().scheduleUploadTask();
    }

    private void toastShortInMiddle(String text) {
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

    private void initNavigationDrawerHeader() {
        View header = drawer.getHeaderView(0);

        civ_profile = header.findViewById(R.id.profile_image);
        tv_headerName = header.findViewById(R.id.tvName);
        tv_headerMail = header.findViewById(R.id.tvMail);

        civ_profile.setOnClickListener(v -> {
            openProfile();
            toggleNavigationDrawer();
        });

        // set observer to profile to update Drawer Profile section
        vmOwnProfile.getMyProfile().observe(this, profile -> {
            if (profile != null) {
                String fullName = String.format("%s %s", profile.getFirstName(), profile.getFamilyName());
                //Log.d(LOG_TAG, "Setting profile name: " + fullName);

                ProfilePictureUtil.setProfilePicturetoImageView(civ_profile, profile);
                civ_profile.setBorderColor(profile.getColor());



                tv_headerName.setText(fullName);
                tv_headerMail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            }
        });
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        checkFirebaseAuth();
    }

    //Choose selected Fragment
    @Override
    public boolean onNavigationItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.menu_submit:
                //Log.d(LOG_TAG, "Clicked menu_submit!");
                fragInfo.submitMarker();
                break;
            case R.id.menu_emergencyCall:
                //Log.d(LOG_TAG, "Clicked menu_emergencyCall!");
                openRoadsideAssistance();
                break;
            case R.id.menu_setting:
                //Log.d(LOG_TAG, "Clicked menu_setting!");
                openSettings();
                break;
            case R.id.menu_ownTracks:
                //Log.d(LOG_TAG, "Clicked menu_ownTracks");
                fragTrackList.setShowOwnTracksOnly(true);
                openTrackList();
                break;
            case R.id.menu_logout:
                //Log.d(LOG_TAG, "Clicked menu_logout!");
                logout();
                break;
            case R.id.menu_profile:
                //Log.d(LOG_TAG, "Clicked menu_profile!");
                openProfile();
                break;
            default:
                //Log.d(LOG_TAG, "Default case");
        }
        toggleNavigationDrawer();
        return true;
    }

    /**
     * First, check on FireStore whether the local stored profile is available on the FireStore. Otherwise,
     * it is only stored in the local WriteBuffer. If it is available on the FireStore, all databases
     * are cleared and the sign-out process is finished. Afterwards, the login activity is started.
     */
    public void goToLoginActivity() {
        Intent intent = new Intent(this, ActivityLogin.class);
        startActivity(intent);
    }

    private void logout() {
        WorkManagerHelper.stopUploadTaskWithWorkManager();
        FirebaseAuth.getInstance().signOut();
        CouchDB.getInstance().clearAllDatabases(); //clear all databases when logging out!
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

        btn_tracks.setOnClickListener(view -> {
            Log.d(LOG_TAG, "Clicked trackList!");
            dLayout.closeDrawers();
            fragTrackList.setShowOwnTracksOnly(false);
            openTrackList();
        });
        btn_community.setOnClickListener(view -> {
            Log.d(LOG_TAG, "clicked community");
            toggleNavigationDrawer();
        });
    }

    /**
     * Initiates Floating Action Button
     */
    private void initFAB() {
        fab = findViewById(R.id.fab_switchMode);
        fab.setOnClickListener(v -> {
            //if Track null start freemode, else start Navigation
            Track track_for_navigation = vm_track.getNavigationTrack().getValue();
            if (track_for_navigation != null) {
                switchInfoDriving();
            } else {
                switchInfoFreemode();
            }
        });
    }

    private void initNavigationDrawer() {
        dLayout = findViewById(R.id.drawerLayout);

        //find width of screen and divide by 2
        int width = getResources().getDisplayMetrics().widthPixels / 2;
        Log.d("FragmentInfoMode", dLayout.toString());
        dLayout.closeDrawer(GravityCompat.END);
        drawer = findViewById(R.id.navigationDrawer);

        //set the drawer width to the half of the screen
        ViewGroup.LayoutParams params = drawer.getLayoutParams();
        params.width = width;
        drawer.setLayoutParams(params);

        // "Lock" Drawer to not open on swipe gestures
        dLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // set Menu Item Listener
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
            fragDriving.cancelUpdateTimer(); // no more speed updates

            navigationView.stopNavigation();
            //navigationView.onDestroy(); //TODO?
            vm_track.setNavigationTrack(null);

            openInfoMode();


            // iterate over registered hazards while driving
            List<Position> hazardPositions = fragDriving.getRegisteredHazardPositions();
            if (hazardPositions.size() > 0) {
                for (Position hazPos : fragDriving.getRegisteredHazardPositions()) {
                    fragInfo.submit_hazard(hazPos);
                }

            }
        } else {
            openDrivingMode();
        }
    }

    private void switchInfoFreemode() {
        if (currentFragment.equals(fragFreemode)) {
            fragFreemode.cancelUpdateTimer(); // no more speed updates

            trackRecorder.stop();
            submitTrack();

            openInfoMode();


            // iterate over registered hazards while driving
            List<Position> hazardPositions = fragDriving.getRegisteredHazardPositions();
            if (hazardPositions != null && hazardPositions.size() > 0) {
                for (Position hazPos : fragDriving.getRegisteredHazardPositions())
                    fragInfo.submit_hazard(hazPos);
            }
        } else {

            //Start Recorder
            trackRecorder.start(this);
            openFreeMode();
        }
    }

    /**
     * Show Dialog to give feedback after finishing your ride
     */
    private void submitTrack() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_track_submit, null);
        TextInputLayout textLayout = dialogView.findViewById(R.id.txt_track_name_layout);
        TextInputEditText editTrackName = dialogView.findViewById(R.id.edit_track_name);
        TextInputEditText editDesc = dialogView.findViewById(R.id.edit_submit_desc);
        RatingBar rbSubmitRoadQuality = dialogView.findViewById(R.id.rb_submit_roadquality);
        RatingBar rbSubmitDifficulty = dialogView.findViewById(R.id.rb_submitk_difficulty);
        RatingBar rbSubmitFun = dialogView.findViewById(R.id.rb_submit_fun);


        AlertDialog submitTrackDialog = new MaterialAlertDialogBuilder(this)
                //.setTitle("Store your Track!")
                .setView(dialogView)
                .setPositiveButton(R.string.submit, null)
                .setNegativeButton(R.string.discard, null)
                .create();
        submitTrackDialog.setCanceledOnTouchOutside(false);
        submitTrackDialog.setOnShowListener(dialogInterface -> {
            submitTrackDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
            submitTrackDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        });
        submitTrackDialog.show();

        editTrackName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!editTrackName.getText().toString().trim().equals("")) {
                    textLayout.setError(null);
                }
            }
        });
        Button btnPos = submitTrackDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btnPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTrackName.getText().toString().trim().equals("")) {
                    //Show Error Hint in EditText to fill in name
                    textLayout.setError(getResources().getString(R.string.error_name));
                } else {
                    String author = vmOwnProfile.getMyProfile().getValue().getGoogleID();
                    String desc = editDesc.getText().toString();
                    String name = editTrackName.getText().toString();

                    Rating newRating = new Rating();
                    newRating.setRoadquality(rbSubmitRoadQuality.getProgress());
                    newRating.setDifficulty(rbSubmitDifficulty.getProgress());
                    newRating.setFun(rbSubmitFun.getProgress());

                    //Save track with Trackrecorder
                    trackRecorder.save(author, newRating, name, desc);
                    submitTrackDialog.dismiss();
                }

            }

        });
    }

    /**
     * Create Fragments
     */
    private void initFragments() {
        fragDriving = new FragmentDrivingMode();
        fragFreemode = new FragmentFreemode();
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
        Log.d("HalloWelt","onKeyDown");
        return vm_BtBtn.handleKeyEvent(event);
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

    private void openFreeMode() {
        currentFragment = fragFreemode;
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

        Log.d(LOG_TAG,"Own Tracks: "+fragTrackList.isOwnTracksOnly());
        if (fragTrackList.isOwnTracksOnly())
            topAppBar.setTitle(R.string.title_mytracks);
        else
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
            if (currentFragment.equals(fragInfo)) {
                //fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDark)));
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_material_bike));
            }else {
                fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDark)));
                fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_material_information));
            }// grow back to original X
            fab.animate().scaleX(1).start();
        }).start();
    }

    private void checkFirebaseAuth() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            goToLoginActivity();
        } else {
            CouchDBHelper cdb_ownDB = new CouchDBHelper(CouchDBHelper.DBMode.OWNDATA);
            if (cdb_ownDB.readMyOwnProfile() == null) {
                //read own Profile from FireStore:
                FirebaseConnection.getInstance().readOwnProfileFromFireStoreAndStoreItToOwnDB(FirebaseAuth.getInstance().getCurrentUser().getUid());

                if (cdb_ownDB.readMyOwnProfile() == null) {
                    Toast.makeText(getApplicationContext(), R.string.loginfailed, Toast.LENGTH_LONG);
                    goToLoginActivity();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

    private void debugWriteBuffer() {
        Processor.getInstance().startRunnable(() -> {
            CouchDBHelper cdb = new CouchDBHelper(CouchDBHelper.DBMode.WRITEBUFFER);
            while (true) {
                List<HazardAlert> haz = cdb.readHazardAlerts();
                List<BikeRack> br = cdb.readBikeRacks();
                List<Track> tr = cdb.readTracks();
                Log.d("HalloWelt", "Debug Buffer: Tracks (" + tr.size() + "):" + tr.toString());
                Log.d("HalloWelt", "Debug Buffer: BikeRacks (" + br.size() + "):" + br.toString());
                Log.d("HalloWelt", "Debug Buffer: Hazards (" + haz.size() + "):" + haz.toString());
                Log.d("HalloWelt", "Debug OWN Profile :" + cdb.readMyOwnProfile());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Prevent leaks
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        if (navigationView != null) {
            navigationView.onDestroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }

    /**
     * refresh data regarding a specified profile.
     */
    public void refreshProfile() {
        Processor.getInstance().startRunnable(() -> {
            try{
                //TODO: do change of name on the server
                Profile profile_own = new CouchDBHelper(CouchDBHelper.DBMode.OWNDATA).readMyOwnProfile();
                if (profile_own != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Uri photoUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
                    String[] name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().split(" ");
                    String firstName = name[0];
                    String familyName = "";
                    if (name.length > 1) {
                        familyName = name[1];
                    }
                    profile_own.setFirstName(firstName);
                    profile_own.setFamilyName(familyName);
                    profile_own.setProfilePictureURL(photoUrl.toString());
                }
                FirebaseConnection.getInstance().storeProfileToFireStoreAndLocalDB(profile_own);
                FirebaseConnection.getInstance().readAllOwnTracksAndStoreItToOwnDB(profile_own.getGoogleID());
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public void checkLocationEnabled() {
        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog gpsDialog = new MaterialAlertDialogBuilder(this)
                    .setTitle(getResources().getString(R.string.gps_title))
                    .setMessage(getResources().getString(R.string.user_location_permission_explanation)
                        +"\n"+ getResources().getString(R.string.gps_message))
                    .setPositiveButton("Yes", null)
                    .setNegativeButton("No", null)
                    .create();

            gpsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    gpsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
                    gpsDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));
                }
            });
            gpsDialog.show();
            gpsDialog.setCanceledOnTouchOutside(false);
            Button btnPos = gpsDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnPos.setOnClickListener(view -> {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                gpsDialog.dismiss();
            });

            Button btnNeg = gpsDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            btnNeg.setOnClickListener(view -> gpsDialog.dismiss());
        }
    }
}