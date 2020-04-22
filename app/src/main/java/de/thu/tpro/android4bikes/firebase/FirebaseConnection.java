package de.thu.tpro.android4bikes.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.CountDownLatch;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.commands.Command;
import de.thu.tpro.android4bikes.data.commands.SearchForHazardAlertsWithPostalCodeInLocalDB;
import de.thu.tpro.android4bikes.data.commands.SearchForTracksWithPostalCodeInFireStore;
import de.thu.tpro.android4bikes.data.commands.SearchForTracksWithPostcodeInLocalDB;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.database.FireStoreDatabase;
import de.thu.tpro.android4bikes.database.LocalDatabaseHelper;
import de.thu.tpro.android4bikes.util.GeoFencing;
import de.thu.tpro.android4bikes.util.JSONHelper;
import de.thu.tpro.android4bikes.util.TimeBase;
import de.thu.tpro.android4bikes.util.compression.PositionCompressor;


public class FirebaseConnection extends Observable implements FireStoreDatabase {
    private static FirebaseConnection firebaseConnection;
    private FirebaseFirestore db;
    private LocalDatabaseHelper localDatabaseHelper;
    private String TAG = "HalloWelt";
    private Gson gson;
    private GeoFencing geoFencingHazards;
    private GeoFencing geoFencingBikeracks;
    private GeoFencing geoFencingTracks;

    //Buffer
    private CouchDBHelper cdb_writeBuffer;
    private CouchDBHelper cdb_deleteBuffer;
    private CouchDBHelper ownDataDB;


