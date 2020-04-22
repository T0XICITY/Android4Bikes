package de.thu.tpro.android4bikes.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.view.driving.FragmentDrivingMode;
import de.thu.tpro.android4bikes.view.info.FragmentInfoMode;
import de.thu.tpro.android4bikes.view.login.ActivityLogin;
import de.thu.tpro.android4bikes.view.menu.roadsideAssistance.FragmentRoadsideAssistance;
import de.thu.tpro.android4bikes.view.menu.settings.FragmentSettings;
import de.thu.tpro.android4bikes.view.menu.showProfile.FragmentShowProfile;
import de.thu.tpro.android4bikes.view.menu.trackList.FragmentTrackList;

//import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @author stlutz
 * This activity acts as a container for all fragments
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String LOG_TAG = "MainActivity";
    private static final String TAG = "CUSTOM_MARKER";

    private BottomAppBar bottomBar;
    FloatingActionButton fab, fab1, fab2, fab3, fab4, fab5;
    private MaterialToolbar topAppBar;
    private ImageButton btn_tracks;
    private ImageButton btn_community;
    private DrawerLayout dLayout;
    private NavigationView drawer;
    private FragmentTransaction fragTransaction;
    private Fragment fragDriving, fragInfo, fragAssistance, fragTrackList, fragProfile,
            fragSettings, currentFragment;
    private ImageView imageView;

    private boolean toolbarHidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkFirebaseAuth();

        setContentView(R.layout.activity_main);
        GlobalContext.setContext(this.getApplicationContext());
        //dialog = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_Dialog);

        /*
        Database database = DatabaseConnection.getInstance();
        database.getLastPosition();
        database.readTracks("89610");
        */

        initFragments();
        initNavigationDrawer();
        initTopBar();
        initBottomNavigation();
        initFragments();
        initFAB();

        onCreateClickShowProfile();
        //start with InfoFragment
        currentFragment = fragInfo;
        updateFragment();

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
            case R.id.menu_hazard:
                Log.d(LOG_TAG, "Clicked menu_hazard!");
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
}