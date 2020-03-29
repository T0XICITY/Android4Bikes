package de.thu.tpro.android4bikes.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.Profile;

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

    public static void addProfileToFirestore(Profile profile) {
        FirebaseFirestore.getInstance().collection(ConstantsFirebase.COLLECTION_USERS.toString())
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
    public static void storeBikeRackInFireStoreAndLocalDB(BikeRack bikerack){
        //TODO: REVIEW
        FirebaseFirestore.getInstance()
                .collection(ConstantsFirebase.COLLECTION_BIKERACKS.toString())
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

    public static void storeRatingInFireStoreAndLocalDB(){

    }




    public static BikeRack updateBikeRackInFireStoreAndLocalDB(){
        return null;
    }

    public static void deleteBikeRackInFirestoreAndLocalDB(){
        //TODO: CALL CLOUD-FUNCTION
        return;
    }

    public void updateToken() {

    }

}
