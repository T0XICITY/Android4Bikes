package de.thu.tpro.android4bikes.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.FineGrainedPositions;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.database.FireStoreDatabase;
import de.thu.tpro.android4bikes.database.LocalDatabaseHelper;
import de.thu.tpro.android4bikes.util.ObserverMechanism.FireStoreObserver;

public class FirebaseConnection implements FireStoreDatabase {
    private static FirebaseConnection firebaseConnection;
    private FirebaseFirestore db;
    private List<FireStoreObserver> fireStoreObservers;
    private LocalDatabaseHelper localDatabaseHelper;

    private FirebaseConnection() {
        this.db = FirebaseFirestore.getInstance();
        fireStoreObservers = new ArrayList<>();
        localDatabaseHelper = new CouchDBHelper();
    }

    public static FirebaseConnection getInstance() {
        if (firebaseConnection == null) {
            firebaseConnection = new FirebaseConnection();
        }
        return firebaseConnection;
    }

    /**
     * should be used to subscribe as an observer
     *
     * @param fireStoreObserver observer regarding fireStore
     */
    public void subscribeObserver(FireStoreObserver fireStoreObserver) {
        this.fireStoreObservers.add(fireStoreObserver);
    }

    public void addProfileToFirestore(Profile profile) {
        db.collection(ConstantsFirebase.COLLECTION_USERS.toString())
                .document(profile.getGoogleID()) //set the id of a given document
                .set(profile.toMap()) //set-Method: Will create or overwrite document if it is existing
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Hallo Welt", "Profile " + profile.getFamilyName() + " added successfully");
                        //TODO Profil in Datenbank speichern.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Hallo Welt", "Error adding Profile" + profile.getFamilyName(), e);
                    }
                });
    }




    public void storeHazardAlertInFireStoreAndLocalDB(HazardAlert hazardAlert) {

    }

    public void deleteHazardAlertFromFireStoreAndLocalDB(HazardAlert hazardAlert) {

    }

    public void updateHazardAlertInFireStoreAndLocalDB(HazardAlert hazardAlert) {

    }

    public void readHazardAlertFromFireStoreAndStoreItToLocalDB(String postcode) {

    }

    public void notifyAllObservers() {

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

    /**
     * stores a BikeRack first in the FireStore and after that in the local database
     * the associated id will be generated automatically.
     *
     * @param bikeRack bikeRack to store.
     */
    @Override
    public void storeBikeRackToFireStoreAndLocalDB(BikeRack bikeRack) {
        db.collection(ConstantsFirebase.COLLECTION_BIKERACKS.toString())
                .add(bikeRack.toMap()) //generate id automatically
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() { //-> bei Erfolg
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String bikeRackID = documentReference.getId();
                        Log.d("Hallo Welt", "DocumentSnapshot added with ID: " + bikeRackID);
                        bikeRack.setFirebaseID(bikeRackID);
                        localDatabaseHelper.storeBikeRack(bikeRack);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Hallo Welt", "Error adding document", e);
                    }
                });
    }

    /**
     * reads all official BikeRacks associated to a certain postcode
     * and stores them in the local database
     *
     * @param postcode postcode as a string
     */
    @Override
    public void readBikeRacksFromFireStoreAndStoreItToLocalDB(String postcode) {
        db.collection(ConstantsFirebase.COLLECTION_OFFICIAL_BIKERACKS.toString())
                .whereEqualTo(BikeRack.ConstantsBikeRack.POSTCODE.toString(), postcode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String fireBaseId = null;
                            String name = null;
                            Position position = null;
                            Map<String, Object> map_position = null;
                            int capacity = -1;
                            boolean isEBikeStation = false;
                            boolean isExisting = false;
                            boolean isCovered = false;
                            BikeRack bikeRack = null;
                            double longitude = -1;
                            double latitude = -1;


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Hallo Welt", document.getId() + " => " + document.getData());

                                try {
                                    fireBaseId = document.getId();
                                    capacity = (int) document.get(BikeRack.ConstantsBikeRack.CAPACITY.toString());
                                    isEBikeStation = document.getBoolean(BikeRack.ConstantsBikeRack.IS_EBIKE_STATION.toString());
                                    isExisting = document.getBoolean(BikeRack.ConstantsBikeRack.IS_EXISTENT.toString());
                                    isCovered = document.getBoolean(BikeRack.ConstantsBikeRack.IS_COVERED.toString());
                                    name = document.getString(BikeRack.ConstantsBikeRack.BIKE_RACK_NAME.toString());

                                    map_position = (Map<String, Object>) document.get(Position.ConstantsPosition.POSITION.toString());
                                    longitude = (double) map_position.get(Position.ConstantsPosition.LONGITUDE.toString());
                                    latitude = (double) map_position.get(Position.ConstantsPosition.LATITUDE.toString());
                                    position = new Position(longitude, latitude);

                                    bikeRack = new BikeRack(
                                            fireBaseId, position, name, capacity, isEBikeStation, isExisting, isCovered
                                    );
                                    localDatabaseHelper.storeBikeRack(bikeRack);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.d("Hallo Welt", "Error getting documents: ", task.getException());
                        }
                    }
                });
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

    public enum ConstantsFirebase {
        COLLECTION_USERS("users"),
        COLLECTION_UTILIZATION("utilization"),
        COLLECTION_RAWGEOPOS("rawgeopos"),
        COLLECTION_COARSED_GEOPOS("coarsedgeopos"),
        COLLECTION_TRACKS("tracks"),
        COLLECTION_RATINGS("ratings"),
        COLLECTION_BIKERACKS("bikeracks"),
        COLLECTION_OFFICIAL_BIKERACKS("officialbikeracks"),
        COLLECTION_HAZARDS("hazards"),
        COLLECTION_OFFICIAL_HAZARDS("officialhazards");

        private String type;

        ConstantsFirebase(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }

}
