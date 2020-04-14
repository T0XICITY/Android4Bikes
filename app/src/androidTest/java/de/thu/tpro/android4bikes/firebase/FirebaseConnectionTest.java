package de.thu.tpro.android4bikes.firebase;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.achievements.KmAchievement;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Rating;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDB;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.util.GeoFencing;
import de.thu.tpro.android4bikes.util.GlobalContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests if the communication between {@link com.google.firebase.firestore.FirebaseFirestore} and the application
 * works correctly. It tests especially if the objects (e.g {@link BikeRack}) involved in the communication are
 * exchanged successfully and in the correct format.
 */
public class FirebaseConnectionTest {
    private static FirebaseConnection firebaseConnection;
    private static CouchDBHelper couchDBHelper;
    private static CouchDB couchDB;
    private static CountDownLatch authSignal = null;
    private static FirebaseAuth auth;



    @BeforeClass
    public static void setUp() throws InterruptedException {
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
        firebaseConnection = FirebaseConnection.getInstance();
        couchDBHelper = new CouchDBHelper();
        couchDB = CouchDB.getInstance();
        authSignal = new CountDownLatch(1);
        auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword("test@test.de", "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    final AuthResult result = task.getResult();
                    authSignal.countDown();
                }
            });

    }


    @Test
    public void testProfileFunctionality(){
        storeProfileToFireStoreAndLocalDB();
        readProfileFromFireStoreAndStoreItToLocalDB();
        updateProfile();
        deleteProfile();
    }
    @Test
    public void geoTest(){
        /**
         * New GeoFencing instance
         */

        GeoPoint center = new GeoPoint(48.403498, 9.978170);
        double radius_in_km = 14.9d;
        GeoFencing geoFencing_hazards = new GeoFencing(GeoFencing.ConstantsGeoFencing.COLLECTION_HAZARDS);
        //FirebaseConnection.getInstance().submitHazardAlertToFireStore(new HazardAlert(
        //HazardAlert.HazardType.GENERAL, new Position(9.997507, 48.408880), 120000, 5, "12345"
        //));

        BikeRack bikeRack_THU = new BikeRack(
                "pfo4eIrvzrI0m363KF0K", new Position(9.997507, 48.408880), "THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true, false);
        FirebaseConnection.getInstance().submitBikeRackToFireStore(bikeRack_THU);

        /**
         * Register positions in Firestore using GeoFirestore
         */

        /*
        PositionProvider.get50kmRadiusPositionstest().forEach(geoFencing_bikeRacks::registerDocument);

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        /**
         * Listen to Geofence
         */
        geoFencing_hazards.setupGeofence(center, radius_in_km);
        geoFencing_hazards.startGeoFenceListener();
    }

    public void storeProfileToFireStoreAndLocalDB() {
        couchDB.clearDB(couchDB.getDatabaseFromName(CouchDB.DatabaseNames.DATABASE_PROFILE));

        Profile profile_kostas = this.createProfile();

        firebaseConnection.storeProfileToFireStoreAndLocalDB(profile_kostas);

        //wait a few seconds because of the asynchronous process of storing data to FireBase
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Profile read_profile = couchDBHelper.readProfile(profile_kostas.getGoogleID());
        assertEquals(profile_kostas, read_profile);


    }

    public void readProfileFromFireStoreAndStoreItToLocalDB(){
        couchDB.clearDB(couchDB.getDatabaseFromName(CouchDB.DatabaseNames.DATABASE_PROFILE));

        Profile profile_kostas = this.createProfile();

        firebaseConnection.readProfileFromFireStoreAndStoreItToLocalDB(profile_kostas.getGoogleID());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Profile read_profile = couchDBHelper.readProfile(profile_kostas.getGoogleID());
        assertEquals(profile_kostas, read_profile);
    }

    public void updateProfile(){
        couchDB.clearDB(couchDB.getDatabaseFromName(CouchDB.DatabaseNames.DATABASE_PROFILE));

        Profile profile_kostas = this.createProfile();

        firebaseConnection.storeProfileToFireStoreAndLocalDB(profile_kostas);

        //wait a few seconds because of the asynchronous process of storing data to FireBase
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Profile read_profile = couchDBHelper.readProfile(profile_kostas.getGoogleID());
        assertEquals(profile_kostas, read_profile);

        Profile profile_kostas_updated = profile_kostas;
        profile_kostas_updated.setColor(255);
        profile_kostas_updated.setOverallDistance(999);
        List<Achievement> achievements = profile_kostas.getAchievements();
        achievements.add(new KmAchievement("",55,6.8,815,999));
        profile_kostas_updated.addAchievements(achievements);

        firebaseConnection.updateProfileInFireStoreAndLocalDB(profile_kostas_updated);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        read_profile = couchDBHelper.readProfile(profile_kostas.getGoogleID());

        assertEquals(profile_kostas_updated,read_profile);
    }

    public void deleteProfile(){
        couchDB.clearDB(couchDB.getDatabaseFromName(CouchDB.DatabaseNames.DATABASE_PROFILE));

        Profile profile_kostas = this.createProfile();


        firebaseConnection.deleteProfileFromFireStoreAndLocalDB(profile_kostas.getGoogleID());

        //wait a few seconds because of the asynchronous process of storing data to FireBase
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNull(couchDBHelper.readProfile(profile_kostas.getGoogleID()));
    }

    /**
     * 1. Generates a {@link de.thu.tpro.android4bikes.data.model.BikeRack} 'THU' that is located in Ulm
     * 2. Stores the same {@link de.thu.tpro.android4bikes.data.model.BikeRack} 'THU' three times to the {@link com.google.firebase.firestore.FirebaseFirestore}
     * 3. Reads all official exemplars of the class {@link de.thu.tpro.android4bikes.data.model.BikeRack} with the postal code '89075' from the {@link com.google.firebase.firestore.FirebaseFirestore} and stores them to the {@link com.couchbase.lite.CouchbaseLite}
     * 4. Reads all stored {@link de.thu.tpro.android4bikes.data.model.BikeRack} with the postal code '89075' from the {@link com.couchbase.lite.CouchbaseLite}
     * <p>
     * Requirements for passing this test:
     * -the {@link de.thu.tpro.android4bikes.data.model.BikeRack} 'THU' should be in the list of read {@link de.thu.tpro.android4bikes.data.model.BikeRack}s
     */
    @Test
    public void submitAndReadBikeRack() {
        //generate a bike rack
        BikeRack bikeRack_THU = generateTHUBikeRack();

        //store the same bike rack three times in the FireStore. After the third occurence
        //of this bike rack in the FireStore it should be an official bike rack. Only
        //official bike racks can be read!!!
        for (int i = 0; i < 3; ++i) {
            firebaseConnection.submitBikeRackToFireStore(bikeRack_THU);
        }
        //wait a few seconds because of the asynchronous process of storing data to FireBase
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //read official bike racks from couch db and store them to the local db
        firebaseConnection.readBikeRacksFromFireStoreAndStoreItToLocalDB(bikeRack_THU.getPostcode());
        //wait a few seconds because of the asynchronous process of deleting data from FireBase
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //read all bike racks from the local db with the postcode '89075'
        List<BikeRack> bikeRacks_with_postcode_89075 = couchDBHelper.readBikeRacks(bikeRack_THU.getPostcode());

        //the just stored bike rack has to be contained in the list of official bike racks
        assertTrue(bikeRacks_with_postcode_89075.contains(bikeRack_THU));
    }

    /**
     * 1. Generates a {@link de.thu.tpro.android4bikes.data.model.Track} 'THU' that is located in Ulm
     * 2. Deletes the {@link de.thu.tpro.android4bikes.data.model.Track} 'THU' in the {@link com.google.firebase.firestore.FirebaseFirestore} and {@link com.couchbase.lite.CouchbaseLite} database
     * 3. Stores the {@link de.thu.tpro.android4bikes.data.model.Track} 'THU' to the {@link com.google.firebase.firestore.FirebaseFirestore} and {@link com.couchbase.lite.CouchbaseLite} database
     * 4. Reads all official exemplars of the class {@link de.thu.tpro.android4bikes.data.model.Track} with the postal code '89075' from the {@link com.google.firebase.firestore.FirebaseFirestore} and stores them to the {@link com.couchbase.lite.CouchbaseLite} database
     * 5. Reads all stored {@link de.thu.tpro.android4bikes.data.model.Track} with the postal code '89075' from the {@link com.couchbase.lite.CouchbaseLite}
     * <p>
     * Requirements for passing this test:
     * -the {@link de.thu.tpro.android4bikes.data.model.Track} 'THU' should be in the list of read {@link de.thu.tpro.android4bikes.data.model.Track}s
     */
    @Test
    public void storeAndReadTrack() {
        //Generate Track THU (postal code 89075)
        Track track_THU = generateTrack();

        //Delete track from FireStore and local database if it is existing
        firebaseConnection.deleteTrackFromFireStoreAndLocalDB(track_THU.getFirebaseID());
        //wait a few seconds because of the asynchronous process of deleting data from FireBase
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Store the track to FireStore and local database
        firebaseConnection.storeTrackToFireStoreAndLocalDB(track_THU);
        //wait a few seconds because of the asynchronous process of storing data to FireBase
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //read all tracks with the postal code '89075' and store them to the local database
        firebaseConnection.readTracksFromFireStoreAndStoreItToLocalDB(track_THU.getPostcode());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Track> list_read_tracks_with_postcode_89075 = couchDBHelper.readTracks(track_THU.getPostcode());
        assertTrue(list_read_tracks_with_postcode_89075.contains(track_THU));

        //delete Track finally from db
        firebaseConnection.deleteTrackFromFireStoreAndLocalDB(track_THU.getFirebaseID());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. Generates a {@link de.thu.tpro.android4bikes.data.model.Track} 'THU' that is located in Ulm
     * 2. Deletes the {@link de.thu.tpro.android4bikes.data.model.Track} 'THU' in the {@link com.google.firebase.firestore.FirebaseFirestore} and {@link com.couchbase.lite.CouchbaseLite} database
     * 3. Stores the {@link de.thu.tpro.android4bikes.data.model.Track} 'THU' to the {@link com.google.firebase.firestore.FirebaseFirestore} and {@link com.couchbase.lite.CouchbaseLite} database
     * 4. Reads all official exemplars of the class {@link de.thu.tpro.android4bikes.data.model.Track} with the postal code '89075' from the {@link com.google.firebase.firestore.FirebaseFirestore} and stores them to the {@link com.couchbase.lite.CouchbaseLite} database
     * 5. Reads all stored {@link de.thu.tpro.android4bikes.data.model.Track} with the postal code '89075' from the {@link com.couchbase.lite.CouchbaseLite}
     * 6. Deletes the {@link de.thu.tpro.android4bikes.data.model.Track} 'THU' in the {@link com.google.firebase.firestore.FirebaseFirestore} and {@link com.couchbase.lite.CouchbaseLite} database
     * 7. Reads all official exemplars of the class {@link de.thu.tpro.android4bikes.data.model.Track} with the postal code '89075' from the {@link com.google.firebase.firestore.FirebaseFirestore} and stores them to the {@link com.couchbase.lite.CouchbaseLite} database
     * 8. Reads all stored {@link de.thu.tpro.android4bikes.data.model.Track} with the postal code '89075' from the {@link com.couchbase.lite.CouchbaseLite}
     * <p>
     * Requirements for passing this test:
     * -the {@link de.thu.tpro.android4bikes.data.model.Track} 'THU' should first be in the list of read {@link de.thu.tpro.android4bikes.data.model.Track}s
     * -after the deletion the {@link de.thu.tpro.android4bikes.data.model.Track} shouldn't be in the list of read {@link de.thu.tpro.android4bikes.data.model.Track}s anymore
     */
    @Test
    public void deleteTrackFromFireStoreAndLocalDB() {
        //Generate Track THU (postal code 89075)
        Track track_THU = generateTrack();

        //Delete track from FireStore and local database if it is already existing
        firebaseConnection.deleteTrackFromFireStoreAndLocalDB(track_THU.getFirebaseID());
        //wait a few seconds because of the asynchronous process of deleting data to FireBase
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Store the track to FireStore and local database
        firebaseConnection.storeTrackToFireStoreAndLocalDB(track_THU);
        //wait a few seconds because of the asynchronous process of storing data to FireBase
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //now the track has to be in the list:
        List<Track> list_read_tracks_with_postcode_89075 = couchDBHelper.readTracks(track_THU.getPostcode());
        assertTrue(list_read_tracks_with_postcode_89075.contains(track_THU));

        //now delete the track from firstroe and the local database
        firebaseConnection.deleteTrackFromFireStoreAndLocalDB(track_THU.getFirebaseID());
        //wait a few seconds because of the asynchronous process of storing data to FireBase
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Afterwards the shouldn't be anymore in the list
        list_read_tracks_with_postcode_89075 = couchDBHelper.readTracks(track_THU.getPostcode());
        assertFalse(list_read_tracks_with_postcode_89075.contains(track_THU));
    }

    /**
     * 1. Generates a {@link de.thu.tpro.android4bikes.data.model.HazardAlert} 'THU' that is located in Ulm
     * 2. Stores the same {@link de.thu.tpro.android4bikes.data.model.HazardAlert} 'THU' three times to the {@link com.google.firebase.firestore.FirebaseFirestore}
     * 3. Reads all official exemplars of the class {@link de.thu.tpro.android4bikes.data.model.HazardAlert} with the postal code '89075' from the {@link com.google.firebase.firestore.FirebaseFirestore} and stores them to the {@link com.couchbase.lite.CouchbaseLite}
     * 4. Reads all stored {@link de.thu.tpro.android4bikes.data.model.HazardAlert} with the postal code '89075' from the {@link com.couchbase.lite.CouchbaseLite}
     * <p>
     * Requirements for passing this test:
     * -the {@link de.thu.tpro.android4bikes.data.model.BikeRack} 'THU' should be in the list of read {@link de.thu.tpro.android4bikes.data.model.HazardAlert}s
     */
    @Test
    public void submitAndReadHazardAlerts() {
        //generate a bike rack
        HazardAlert hazardAlert_THU = generateHazardAlert();

        //store the same hazard alert three times in the FireStore. After the third occurrence
        //of this hazard alert in the FireStore it should be an official bhazard alert. Only
        //official hazard alerts can be read!!!
        for (int i = 0; i < 3; ++i) {
            firebaseConnection.submitHazardAlertToFireStore(hazardAlert_THU);
        }
        //wait a few seconds because of the asynchronous process of storing data to FireBase
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //read official bike racks from couch db and store them to the local db
        firebaseConnection.readHazardAlertsFromFireStoreAndStoreItToLocalDB(hazardAlert_THU.getPostcode());
        //wait a few seconds because of the asynchronous process of storing data to FireBase
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //read all bike racks from the local db with the postcode '89075'
        List<HazardAlert> hazardAlerts_with_postcode_89075 = couchDBHelper.readHazardAlerts(hazardAlert_THU.getPostcode());

        //the just stored bike rack has to be contained in the list of official bike racks
        assertTrue(hazardAlerts_with_postcode_89075.contains(hazardAlert_THU));
    }


    @Test
    public void updateToken() {
    }

    /**
     * generates a new instance of the class {@link de.thu.tpro.android4bikes.data.model.Profile} for test purposes
     *
     * @return instance of the class {@link de.thu.tpro.android4bikes.data.model.Profile}
     */
    private Profile createProfile() {
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new KmAchievement("First Mile", 1, 1, 1, 2));
        achievements.add(new KmAchievement("From Olympia to Corinth", 2, 40, 7, 119));

        Profile profile = new Profile("Kostas", "Kostidis", "00x15dxxx", 10, 250, achievements);
        return profile;
    }

    /**
     * generates a new instance of the class {@link de.thu.tpro.android4bikes.data.model.BikeRack} for test purposes
     *
     * @return instance of the class {@link de.thu.tpro.android4bikes.data.model.BikeRack}
     */
    private BikeRack generateTHUBikeRack() {
        //create new BikeRack
        BikeRack bikeRack_THU = new BikeRack(
                "pfo4eIrvzrI0m363KF0K", new Position(48.408880, 9.997507), "THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true, false
        );
        return bikeRack_THU;
    }

    /**
     * generates a new instance of the class Track for test purposes
     *
     * @return instance of a track
     */
    private Track generateTrack() {
        List<Position> positions = new ArrayList<>();
        positions.add(new Position(48.408880, 9.997507));
        positions.add(new Position(49.408880, 10.997507));
        positions.add(new Position(50.408880, 11.997507));
        Track track = new Track("nullacht15", new Rating(), "Heimweg", "Das ist meine super tolle Strecke",
                "siebenundvierzig11", 1585773516, 25,
                positions, new ArrayList<>(), true);
        return track;
    }

    /**
     * generates a new instance of the class HazardAlert for test purposes
     *
     * @return instance of a hazard alert
     */
    private HazardAlert generateHazardAlert() {
        HazardAlert hazardAlert_thu = new HazardAlert(
                HazardAlert.HazardType.GENERAL, new Position(48.408880, 9.997507), 120000, 5, "12345"
        );
        return hazardAlert_thu;
    }
}