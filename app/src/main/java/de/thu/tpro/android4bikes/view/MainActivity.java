package de.thu.tpro.android4bikes.view;

import android.annotation.SuppressLint;
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
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

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
import de.thu.tpro.android4bikes.database.WriteBuffer;
import de.thu.tpro.android4bikes.firebase.FirebaseConnection;
import de.thu.tpro.android4bikes.services.PositionTracker;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.util.Processor;
import de.thu.tpro.android4bikes.util.TestObjectsGenerator;
import de.thu.tpro.android4bikes.util.WorkManagerHelper;
import de.thu.tpro.android4bikes.view.driving.FragmentDrivingMode;
import de.thu.tpro.android4bikes.view.info.FragmentInfoMode;
import de.thu.tpro.android4bikes.view.login.ActivityLogin;
import de.thu.tpro.android4bikes.view.menu.roadsideAssistance.FragmentRoadsideAssistance;
import de.thu.tpro.android4bikes.view.menu.settings.FragmentSettings;
import de.thu.tpro.android4bikes.view.menu.showProfile.FragmentShowProfile;
import de.thu.tpro.android4bikes.view.menu.trackList.FragmentTrackList;
import de.thu.tpro.android4bikes.viewmodel.ViewModelInternetConnection;
import de.thu.tpro.android4bikes.viewmodel.ViewModelOwnProfile;
import de.thu.tpro.android4bikes.viewmodel.ViewModelOwnTracks;

