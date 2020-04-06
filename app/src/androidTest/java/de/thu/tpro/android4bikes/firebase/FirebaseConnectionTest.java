package de.thu.tpro.android4bikes.firebase;

import androidx.test.core.app.ApplicationProvider;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.achievements.KmAchievement;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.database.CouchDB;
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.util.GlobalContext;

import static org.junit.Assert.assertEquals;
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


    @BeforeClass
    public static void setUp() {
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
        firebaseConnection = FirebaseConnection.getInstance();
        couchDBHelper = new CouchDBHelper();
        couchDB = CouchDB.getInstance();
    }

    @Test
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


        //TODO: Remove comment
        //firebaseConnection.deleteProfileFromFireStoreAndLocalDB(profile_kostas.getGoogleID());
    }

    @Test
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

    @Test
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
        profile_kostas_updated.setAchievements(achievements);

        firebaseConnection.updateProfileInFireStoreAndLocalDB(profile_kostas_updated);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        read_profile = couchDBHelper.readProfile(profile_kostas.getGoogleID());

        assertEquals(profile_kostas_updated,read_profile);
    }

    @Test
    public void deleteProfile(){
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
     * -the {@link de.thu.tpro.android4bikes.data.model.BikeRack} 'THU' should be in the list of read {@link de.thu.tpro.android4bikes.data.model.BikeRack}
     */
    @Test
    public void storeBikeRackInFireStore() {
        //generate a bike rack
        BikeRack bikeRack_THU = generateTHUBikeRack();

        //store the same bike rack three times in the FireStore. After the third occurence
        //of this bike rack in the FireStore it should be an official bike rack. Only
        //official bike racks can be read!!!
        for (int i = 0; i < 3; ++i) {
            firebaseConnection.submitBikeRackToFireStoreAndLocalDB(bikeRack_THU);
        }

        //wait a few seconds because of the asynchronous process of storing data to FireBase
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //read official bike racks from couch db and store them to the local db
        firebaseConnection.readBikeRacksFromFireStoreAndStoreItToLocalDB(bikeRack_THU.getPostcode());

        //read all bike racks from the local db with the postcode '89075'
        List<BikeRack> bikeRacks_with_postcode_89075 = couchDBHelper.readBikeRacks(bikeRack_THU.getPostcode());

        //the just stored bike rack has to be contained in the list of official bike racks
        assertTrue(bikeRacks_with_postcode_89075.contains(bikeRack_THU));
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
                "pfo4eIrvzrI0m363KF0K", new Position(9.997507, 48.408880), "THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true, false
        );
        return bikeRack_THU;
    }
}