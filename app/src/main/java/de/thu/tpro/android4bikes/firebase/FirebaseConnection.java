package de.thu.tpro.android4bikes.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.FineGrainedPositions;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.Android4BikeServerDatabase;
import de.thu.tpro.android4bikes.util.FirebaseHelper;

import static android.content.ContentValues.TAG;

public class FirebaseConnection implements Android4BikeServerDatabase {
    public static void addProfileToFirestore(Profile profile) {
        FirebaseFirestore.getInstance().collection("users")
                .document(profile.getGoogleID())
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

    @Override
    public void storeProfileToFireStoreAndLocalDB(Profile Profile) {

    }

    @Override
    public void readProfileFromFireStoreAndStoreItToLocalDB(String googleID) {

    }

    @Override
    public void updateProfileInFireStoreAndLocalDB(Profile profile) {

    }

    @Override
    public void deleteProfileFromFireStoreAndLocalDB(String googleID) {

    }

    @Override
    public void storeBikeRackToFireStoreAndLocalDB(BikeRack bikeRack) {

    }

    @Override
    public void readBikeRacksFromFireStoreAndStoreItToLocalDB(String postcode) {

    }

    @Override
    public void storeTrackToFireStoreAndLocalDB(Track track, FineGrainedPositions fineGrainedPositions) {

    }

    @Override
    public void readCoarseGrainedTracksFromFireStoreAndStoreThemToLocalDB(String fireBaseID) {

    }

    @Override
    public void readFineGrainedTracksFromFireStoreAndStoreThemToLocalDB(String fireBaseID) {

    }

    @Override
    public void deleteTrackFromFireStoreAndLocalDB(String fireBaseID) {

    }

    @Override
    public void storeHazardAlertToFireStoreAndLocalDB(HazardAlert hazardAlert) {

    }

    @Override
    public void readHazardAlertsFromFireStoreAndStoreItToLocalDB(String postcode) {

    }

    @Override
    public void storeUtilizationToFireStore(List<Position> utilization) {

    }
}
