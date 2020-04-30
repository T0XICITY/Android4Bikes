package de.thu.tpro.android4bikes.view.login;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.view.MainActivity;

/**
 * Firebase Authentication:
 * https://firebase.google.com/docs/auth/android/google-signin
 */
public class ActivityLogin extends AppCompatActivity {

    private static final String TAG = "ActivityLogin";
    private final static int RC_SIGN_IN = 9999;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private PermissionsManager permissionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalContext.setContext(this.getApplicationContext());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initialize();
        createRequest();
        findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.progBar_login).setVisibility(View.VISIBLE);
                signIn();
            }
        });
    }

    /**
     * Checks if user is logged in.
     * If user is logged in -> the user is forwarded to ActivityInfoMode
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * initializes views, variables
     */
    private void initialize() {
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.progBar_login).setVisibility(View.GONE);
    }

    /**
     * Creates the request form for current user
     */
    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Performs Google sign
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Read the users contact data
     * Is called whenever user returns from Google Login UI
     *
     * @param requestCode Requested code
     * @param resultCode  Result cod from Google Api
     * @param data        User data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                if (!PermissionsManager.areLocationPermissionsGranted(this)) {
                    permissionsManager = new PermissionsManager(new PermissionsListener() {
                        @Override
                        public void onExplanationNeeded(List<String> permissionsToExplain) {
                            Toast.makeText(ActivityLogin.this, R.string.user_location_permission_explanation,
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onPermissionResult(boolean granted) {
                            if (!granted) {
                                Toast.makeText(ActivityLogin.this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    permissionsManager.requestLocationPermissions(this);
                }
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * After a user successfully signs in, get an ID token from the GoogleSignInAccount object,
     * exchange it for a Firebase credential, and authenticate with Firebase using the Firebase credential
     *
     * @param acct users GoogleSignInAccount
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ActivityLogin.this, R.string.Activity_Login_Toast_Fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // close the app
            this.finishAffinity();
            try{
                return true;//this line does the rest
            }
            catch(IllegalStateException e){
                e.printStackTrace();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event); //handles other keys
    }
}