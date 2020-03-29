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

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.util.ObserverMechanism.FireStoreObserver;

import static android.content.ContentValues.TAG;

public class FirebaseConnection {
    public enum ConstantsFirebase{
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

    private FirebaseConnection firebaseConnection;
    private FirebaseFirestore db;
    private List<FireStoreObserver> fireStoreObservers;

    public FirebaseConnection getInstance(){
        if(this.firebaseConnection==null){
            this.firebaseConnection = new FirebaseConnection();
        }
        return this.firebaseConnection;
    }

    private FirebaseConnection(){
        this.db = FirebaseFirestore.getInstance();
        fireStoreObservers = new ArrayList<>();
    }

    /**
     * should be used to subscribe as an observer
     * @param fireStoreObserver observer regarding fireStore
     */
    public void subscribeObserver(FireStoreObserver fireStoreObserver){
        this.fireStoreObservers.add(fireStoreObserver);
    }

    public void addProfileToFirestore(Profile profile) {
        db.collection(ConstantsFirebase.COLLECTION_USERS.toString())
                .document(profile.getFirebaseAccountID()) //set the id of a given document
                .set(profile.toMap()) //set-Method: Will create or overwrite document if it is existing
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

    /**
     * stores a BikeRack first in the FireStore and after that in the local database
     * @param bikerack bikeRack to store.
     */
    public void storeBikeRackInFireStoreAndLocalDB(BikeRack bikerack){
        //TODO: REVIEW
        db.collection(ConstantsFirebase.COLLECTION_BIKERACKS.toString())
                .add(bikerack.toMap()) //generate id automatically
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() { //-> bei Erfolg
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String bikeRackID = documentReference.getId();
                        Log.d("Hallo Welt", "DocumentSnapshot added with ID: " + bikeRackID);
                        //TODO save BikeRack in Local DB
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
    public void deleteBikeRackFromFireStoreAndLocalDB(BikeRack bikeRack){
        db.collection(ConstantsFirebase.COLLECTION_BIKERACKS.toString()) //which collection?
                .document(bikeRack.getFirebaseID()) //id of the document
                .delete()//delte document
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        //TODO: Call Method from local db
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    /**
     * updates a bikeRack in the the local db and the fireStore using
     * its unique fireBaseID
     *
     * @param bikeRack bikeRack to update
     */
    public void updateBikeRackInFireStoreAndLocalDB(BikeRack bikeRack){
        db.collection(ConstantsFirebase.COLLECTION_BIKERACKS.toString())
                .document(bikeRack.getFirebaseID())
                .set(bikeRack.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        //todo: update bikeRack in local db
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    /**
     * reads all official BikeRacks associated to a certain postcode
     * and stores them in the local database
     * @param postcode postcode as a string
     */
    public void readOfficialBikeRackFromFireStoreAndStoreItToLocalDB(String postcode){
                db.collection(ConstantsFirebase.COLLECTION_OFFICIAL_BIKERACKS.toString())
                .whereEqualTo(BikeRack.ConstantsBikeRack.POSTCODE.toString(), postcode).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                //todo: store document in local db and notify observers
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void notifyAllObservers(){
        
    }

    public void updateToken() {

    }

}