//import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @author stlutz
 * This activity acts as a container for all fragments
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, Observer<Profile> {
    private static final String LOG_TAG = "MainActivity";
    private static final String TAG = "CUSTOM_MARKER";

    private ViewModelOwnProfile vmOwnProfile;
    private ViewModelOwnTracks vmOwnTracks;

    public LatLng lastPos;
    public com.mapbox.services.android.navigation.ui.v5.NavigationView navigationView;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private BottomAppBar bottomBar;
    private FloatingActionButton fab;
    private MaterialToolbar topAppBar;
    private ImageButton btn_tracks;
    private ImageButton btn_community;
    private DrawerLayout dLayout;
    private NavigationView drawer;
    private FragmentTransaction fragTransaction;
    private Fragment fragAssistance, fragTrackList, fragProfile, fragSettings, currentFragment;
    private FragmentInfoMode fragInfo;
    private FragmentDrivingMode fragDriving;
    private ImageView imageView;
    private TextView tv_headerName;
    private TextView tv_headerMail;

    public LocationEngine locationEngine;
    public PositionTracker.LocationChangeListeningActivityLocationCallback callback;

    private boolean toolbarHidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalContext.setContext(this.getApplicationContext());

        checkFirebaseAuth();

        // init View Models
        ViewModelProvider provider = new ViewModelProvider(this);
        vmOwnProfile = provider.get(ViewModelOwnProfile.class);
        vmOwnTracks = provider.get(ViewModelOwnTracks.class);

        setContentView(R.layout.activity_main);

        debugWriteBuffer();

        initFragments();
        initNavigationDrawer();
        initTopBar();
        initBottomNavigation();
        initFragments();
        initFAB();

        onCreateClickShowProfile();
        currentFragment = fragInfo;
        updateFragment();

        testWorkManager();
        //observeInternet();

        WorkManagerHelper.scheduleUploadTaskWithWorkManager();
        //scheduleUploadTaskWithTaskSchedule();

        //init Location Engine
        this.callback = new PositionTracker.LocationChangeListeningActivityLocationCallback(this);

    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    public void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(DEFAULT_MAX_WAIT_TIME)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onCreate(savedInstanceState);
    }

    private void testWorkManager() {
        WriteBuffer writeBuffer = CouchWriteBuffer.getInstance();
        for (int i = 0; i < 55; i++) {
            writeBuffer.addToUtilization(new Position(40.000 + i / 200.0, 9 + i / 200.0));
        }
        writeBuffer.storeTrack(TestObjectsGenerator.generateTrack());
        writeBuffer.submitBikeRack(TestObjectsGenerator.generateTHUBikeRack());
        writeBuffer.submitHazardAlerts(TestObjectsGenerator.generateHazardAlert());
    }

    public void observeInternet() {
        ViewModelInternetConnection model_internet = new ViewModelProvider(this).get(ViewModelInternetConnection.class);
        model_internet.getConnectedToWifi().observe(this, connectedToWifi -> {
            toastShortInMiddle("Wifi connection state: " + connectedToWifi);
            Log.d("HalloWelt2", "Wifi connection state: " + connectedToWifi);
        });
        model_internet.getConnectedToMobile().observe(this, connectedToMobile -> {
            toastShortInMiddle("Mobile connection state: " + connectedToMobile);
            Log.d("HalloWelt2", "Mobile connection state: " + connectedToMobile);
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

    public void onCreateClickShowProfile() {

        View header = drawer.getHeaderView(0);
        imageView = header.findViewById(R.id.imageView_profile);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
                toggleNavigationDrawer();
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
                Log.d(LOG_TAG, "Clicked menu_submit!");
                //currentFragment = new SecondFragment();
                fragInfo.submitMarker();
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

    @Override
    public void onChanged(Profile profile) {
        Log.d("PROFILE Main", "" + profile);
        if (profile != null) {
            String fullName = String.format("%s %s", profile.getFirstName(), profile.getFamilyName());
            tv_headerName.setText(fullName);
            // TODO: Load email address from profile -> reading from FirebaseAuth doesn't seem right
            tv_headerMail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
    }

    /**
     * First, check on FireStore whether the local stored profile is available on the FireStore. Otherwise,
     * it is only stored in the local WriteBuffer. If it is available on the FireStore, all databases
     * are cleared and the sign-out process is finished. Afterwards, the login activity is started.
     */
    private void goToLoginActivity() {
        FirebaseAuth.getInstance().signOut();
        //todo: Delete user from local db
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
        tv_headerName = findViewById(R.id.tvName);
        tv_headerMail = findViewById(R.id.tvMail);
        //find width of screen and divide by 2
        int width = getResources().getDisplayMetrics().widthPixels / 2;
        Log.d("FragmentInfoMode", dLayout.toString());
        dLayout.closeDrawer(GravityCompat.END);
        drawer = findViewById(R.id.navigationDrawer);
        //set the drawer width to the half of the screen
        ViewGroup.LayoutParams params = drawer.getLayoutParams();
        params.width = width;
        drawer.setLayoutParams(params);
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
            openInfoMode();
            submitTrack();
        } else {
            openDrivingMode();
        }
    }

    /**
     * Show Dialog to give feedback after finishing your ride
     */
    //TODO: delete after Testing
        private void submitTrack() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_track_submit, null);

        TextView tvTrackName = dialogView.findViewById(R.id.tv_track_name);
        TextView tvDescription = dialogView.findViewById(R.id.tv_submit_desc);
        RatingBar rbSubmitRoadQuality = dialogView.findViewById(R.id.rb_submit_roadquality);
        RatingBar rbSubmitDifficulty = dialogView.findViewById(R.id.rb_submitk_difficulty);
        RatingBar rbSubmitFun = dialogView.findViewById(R.id.rb_submit_fun);

        dialogBuilder.setTitle("Store your Track!");
        dialogBuilder.setView(dialogView);

        dialogBuilder.setPositiveButton("Submit", (dialogInterface, i) -> {
            Track newTrack = new Track();
            newTrack.setName(tvTrackName.getText().toString());
            newTrack.setDescription(tvDescription.getText().toString());

            Rating newRating = new Rating();
            newRating.setRoadquality(rbSubmitRoadQuality.getProgress());
            newRating.setDifficulty(rbSubmitDifficulty.getProgress());
            newRating.setFun(rbSubmitFun.getProgress());
            newTrack.setRating(newRating);

            // TODO get fine grained positions

            newTrack.setAuthor_googleID(vmOwnProfile.getMyProfile().getValue().getGoogleID());

            vmOwnTracks.submitTrack(newTrack);
        });

        dialogBuilder.setNegativeButton("Discard", null); // do nothing on Cancel

        dialogBuilder.show();
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

    private void debugWriteBuffer() {
        Processor.getInstance().startRunnable(() -> {
            CouchDBHelper cdb = new CouchDBHelper(CouchDBHelper.DBMode.WRITEBUFFER);
            while (true) {
                List<HazardAlert> haz = cdb.readHazardAlerts();
                List<BikeRack> br = cdb.readBikeRacks();
                List<Track> tr = cdb.readTracks();
                Log.d("HalloWelt","Debug Buffer: Tracks ("+tr.size()+"):"+tr.toString());
                Log.d("HalloWelt","Debug Buffer: BikeRacks ("+br.size()+"):"+br.toString());
                Log.d("HalloWelt","Debug Buffer: Hazards ("+haz.size()+"):"+haz.toString());
                Log.d("HalloWelt","Debug OWN Profile :"+cdb.readMyOwnProfile());
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
    }
}