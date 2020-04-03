package de.thu.tpro.android4bikes.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
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

import static java.util.stream.Collectors.toMap;

public class FirebaseConnection implements FireStoreDatabase {
    private static FirebaseConnection firebaseConnection;
    private FirebaseFirestore db;
    private LocalDatabaseHelper localDatabaseHelper;
    private String TAG = "HalloWelt";

    private FirebaseConnection() {
        this.db = FirebaseFirestore.getInstance();
        localDatabaseHelper = new CouchDBHelper();
    }

    public static FirebaseConnection getInstance() {
        if (firebaseConnection == null) {
            firebaseConnection = new FirebaseConnection();
        }
        return firebaseConnection;
    }

    /**
     * stores a Profile first in the FireStore and after that in the local database
     *
     * @param profile profile to store
     */
    @Override
    public void storeProfileToFireStoreAndLocalDB(Profile profile) {
        //TODO Review and Testing
        db.collection(ConstantsFirebase.COLLECTION_PROFILES.toString())
                .document(profile.getGoogleID()) //set the id of a given document
                .set(profile) //set-Method: Will create or overwrite document if it is existing
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Profile " + profile.getFamilyName() + " added successfully");
                        try {
                            localDatabaseHelper.storeProfile(profile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding Profile " + profile.getFamilyName(), e);
                    }
                });
    }

    /**
     * reads a Profile from the FireStore and saves it in the local database
     *
     * @param googleID google AccountId as a String
     */
    @Override
    public void readProfileFromFireStoreAndStoreItToLocalDB(String googleID) {
        //TODO Review and Testing
        DocumentReference docRef = db.collection(ConstantsFirebase.COLLECTION_PROFILES.toString()).document(googleID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Profile " + document.toObject(Profile.class).getFamilyName() + " got successfully");
                        try {
                            localDatabaseHelper.storeProfile(document.toObject(Profile.class));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d(TAG, "No such Profile");
                        //TODO Exception Document not found

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    //TODO Exception no Connection
                }
            }
        });
    }

    /**
     * updates a Profile in the FireStore and saves it in the local database
     *
     * @param profile profile to update
     */
    @Override
    public void updateProfileInFireStoreAndLocalDB(Profile profile) {
        //TODO Review and Testing
        storeProfileToFireStoreAndLocalDB(profile); //TODO update with store in locale DB?
    }

    /**
     * deletes a Profile from the FireStore and after that in the local database
     *
     * @param googleID profile to delete
     */
    @Override
    public void deleteProfileFromFireStoreAndLocalDB(String googleID) {
        //TODO Review and Testing
        db.collection(ConstantsFirebase.COLLECTION_PROFILES.toString()).document(googleID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Profile successfully deleted!");
                        localDatabaseHelper.deleteProfile(googleID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting Profile", e);
                    }
                });

    }

    /**
     * submits a BikeRack to the FireStore
     * which gets validated by the Cloudfunction
     * to generate an Official Bikerack
     * the associated id will be generated automatically.
     *
     * @param bikeRack bikeRack to store.
     */
    @Override
    public void submitBikeRackToFireStoreAndLocalDB(BikeRack bikeRack) {
        db.collection(ConstantsFirebase.COLLECTION_BIKERACKS.toString())
                .add(bikeRack) //generate id automatically
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() { //-> bei Erfolg
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String firebaseID = documentReference.getId();
                        Log.d(TAG, "Bikerack with Location "
                                + bikeRack.getPosition().getLatitude()
                                + ","
                                + bikeRack.getPosition().getLongitude()
                                + " submitted successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error submitting BikeRack", e);
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
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "Bikerack with Location "
                                        + document.toObject(BikeRack.class).getPosition().getLatitude()
                                        + ","
                                        + document.toObject(BikeRack.class).getPosition().getLongitude()
                                        + " got successfully");

                                try {
                                    localDatabaseHelper.storeBikeRack(document.toObject(BikeRack.class));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting Bikerack(s): ", task.getException());
                        }
                    }
                });
    }

    /**
     * stores a Track and FineGrainedPosition first in the FireStore and after that in the local database
     *
     * @param track                track to store
     * @param fineGrainedPositions fine grained position data for corresponding track
     */
    @Override
    public void storeTrackToFireStoreAndLocalDB(Track track, FineGrainedPositions fineGrainedPositions) {
        //TODO Review and Testing
        db.collection(ConstantsFirebase.COLLECTION_PROFILES.toString())
                .add(track) //generate id automatically
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() { //-> bei Erfolg
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String firebaseID = documentReference.getId();
                        Log.d(TAG, "Track " + track.getName() + " added successfully");

                        try {
                            track.setFirebaseID(firebaseID);
                            fineGrainedPositions.setFirebaseID(track.getFirebaseID()); //fineGrainedPositions has the track id

                            db.collection(ConstantsFirebase.COLLECTION_FINE_GEOPOSITIONS.toString())
                                    .document(fineGrainedPositions.getFirebaseID()) //set the id of a given document
                                    .set(fineGrainedPositions) //set-Method: Will create or overwrite document if it is existing
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Fine-GeoPositions added successfully");
                                            try {
                                                localDatabaseHelper.storeTrack(track);
                                                localDatabaseHelper.storeFineGrainedPositions(fineGrainedPositions);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding Fine-GeoPositions", e);
                                        }
                                    });


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error submitting BikeRack", e);
                    }
                });
    }

    /**
     * reads a Track with CoarseGrainedInformation from the FireStore and saves it in the local database
     *
     * @param fireBaseID trackID as a String
     */
    @Override
    public void readCoarseGrainedTracksFromFireStoreAndStoreThemToLocalDB(String fireBaseID) {
        //TODO Review and Testing
        DocumentReference docRef = db.collection(ConstantsFirebase.COLLECTION_TRACKS.toString()).document(fireBaseID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Track " + document.toObject(Track.class).getName() + " got successfully");
                        try {
                            localDatabaseHelper.storeTrack(document.toObject(Track.class));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d(TAG, "No such Track");
                        //TODO Exception Document not found

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    //TODO Exception no Connection
                }
            }
        });
    }

    /**
     * reads FineGrainedInformation from the FireStore and saves it in the local database
     *
     * @param fireBaseID trackID as a String
     */
    @Override
    public void readFineGrainedTracksFromFireStoreAndStoreThemToLocalDB(String fireBaseID) {
        //TODO Review and Testing
        DocumentReference docRef = db.collection(ConstantsFirebase.COLLECTION_FINE_GEOPOSITIONS.toString()).document(fireBaseID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Fine-GeoPosition got successfully");
                        try {
                            localDatabaseHelper.storeFineGrainedPositions(document.toObject(FineGrainedPositions.class));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d(TAG, "No such Fine-GeoPosition");
                        //TODO Exception Document not found

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    //TODO Exception no Connection
                }
            }
        });

    }

    /**
     * deletes a Track from the FireStore and after that in the local database
     *
     * @param fireBaseID trackID as a String
     */
    @Override
    public void deleteTrackFromFireStoreAndLocalDB(String fireBaseID) {
        //TODO Review and Testing
        db.collection(ConstantsFirebase.COLLECTION_TRACKS.toString())
                .document(fireBaseID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Track successfully deleted!");
                        localDatabaseHelper.deleteTrack(fireBaseID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting Track", e);
                    }
                });
    }

    /**
     * submits a Hazard to the FireStore
     * which gets validated by the Cloudfunction
     * to generate a Official Bikerack
     * the associated id will be generated automatically.
     *
     * @param hazardAlert hazard alert which gets submitted
     */
    @Override
    public void submitHazardAlertToFireStoreAndLocalDB(HazardAlert hazardAlert) {
        //TODO Review and Testing
        db.collection(ConstantsFirebase.COLLECTION_HAZARDS.toString())
                .add(hazardAlert) //generate id automatically
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() { //-> bei Erfolg
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String firebaseID = documentReference.getId();
                        Log.d(TAG, "HazardAlert with Location "
                                + hazardAlert.getPosition().getLatitude()
                                + ","
                                + hazardAlert.getPosition().getLongitude()
                                + " submitted successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error submitting HazardAlert", e);
                    }
                });
    }

    /**
     * reads all official Hazards associated to a certain postcode
     * and stores them in the local database
     *
     * @param postcode as a String
     */
    @Override
    public void readHazardAlertsFromFireStoreAndStoreItToLocalDB(String postcode) {
        //TODO Review and Testing
        db.collection(ConstantsFirebase.COLLECTION_OFFICIAL_HAZARDS.toString())
                .whereEqualTo(HazardAlert.ConstantsHazardAlert.POSTCODE.toString(), postcode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "Hazard with Location "
                                        + document.toObject(HazardAlert.class).getPosition().getLatitude()
                                        + ","
                                        + document.toObject(HazardAlert.class).getPosition().getLongitude()
                                        + " got successfully");

                                try {
                                    localDatabaseHelper.storeHazardAlerts(document.toObject(HazardAlert.class));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting Hazard(s): ", task.getException());
                        }
                    }
                });
    }

    /**
     * saves position data to Firebase in order to contribute to Utilization Heat mapping
     *
     * @param utilization List with position data
     */
    @Override
    public void storeUtilizationToFireStore(List<Position> utilization) {
        //TODO Review and Testing
        Map<String,Object> map = new HashMap<>();
        utilization.forEach(entry-> map.put(entry.toString(),entry));
        db.collection(ConstantsFirebase.COLLECTION_UTILIZATION.toString())
                .document(ConstantsFirebase.DOCUMENT_UTILIZATION.toString()) //set the id of a given document
                .set(map, SetOptions.merge()) //set-Method with merge: Will append document if it is existing
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Utilization updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding Utilization", e);
                    }
                });

    }

    public enum ConstantsFirebase {
        COLLECTION_PROFILES("profiles"),
        COLLECTION_UTILIZATION("utilization"),
        COLLECTION_FINE_GEOPOSITIONS("finegeopositions"),
        COLLECTION_TRACKS("tracks"),
        COLLECTION_BIKERACKS("bikeracks"),
        COLLECTION_OFFICIAL_BIKERACKS("officialbikeracks"),
        COLLECTION_HAZARDS("hazards"),
        COLLECTION_OFFICIAL_HAZARDS("officialhazards"),
        DOCUMENT_UTILIZATION("9xapHttnwM5eXheJStQf");

        private String type;

        ConstantsFirebase(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }

}