    private FirebaseConnection() {
        this.db = FirebaseFirestore.getInstance();
        localDatabaseHelper = new CouchDBHelper();
        this.gson = new Gson();
        geoFencingHazards = new GeoFencing(GeoFencing.ConstantsGeoFencing.COLLECTION_HAZARDS);
        geoFencingBikeracks = new GeoFencing(GeoFencing.ConstantsGeoFencing.COLLECTION_BIKERACKS);
        geoFencingTracks = new GeoFencing(GeoFencing.ConstantsGeoFencing.COLLECTION_TRACKS);

        //WriteBuffer und DeleteBuffer
        cdb_writeBuffer = new CouchDBHelper(CouchDBHelper.DBMode.WRITEBUFFER);
        cdb_deleteBuffer = new CouchDBHelper(CouchDBHelper.DBMode.DELETEBUFFER);
        ownDataDB = new CouchDBHelper(CouchDBHelper.DBMode.OWNDATA);
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
        try {
            JSONObject jsonObject_profile = new JSONObject(gson.toJson(profile));
            Map map_profile = gson.fromJson(jsonObject_profile.toString(), Map.class);
            db.collection(ConstantsFirebase.COLLECTION_PROFILES.toString())
                    .document(profile.getGoogleID()) //set the id of a given document
                    .set(map_profile) //set-Method: Will create or overwrite document if it is existing
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Profile " + profile.getFamilyName() + " added successfully");
                            localDatabaseHelper.storeProfile(profile);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding Profile " + profile.getFamilyName(), e);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        //Log.d(TAG, "Profile " + document.toObject(Profile.class).getFamilyName() + " got successfully"); //toObjectMethod don't works for profile!!!
                        Map map_result = document.getData();
                        localDatabaseHelper.storeProfile(map_result);
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
    public void submitBikeRackToFireStore(BikeRack bikeRack) {
        try{
            JSONObject jsonObject_bikeRack = new JSONObject(gson.toJson(bikeRack));
            Map map_bikeRack = gson.fromJson(jsonObject_bikeRack.toString(), Map.class);
            db.collection(ConstantsFirebase.COLLECTION_BIKERACKS.toString())
                    .add(map_bikeRack) //generate id automatically
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() { //-> bei Erfolg
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String firebaseID = documentReference.getId();
                            Log.d(TAG, "Bikerack with Location "
                                    + bikeRack.getPosition().getLatitude()
                                    + ","
                                    + bikeRack.getPosition().getLongitude()
                                    + " submitted successfully");
                            geoFencingBikeracks.registerDocument(documentReference.getId(), bikeRack.getPosition().getGeoPoint());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error submitting BikeRack", e);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * reads all official BikeRacks associated to a certain postcode
     * and stores them in the local database
     *
     * @param postcode postcode as a string
     */
    @Override
    public void readBikeRacksFromFireStoreAndStoreItToLocalDB(String postcode) {
        try {
        db.collection(ConstantsFirebase.COLLECTION_OFFICIAL_BIKERACKS.toString())
                .whereEqualTo(BikeRack.ConstantsBikeRack.POSTCODE.toString(), postcode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map map_result = document.getData();
                                Log.d(TAG, "Got BikeRack "+ map_result.toString());
                                localDatabaseHelper.storeBikeRack(map_result);
                            }
                        } else {
                            Log.d(TAG, "Error getting Bikerack(s): ", task.getException());

                            //notify observers that the connection to FireStore failed!
                            setChanged();
                            notifyObservers(new SearchForTracksWithPostalCodeInFireStore(postcode));
                        }
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * stores a Track and FineGrainedPosition first in the FireStore and after that in the local database
     *
     * @param track                track to store
     */
    @Override
    public void storeTrackToFireStoreAndLocalDB(Track track) {
        //TODO Review and Testing

        /*
        Document to store on FireStore:
        {
            ...
            "position": [89, -120, 77, ...] <- Compressed
        }
         */
        try {
            JSONObject jsonObject_track = new JSONObject(gson.toJson(track));
            Map map_track = gson.fromJson(jsonObject_track.toString(), Map.class);
            PositionCompressor positionCompressor = new PositionCompressor();

            //compress fineGrainedPositions
            byte[] compressedPositions = positionCompressor.compressPositions(track.getFineGrainedPositions());

            //BLOB
            Blob blob_trackpositions_compressed = Blob.fromBytes(compressedPositions);

            //replace fineGrainedPositions by compressed version
            map_track.put(Track.ConstantsTrack.FINEGRAINEDPOSITIONS.toString(), blob_trackpositions_compressed);

            db.collection(ConstantsFirebase.COLLECTION_TRACKS.toString())
                    .add(map_track) //generate id automatically
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() { //-> bei Erfolg
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String firebaseID = documentReference.getId();
                            track.setFirebaseID(firebaseID);
                            Log.d(TAG, "Track " + track.getName() + " added successfully "+ TimeBase.getCurrentUnixTimeStamp());
                            geoFencingTracks.registerDocument(documentReference.getId(), track.getFineGrainedPositions().get(0).getGeoPoint());
                            localDatabaseHelper.storeTrack(track);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error submitting BikeRack", e);
                        }
                    });
            Log.d("HalloWelt","Ende "+ TimeBase.getCurrentUnixTimeStamp());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * reads a Track with CoarseGrainedInformation from the FireStore and saves it in the local database
     *
     * @param postcode trackID as a String
     */
    @Override
    public void readTracksFromFireStoreAndStoreItToLocalDB(String postcode) {
        //TODO Review and Testing
        db.collection(ConstantsFirebase.COLLECTION_TRACKS.toString())
                .whereEqualTo(Track.ConstantsTrack.POSTCODE.toString(), postcode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d(TAG, "Track " + document.toObject(Track.class).getName() + " got successfully");
                                    try {

                                        PositionCompressor positionCompressor = new PositionCompressor();
                                        Blob blob_compressedPositions = document.getBlob(Track.ConstantsTrack.FINEGRAINEDPOSITIONS.toString());
                                        byte[] compressedPositions = blob_compressedPositions.toBytes();

                                        List<Position> fineGrainedPositions = positionCompressor.decompressPositions(compressedPositions);

                                        Map<String, Object> map_track = document.getData();


                                        JSONHelper<Track> jsonHelper_Track = new JSONHelper(Track.class);
                                        JSONHelper<Position> jsonHelper_Position = new JSONHelper(Position.class);
                                        ArrayList<Map<String, Object>> arraylist_of_map_string_Object_positions = new ArrayList<>();

                                        JSONObject jsonObject_position = null;
                                        Map map_position = null;

                                        for (Position pos : fineGrainedPositions) {
                                            jsonObject_position = jsonHelper_Position.convertObjectToJSONObject(pos);
                                            map_position = gson.fromJson(jsonObject_position.toString(), Map.class);
                                            arraylist_of_map_string_Object_positions.add(map_position);
                                        }

                                        map_track.put(Track.ConstantsTrack.FINEGRAINEDPOSITIONS.toString(), arraylist_of_map_string_Object_positions);
                                        JSONObject jsonObject_track = new JSONObject(map_track);
                                        Track track = jsonHelper_Track.convertJSONObjectToObject(jsonObject_track);

                                        track.setFineGrainedPositions(fineGrainedPositions);
                                        localDatabaseHelper.storeTrack(track);

                                        //advice observers to access new data stored in the local database using the command pattern
                                        Command command = new SearchForTracksWithPostcodeInLocalDB(postcode);
                                        setChanged();
                                        notifyObservers(command);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Log.d(TAG, "No such Track");

                                setChanged();
                                notifyObservers(null);
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                            //Notify ViewModel "ViewModelTrack" that connection to FireStore isn't possible

                            setChanged();
                            notifyObservers(null);
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
    public void submitHazardAlertToFireStore(HazardAlert hazardAlert) {
        //TODO Review and Testing
        try{
            JSONObject jsonObject_hazardAlert = new JSONObject(gson.toJson(hazardAlert));
            Map map_hazardAlert = gson.fromJson(jsonObject_hazardAlert.toString(), Map.class);
            db.collection(ConstantsFirebase.COLLECTION_HAZARDS.toString())
                    .add(map_hazardAlert) //generate id automatically
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() { //-> bei Erfolg
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String firebaseID = documentReference.getId();
                            Log.d(TAG, "HazardAlert with Location "
                                    + hazardAlert.getPosition().getLatitude()
                                    + ","
                                    + hazardAlert.getPosition().getLongitude()
                                    + " submitted successfully");
                            geoFencingHazards.registerDocument(documentReference.getId(), hazardAlert.getPosition().getGeoPoint());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error submitting HazardAlert", e);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
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
                                Map map_result = document.getData();
                                Log.d(TAG, "Got Hazard "+ map_result.toString());
                                try {
                                    localDatabaseHelper.storeHazardAlerts(map_result);
                                    Command c = new SearchForHazardAlertsWithPostalCodeInLocalDB(postcode);
                                    setChanged();
                                    notifyObservers(c);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting Hazard(s): ", task.getException());
                            setChanged();
                            notifyObservers(null);
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
        try{
            CountDownLatch countDownLatch = new CountDownLatch(1);
            //TODO Review and Testing
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < utilization.size(); i++) {
                map.put(Integer.toString(i), utilization.get(i));
            }
            db.collection(ConstantsFirebase.COLLECTION_UTILIZATION.toString())
                    .add(map)//generate id automatically
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() { //-> bei Erfolg
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Utilization updated");
                            cdb_writeBuffer.resetUtilization();
                            countDownLatch.countDown();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding Utilization", e);
                            countDownLatch.countDown();
                        }
                    });
            countDownLatch.await();
            Log.d("HalloWelt","Await is over");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * TODO
     *
     * @param tracks
     */
    @Override
    public void readProfilesBasedOnTracks(List<Track> tracks) {
        //TODO: PLEASE BY FABI AND PATRICK.

        //TODO: MAINTAIN FOLLOWING CODE BASE:
        Map<Track, Profile> map_track_profile = new HashMap<>();

        //Implementation
        //Get all Google IDs from tracks and load those profiles
        //Afterwards: Save combination of track and profile in Map!!!!
        //...

        tracks.forEach(entry -> {
            map_track_profile.put(entry,createAnonProfile());
        });

        setChanged();
        notifyObservers(map_track_profile);
    }

    private Profile createAnonProfile() {
        List<Achievement> list = new ArrayList<>();
        return new Profile("Android","Biker","-1",0x2e8b57,0,list);
    }


    //Methods for buffering################################################################################
    @Override
    public void storeProfileToFireStore(Profile profile) {
        try {
            //may be final
            CountDownLatch countDownLatch = new CountDownLatch(1);

            JSONObject jsonObject_profile = new JSONObject(gson.toJson(profile));
            Map map_profile = gson.fromJson(jsonObject_profile.toString(), Map.class);
            db.collection(ConstantsFirebase.COLLECTION_PROFILES.toString())
                    .document(profile.getGoogleID()) //set the id of a given document
                    .set(map_profile) //set-Method: Will create or overwrite document if it is existing
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Profile " + profile.getFamilyName() + " added successfully");

                            //Delete profile from the WriteBuffer:
                            cdb_writeBuffer.deleteProfile(profile);
                            //update and creation:
                            ownDataDB.updateMyOwnProfile(profile);

                            //onSuccess():
                            countDownLatch.countDown();
                            Log.d("HalloWelt", "Decremented Countdown-Letch-Success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding Profile " + profile.getFamilyName(), e);

                            //onSuccess():
                            countDownLatch.countDown();
                            Log.d("HalloWelt", "Decremented Countdown-Letch-Failure");
                        }
                    });
            countDownLatch.await();
            Log.d("HalloWelt", "Await is over");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteProfileFromFireStore(Profile profile) {
        try {
            //may be final
            CountDownLatch countDownLatch = new CountDownLatch(1);

            db.collection(ConstantsFirebase.COLLECTION_PROFILES.toString()).document(profile.getGoogleID())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Profile successfully deleted!");

                            //Delete profile from delete buffer
                            cdb_deleteBuffer.deleteProfile(profile);

                            //Delete profile from ownDataDB
                            ownDataDB.deleteProfile(profile);

                            //should always be last operation
                            countDownLatch.countDown();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting Profile", e);

                            //onSuccess():
                            countDownLatch.countDown();
                        }
                    });

            countDownLatch.await();
            Log.d("HalloWelt", "Await is over");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void storeTrackInFireStore(Track track) {
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            JSONObject jsonObject_track = new JSONObject(gson.toJson(track));
            Map map_track = gson.fromJson(jsonObject_track.toString(), Map.class);
            PositionCompressor positionCompressor = new PositionCompressor();

            //compress fineGrainedPositions
            byte[] compressedPositions = positionCompressor.compressPositions(track.getFineGrainedPositions());

            //BLOB
            Blob blob_trackpositions_compressed = Blob.fromBytes(compressedPositions);

            //replace fineGrainedPositions by compressed version
            map_track.put(Track.ConstantsTrack.FINEGRAINEDPOSITIONS.toString(), blob_trackpositions_compressed);

            db.collection(ConstantsFirebase.COLLECTION_TRACKS.toString())
                    .add(map_track) //generate id automatically
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() { //-> bei Erfolg
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String firebaseID = documentReference.getId();
                            track.setFirebaseID(firebaseID);
                            Log.d(TAG, "Track " + track.getName() + " added successfully " + TimeBase.getCurrentUnixTimeStamp());
                            geoFencingTracks.registerDocument(documentReference.getId(), track.getFineGrainedPositions().get(0).getGeoPoint());

                            //track to store by own user:
                            ownDataDB.storeTrack(track);

                            //update buffer
                            cdb_writeBuffer.deleteTrack(track);

                            countDownLatch.countDown();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error submitting BikeRack", e);
                            countDownLatch.countDown();
                        }
                    });
            countDownLatch.await();
            Log.d("HalloWelt", "Await is over!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTrackFromFireStore(Track track) {
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            db.collection(ConstantsFirebase.COLLECTION_TRACKS.toString())
                    .document(track.getFirebaseID())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Track successfully deleted!");

                            //delete track from own db
                            ownDataDB.deleteTrack(track.getFirebaseID());

                            //delete Track from delete buffer
                            cdb_deleteBuffer.deleteTrack(track.getFirebaseID());

                            countDownLatch.countDown();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting Track", e);

                            countDownLatch.countDown();
                        }
                    });
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void storeBikeRackInFireStore(BikeRack bikeRack) {
        try{
            CountDownLatch countDownLatch = new CountDownLatch(1);
            JSONObject jsonObject_bikeRack = new JSONObject(gson.toJson(bikeRack));
            Map map_bikeRack = gson.fromJson(jsonObject_bikeRack.toString(), Map.class);
            db.collection(ConstantsFirebase.COLLECTION_BIKERACKS.toString())
                    .add(map_bikeRack) //generate id automatically
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() { //-> bei Erfolg
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String firebaseID = documentReference.getId();
                            Log.d(TAG, "Bikerack with Location "
                                    + bikeRack.getPosition().getLatitude()
                                    + ","
                                    + bikeRack.getPosition().getLongitude()
                                    + " submitted successfully");
                            geoFencingBikeracks.registerDocument(documentReference.getId(), bikeRack.getPosition().getGeoPoint());
                            ownDataDB.storeBikeRack(bikeRack);
                            cdb_writeBuffer.deleteBikeRack(bikeRack);
                            countDownLatch.countDown();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error submitting BikeRack", e);
                            countDownLatch.countDown();
                        }
                    });
            Log.d("HalloWelt", "Ende " + TimeBase.getCurrentUnixTimeStamp());
            countDownLatch.await();
            Log.d("HalloWelt", "Await is over!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void storeHazardAlertInFireStore(HazardAlert hazardAlert) {
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            JSONObject jsonObject_hazardAlert = new JSONObject(gson.toJson(hazardAlert));
            Map map_hazardAlert = gson.fromJson(jsonObject_hazardAlert.toString(), Map.class);
            db.collection(ConstantsFirebase.COLLECTION_HAZARDS.toString())
                    .add(map_hazardAlert) //generate id automatically
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() { //-> bei Erfolg
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String firebaseID = documentReference.getId();
                            Log.d(TAG, "HazardAlert with Location "
                                    + hazardAlert.getPosition().getLatitude()
                                    + ","
                                    + hazardAlert.getPosition().getLongitude()
                                    + " submitted successfully");
                            geoFencingHazards.registerDocument(documentReference.getId(), hazardAlert.getPosition().getGeoPoint());

                            //track to store by own user:
                            ownDataDB.storeHazardAlerts(hazardAlert);

                            //update buffer
                            cdb_writeBuffer.deleteHazardAlert(hazardAlert);

                            countDownLatch.countDown();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error submitting HazardAlert", e);

                            countDownLatch.countDown();
                        }
                    });
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Methods for buffering################################################################################

    public enum ConstantsFirebase {
        COLLECTION_PROFILES("profiles"),
        COLLECTION_UTILIZATION("utilization"),
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

    public enum STATUSCODES {
        SUCCESS, ERROR
    }

}
