package de.thu.tpro.android4bikes.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.util.FirebaseHelper;

import static android.content.ContentValues.TAG;

public class FirebaseConnection {
    public static void addProfileToFirestore(Profile profile) {
        FirebaseFirestore.getInstance().collection("users").document(profile.getFirebaseAccountID())
                .set(FirebaseHelper.convertProfileToMap(profile))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Profile " + profile.getFamilyName() + " added successfully");
                        //TODO Profil in Datenbank speichern.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding Profile" + profile.getFamilyName(), e);
                    }
                });
    }

    public void updateToken() {

    }

}
