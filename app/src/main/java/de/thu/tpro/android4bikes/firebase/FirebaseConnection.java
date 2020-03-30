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
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.database.Android4BikesLocalDatabaseHelper;

import de.thu.tpro.android4bikes.database.CouchDbHelper;
import de.thu.tpro.android4bikes.util.ObserverMechanism.FireStoreObserver;

public class FirebaseConnection {
    private static FirebaseConnection firebaseConnection;
    private FirebaseFirestore db;
    private List<FireStoreObserver> fireStoreObservers;
    private Android4BikesLocalDatabaseHelper android4BikesDatabaseHelper;
    private FirebaseConnection() {
        this.db = FirebaseFirestore.getInstance();
        fireStoreObservers = new ArrayList<>();
        android4BikesDatabaseHelper = new CouchDbHelper();
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

    /**
     * stores a BikeRack first in the FireStore and after that in the local database
     * the associated id will be generated automatically.
     *
     * @param bikerack bikeRack to store.
     */
    public void storeBikeRackInFireStoreAndLocalDB(BikeRack bikerack) {
        //TODO: REVIEW
        db.collection(ConstantsFirebase.COLLECTION_BIKERACKS.toString())
                .add(bikerack.toMap()) //generate id automatically
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() { //-> bei Erfolg
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String bikeRackID = documentReference.getId();
                        Log.d("Hallo Welt", "DocumentSnapshot added with ID: " + bikeRackID);
                        android4BikesDatabaseHelper.storeBikeRack(bikerack);
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
     * deletes a bikeRack from the local db and the fireStore using
     * its unique fireBaseID
     *
     * @param bikeRack bikeRack to delete
     */
    public void deleteBikeRackFromFireStoreAndLocalDB(BikeRack bikeRack) {
        db.collection(ConstantsFirebase.COLLECTION_BIKERACKS.toString()) //which collection?
                .document(bikeRack.getFirebaseID()) //id of the document
                .delete()//delte document
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Hallo Welt", "DocumentSnapshot successfully deleted!");
                        android4BikesDatabaseHelper.deleteBikeRack(bikeRack);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Hallo Welt", "Error deleting document", e);
                    }
                });
    }


    /**
     * reads all official BikeRacks associated to a certain postcode
     * and stores them in the local database
     *
     * @param postcode postcode as a string
     */
    public void readOfficialBikeRackFromFireStoreAndStoreItToLocalDB(String postcode) {
        db.collection(ConstantsFirebase.COLLECTION_OFFICIAL_BIKERACKS.toString())
                .whereEqualTo(BikeRack.ConstantsBikeRack.POSTCODE.toString(), postcode).get()
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


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Hallo Welt", document.getId() + " => " + document.getData());

                                try {
                                    fireBaseId = document.getId();
                                    capacity = (int) document.get(BikeRack.ConstantsBikeRack.CAPACITY.toString());
                                    isEBikeStation = (boolean) document.get(BikeRack.ConstantsBikeRack.IS_EBIKE_STATION.toString());
                                    isExisting = (boolean) document.get(BikeRack.ConstantsBikeRack.IS_EXISTENT.toString());
                                    isCovered = (boolean) document.get(BikeRack.ConstantsBikeRack.IS_COVERED.toString());
                                    name = String.valueOf(document.get(BikeRack.ConstantsBikeRack.BIKE_RACK_NAME.toString()));

                                    map_position = (Map<String, Object>) document.get(Position.ConstantsPosition.POSITION.toString());
                                    double longitude = (double) map_position.get(Position.ConstantsPosition.LONGITUDE.toString());
                                    double latitude = (double) map_position.get(Position.ConstantsPosition.LATITUDE.toString());
                                    position = new Position(longitude, latitude);

                                    bikeRack = new BikeRack(
                                            fireBaseId, position, name, capacity, isEBikeStation, isExisting, isCovered
                                    );
                                    android4BikesDatabaseHelper.storeBikeRack(bikeRack);

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
