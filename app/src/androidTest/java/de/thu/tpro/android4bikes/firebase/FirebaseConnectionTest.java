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
import de.thu.tpro.android4bikes.database.CouchDBHelper;
import de.thu.tpro.android4bikes.util.GlobalContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FirebaseConnectionTest {
    private static FirebaseConnection firebaseConnection;
    private static CouchDBHelper couchDBHelper;


    @BeforeClass
    public static void setUp() {
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
        firebaseConnection = FirebaseConnection.getInstance();
        couchDBHelper = new CouchDBHelper();

    }

    @Test
    public void storeProfileToFireStoreAndLocalDB() {
        Profile profile_kostas = this.createProfile();

        firebaseConnection.storeProfileToFireStoreAndLocalDB(profile_kostas);
        //wait a few seconds
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Profile read_profile = couchDBHelper.readProfile(profile_kostas.getGoogleID());
        assertEquals(profile_kostas, read_profile);
    }

    @Test
    public void storeBikeRackInFireStoreAndLocalDB() {
        BikeRack bikeRack_THU = new BikeRack(
                "pfo4eIrvzrI0m363KF0K", new Position(9.997507, 48.408880), "THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true, false
        );

        firebaseConnection.submitBikeRackToFireStoreAndLocalDB(bikeRack_THU);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<BikeRack> bikeRacks_with_postcode_89075 = couchDBHelper.readBikeRacks(bikeRack_THU.getFirebaseID());
        assertTrue(bikeRacks_with_postcode_89075.contains(bikeRack_THU));
    }

    @Test
    public void readOfficialBikeRackFromFireStore() {
        firebaseConnection.readBikeRacksFromFireStoreAndStoreItToLocalDB("89075");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateToken() {
    }

    private Profile createProfile() {
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new KmAchievement("First Mile", 1, 1, 1, 2));
        achievements.add(new KmAchievement("From Olympia to Corinth", 2, 40, 7, 119));

        Profile profile = new Profile("Kostas", "Kostidis", "00x15dxxx", 10, 250, achievements);
        return profile;
    }
}