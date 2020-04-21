package de.thu.tpro.android4bikes.database;

import androidx.test.core.app.ApplicationProvider;

import com.couchbase.lite.Database;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.data.achievements.KmAchievement;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Rating;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDB.DatabaseNames;
import de.thu.tpro.android4bikes.util.GlobalContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CouchDbHelperTest {
    private static CouchDB couchdb;
    private static CouchDBHelper couchDbHelper;
    private static CouchDBHelper cdbWriteBuffer;
    private static CouchDBHelper cdbOwn;

    @BeforeClass
    public static void setUp() throws Exception {
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
        couchDbHelper = new CouchDBHelper();
        couchdb = CouchDB.getInstance();
        cdbWriteBuffer = new CouchDBHelper(CouchDBHelper.DBMode.WRITEBUFFER);
        cdbOwn = new CouchDBHelper(CouchDBHelper.DBMode.OWNDATA);
    }

    /**
     * 1. Generates a bike rack "THU" that is located in Ulm
     * 2. Stores the bike rack 'THU' to the db.
     * 3. Reads all stored bike racks with the postal code '89075' from the db.
     * <p>
     * Requirements for passing this test:
     * -after storing the new bike rack in the db there should be one more document in the db
     * -the bike rack 'THU' should be in the list of read bike racks
     */
    @Test
    public void storeBikeRack() {
        //Get database:
        Database db_bikeRack = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK);

        //get count of initially stored documents:
        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_bikeRack);

        //create new BikeRack
        BikeRack bikeRack_THU = this.generateTHUBikeRack();

        //store BikeRack in local database
        couchDbHelper.storeBikeRack(bikeRack_THU);

        //read new amount of stored documents
        long newNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_bikeRack);

        //after storing the bike rack there must be one bike rack more
        assertEquals(initialNumberOfDocuments + 1, newNumberOfDocuments);

        //read the just stored bike rack (THUBikeRack)
        List<BikeRack> bikeRacks_with_postcode_89075 = couchDbHelper.readBikeRacks();

        //the just stored bike rack must be in the list of the read bike racks
        assertTrue(bikeRacks_with_postcode_89075.contains(bikeRack_THU));

        couchDbHelper.deleteBikeRack(bikeRack_THU.getFirebaseID());
    }

    /**
     * 1. Clears the db (deletes all documents)
     * 2. Generates a bike rack 'THU' that is located in Ulm and stores it to the db.
     * 3. Deletes the bike rack 'THU' by using its firebase id.
     * 4. Reads all stored bike racks with the postal code '89075' from the db.
     *
     * Requirements for passing this test:
     * -after clearing the db 'bike racks' there should be no more documents in the db
     * -after inserting and deleting the bike rack 'THU' there should be no documents in the db
     * -the bike rack 'THU' should not be in the list of read bike racks
     */
    @Test
    public void deleteBikeRack() {
        //Get database:
        Database db_bikeRack = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK);

        //clear database
        couchdb.clearDB(db_bikeRack);

        //get number of documents
        long initialNumberOfDouments = couchdb.getNumberOfStoredDocuments(db_bikeRack);

        //initial number of documents must be 0
        assertEquals(0, initialNumberOfDouments);

        //create new BikeRack
        BikeRack bikeRack_THU = this.generateTHUBikeRack();

        //store BikeRack in local database
        couchDbHelper.storeBikeRack(bikeRack_THU);

        //delete just inserted bikeRack
        couchDbHelper.deleteBikeRack(bikeRack_THU);

        //read new amount of stored documents
        long newNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_bikeRack);

        //after deletion the bike rack there must be stored again the initial number of documents
        assertEquals(initialNumberOfDouments, newNumberOfDocuments);

        //read all bike racks with the postcode of the stored and deleted bike rack
        List<BikeRack> bikeRacks_with_postcode_89075 = couchDbHelper.readBikeRacks();

        //the just stored bike rack must not be in the list of the read bike racks
        assertFalse(bikeRacks_with_postcode_89075.contains(bikeRack_THU));
    }

    /**
     * 1. Generates a bike rack 'THU' that is located in Ulm
     * 2. Stores the bike rack 'THU' to the db.
     * 3. Reads all stored bike racks with the postal code '89075' from the db.
     *
     * Requirements for passing this test:
     * -the bike rack 'THU' should be in the list of read bike racks
     */
    @Test
    public void readBikeRack() {
        //Get database:
        Database db_bikeRack = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK);

        //clear database
        couchdb.clearDB(db_bikeRack);

        //generate new BikeRack
        BikeRack bikeRack_THU = this.generateTHUBikeRack();

        //store bikeRack in local DB
        couchDbHelper.storeBikeRack(bikeRack_THU);

        //read from
        List<BikeRack> bikeRacks_with_postcode_89075 = couchDbHelper.readBikeRacks();


        //the just stored bike rack must be in the list of the read bike racks
        assertTrue(bikeRacks_with_postcode_89075.contains(bikeRack_THU));


        couchDbHelper.deleteBikeRack(bikeRack_THU);
    }

    /**
     * 1. Generates a hazard alert 'THU' that is located in Ulm
     * 2. Stores the hazard alert 'THU' to the db.
     * 3. Reads all stored hazard alerts with the postal code '89075' from the db.
     * <p>
     * Requirements for passing this test:
     * -after storing the new hazard alert in the db there should be one more document in the db
     * -the hazard alert 'THU' should be in the list of read hazard alerts
     */
    @Test
    public void storeHazardAlert() {
        //Get database:
        Database db_hazardAlerts = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT);

        //get count of initially stored documents:
        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_hazardAlerts);

        //create new BikeRack
        HazardAlert hazardAlert_thu = this.generateHazardAlert();

        //store BikeRack in local database
        couchDbHelper.storeHazardAlerts(hazardAlert_thu);

        //read new amount of stored documents
        long newNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_hazardAlerts);

        //after storing the bike rack there must be one bike rack more
        assertEquals(initialNumberOfDocuments + 1, newNumberOfDocuments);

        //read the just stored bike rack (THUBikeRack)
        List<HazardAlert> hazardAlerts_with_postcode_89075 = couchDbHelper.readHazardAlerts();

        //the just stored bike rack must be in the list of the read bike racks
        assertTrue(hazardAlerts_with_postcode_89075.contains(hazardAlert_thu));

        couchDbHelper.deleteBikeRack(hazardAlert_thu.getFirebaseID());
    }

    /**
     * 1. Clears the db (deletes all documents)
     * 2. Generates a hazard alert 'THU' that is located in Ulm and stores it to the db.
     * 3. Deletes the hazard alert 'THU' by using its firebase id.
     * 4. Reads all stored hazard alerts with the postal code '89075' from the db.
     * <p>
     * Requirements for passing this test:
     * -after clearing the db 'hazard alerts' there should be no more documents in the db
     * -after inserting and deleting the hazard alert 'THU' there should be no documents in the db
     * -the hazard alert 'THU' should not be in the list of read bike racks
     */
    @Test
    public void deleteHazardAlert() {
        //Get database:
        Database db_hazardAlerts = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT);

        //clear database
        couchdb.clearDB(db_hazardAlerts);

        //get number of documents
        long initialNumberOfDouments = couchdb.getNumberOfStoredDocuments(db_hazardAlerts);

        //initial number of documents must be 0
        assertEquals(0, initialNumberOfDouments);

        //create new BikeRack
        HazardAlert hazardAlert_THU = this.generateHazardAlert();

        //store BikeRack in local database
        couchDbHelper.storeHazardAlerts(hazardAlert_THU);

        //delete just inserted hazard alert
        couchDbHelper.deleteHazardAlert(hazardAlert_THU);

        //read new amount of stored documents
        long newNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_hazardAlerts);

        //after deletion the hazard alert there must be stored again the initial number of documents
        assertEquals(initialNumberOfDouments, newNumberOfDocuments);

        //read all hazard alerts with the postcode of the stored and deleted bike rack
        List<HazardAlert> hazardAlerts_with_postcode_89075 = couchDbHelper.readHazardAlerts();

        //the just stored bike rack must not be in the list of the read bike racks
        assertFalse(hazardAlerts_with_postcode_89075.contains(hazardAlert_THU));
    }


    /**
     * 1. Generates a hazard alert 'THU' that is located in Ulm
     * 2. Stores the hazard alert 'THU' to the db.
     * 3. Reads all stored  hazard alert with the postal code '89075' from the db.
     * <p>
     * Requirements for passing this test:
     * -the hazard alert 'THU' should be in the list of read bike racks
     */
    @Test
    public void readHazardAlertRack() {
        //Get database:
        Database db_hazardAlerts = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT);

        //clear database
        couchdb.clearDB(db_hazardAlerts);

        //generate new  hazard alert
        HazardAlert hazardAlert_THU = this.generateHazardAlert();

        //store  hazard alert in local DB
        couchDbHelper.storeHazardAlerts(hazardAlert_THU);

        //read from
        List<HazardAlert> bikeRacks_with_postcode_89075 = couchDbHelper.readHazardAlerts();


        //the just stored hazard alert must be in the list of the read bike racks
        assertTrue(bikeRacks_with_postcode_89075.contains(hazardAlert_THU));


        couchDbHelper.deleteHazardAlert(hazardAlert_THU);
    }



    @Test
    public void storeProfile(){
        Database db_profile = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE);

        Profile profile = createProfile();

        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_profile);

        couchDbHelper.storeProfile(profile);

        long newNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_profile);

        assertEquals(initialNumberOfDocuments + 1, newNumberOfDocuments);

        Profile readProfile = couchDbHelper.readProfile(profile.getGoogleID());

        assertEquals(profile,readProfile);

        couchDbHelper.deleteProfile(profile);
    }

    @Test
    public void readProfile(){
        Database db_profile = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE);

        couchdb.clearDB(db_profile);

        Profile profile = createProfile();

        couchDbHelper.storeProfile(profile);

        Profile readProfile = couchDbHelper.readProfile(profile.getGoogleID());

        assertEquals(profile,readProfile);

        couchDbHelper.deleteProfile(profile);
    }

    @Test
    public void updateProfile(){
        Database db_profile = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE);

        couchdb.clearDB(db_profile);

        Profile profile = createProfile();

        Profile profileUpdate = createDifferentProfile();

        couchDbHelper.storeProfile(profile);

        Profile readOldProfile = couchDbHelper.readProfile(profile.getGoogleID());

        couchDbHelper.updateProfile(profileUpdate);

        Profile readUpdatedProfile = couchDbHelper.readProfile(profile.getGoogleID());

        assertEquals(profile,readOldProfile);
        assertEquals(profileUpdate,readUpdatedProfile);

        assertEquals(profile.getFamilyName(),readUpdatedProfile.getFamilyName());
        assertEquals(profile.getFirstName(),readUpdatedProfile.getFirstName());
        assertEquals(profile.getGoogleID(),readUpdatedProfile.getGoogleID());
        assertEquals(profile.getAchievements(),readUpdatedProfile.getAchievements());
        assertNotEquals(profile.getColor(),readUpdatedProfile.getColor());
        assertNotEquals(profile.getOverallDistance(),readUpdatedProfile.getOverallDistance());

        couchDbHelper.deleteProfile(profile.getGoogleID());
    }

    @Test
    public void deleteProfile(){
        Database db_profile = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE);

        couchdb.clearDB(db_profile);

        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_profile);

        assertEquals(0, initialNumberOfDocuments);

        Profile profile = createProfile();

        couchDbHelper.storeProfile(profile);

        assertEquals(initialNumberOfDocuments+1,couchdb.getNumberOfStoredDocuments(db_profile));

        couchDbHelper.deleteProfile(profile);

        long newNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_profile);

        assertEquals(initialNumberOfDocuments, newNumberOfDocuments);

        Profile readProfile = couchDbHelper.readProfile(profile.getGoogleID());

        assertNull(readProfile);
    }

    @Test
    public void storeTrack(){
        Database db_track = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_TRACK);

        Track track = generateTrack();

        String postcode = "89075";

        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_track);

        couchDbHelper.storeTrack(track);

        long newNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_track);

        assertEquals(initialNumberOfDocuments + 1, newNumberOfDocuments);

        List<Track> readTracks = couchDbHelper.readTracks();

        assertTrue(readTracks.contains(track));

        couchDbHelper.deleteTrack(track.getFirebaseID());
    }

    @Test
    public void readTrack(){
        Database db_track = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_TRACK);

        couchdb.clearDB(db_track);

        Track track = generateTrack();

        couchDbHelper.storeTrack(track);

        List<Track> readTracks = couchDbHelper.readTracks();

        assertTrue(readTracks.contains(track));

        couchDbHelper.deleteTrack(track.getFirebaseID());
    }

    @Test
    public void deleteTrack(){
        Database db_track = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_TRACK);

        couchdb.clearDB(db_track);

        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_track);

        assertEquals(0, initialNumberOfDocuments);

        Track track = generateTrack();

        String postcode = "89075";

        couchDbHelper.storeTrack(track);

        assertEquals(initialNumberOfDocuments+1,couchdb.getNumberOfStoredDocuments(db_track));

        couchDbHelper.deleteTrack(track.getFirebaseID());

        long newNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_track);

        assertEquals(initialNumberOfDocuments, newNumberOfDocuments);

        List<Track> readTracks = couchDbHelper.readTracks();

        assertTrue(readTracks.isEmpty());
    }

    @Test
    public void addUtilisation(){
        Database db_position = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_POSITION);

        couchdb.clearDB(db_position);

        Position position;

        double k = 0;

        for (int i = 0; i < 99; i++) {

            position = new Position(50.999999 + k, 10.999999 + k);

            k = k + 0.000001;

            couchDbHelper.addToUtilization(position);

        }


        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_position);

        assertEquals(49,initialNumberOfDocuments);

        position = new Position(50.999999 + k, 10.999999 + k);

        couchDbHelper.addToUtilization(position);
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long newNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_position);

        assertEquals(0,newNumberOfDocuments);
    }

    @Test
    public void resetUtilisation(){
        Database db_position = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_POSITION);

        couchdb.clearDB(db_position);

        Position position = new Position(48.408880, 9.997507);

        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_position);

        assertEquals(0, initialNumberOfDocuments);

        couchDbHelper.addToUtilization(position);

        assertEquals(initialNumberOfDocuments+1,couchdb.getNumberOfStoredDocuments(db_position));

        couchDbHelper.resetUtilization();

        long newNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_position);

        assertEquals(initialNumberOfDocuments, newNumberOfDocuments);
    }

    @Test
    public void storeTrackWriteBuffer(){
        Database db_track = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_TRACK);

        couchdb.clearDB(db_track);

        Track track = generateTrack();

        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_track);

        cdbWriteBuffer.storeTrack(track);

        long newNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_track);

        assertEquals(initialNumberOfDocuments + 1, newNumberOfDocuments);

        List<Track> readTracks = cdbWriteBuffer.readTracks();

        assertTrue(readTracks.contains(track));

        cdbWriteBuffer.deleteTrack(track.getFirebaseID());
    }

    @Test
    public void storeTrackOwn(){
        Database db_track_own = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_TRACK);

        couchdb.clearDB(db_track_own);

        Track track_Own = generateTrack();

        long initialNumberOfDocuments_own = couchdb.getNumberOfStoredDocuments(db_track_own);

        cdbOwn.storeTrack(track_Own);

        long newNumberOfDocuments_own = couchdb.getNumberOfStoredDocuments(db_track_own);

        assertEquals(initialNumberOfDocuments_own + 1, newNumberOfDocuments_own);

        List<Track> readTracks_own = cdbOwn.readTracks();

        assertTrue(readTracks_own.contains(track_Own));

        cdbOwn.deleteTrack(track_Own.getFirebaseID());
    }

    @Test
    public void trackTestInterference(){
        Database db_track_WB = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_TRACK);
        Database db_track = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_TRACK);
        Database db_track_own = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_TRACK);


        couchdb.clearDB(db_track);
        couchdb.clearDB(db_track_WB);
        couchdb.clearDB(db_track_own);

        Track track_WB = generateTrack();
        Track track_Normal = generateDifferentTrack("Normal");
        Track track_Own = generateDifferentTrack("OWN");


        long initialNumberOfDocuments_WB = couchdb.getNumberOfStoredDocuments(db_track_WB);
        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_track);
        long initialNumberOfDocuments_own = couchdb.getNumberOfStoredDocuments(db_track_own);

        cdbWriteBuffer.storeTrack(track_WB);
        couchDbHelper.storeTrack(track_Normal);
        cdbOwn.storeTrack(track_Own);

        long newNumberOfDocuments_WB = couchdb.getNumberOfStoredDocuments(db_track_WB);
        long newNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_track);
        long newNumberOfDocuments_own = couchdb.getNumberOfStoredDocuments(db_track_own);

        assertEquals(initialNumberOfDocuments_WB + 1, newNumberOfDocuments_WB);
        assertEquals(initialNumberOfDocuments + 1, newNumberOfDocuments);
        assertEquals(initialNumberOfDocuments_own + 1, newNumberOfDocuments_own);

        List<Track> readTracks_WB = cdbWriteBuffer.readTracks();
        List<Track> readTracks_Normal = couchDbHelper.readTracks();
        List<Track> readTracks_own = cdbOwn.readTracks();

        assertTrue(readTracks_Normal.contains(track_Normal));
        assertFalse(readTracks_Normal.contains(track_WB));
        assertFalse(readTracks_Normal.contains(track_Own));

        assertTrue(readTracks_WB.contains(track_WB));
        assertFalse(readTracks_WB.contains(track_Normal));
        assertFalse(readTracks_WB.contains(track_Own));

        assertTrue(readTracks_own.contains(track_Own));
        assertFalse(readTracks_own.contains(track_WB));
        assertFalse(readTracks_own.contains(track_Normal));

        cdbWriteBuffer.deleteTrack(track_WB.getFirebaseID());
        couchDbHelper.deleteTrack(track_WB.getFirebaseID());
        cdbOwn.deleteTrack(track_Own.getFirebaseID());
    }


    /**
     * generates a new instance of the class BikeRack for test purposes
     *
     * @return instance of a bike rack
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
     * generates a new instance of the class HazardAlert for test purposes
     *
     * @return instance of a hazard alert
     */
    private HazardAlert generateHazardAlert() {
        HazardAlert hazardAlert_thu = new HazardAlert(
                HazardAlert.HazardType.GENERAL, new Position(48.408880, 9.997507), 120000, 5, "12345", true
        );
        return hazardAlert_thu;
    }

    /**
     * generates a new instance of the class Track for test purposes
     * @return instance of a track
     * */
    private Track generateTrack(){
        List<Position> positions = new ArrayList<>();
        positions.add(new Position(48.408880, 9.997507));
        Track track = new Track("nullacht15",new Rating(),"Heimweg","Das ist meine super tolle Strecke",
                "siebenundvierzig11",1585773516,25,
                positions,new ArrayList<>(),true);
        return track;
    }

    private Track generateDifferentTrack(String name){
        Track track = generateTrack();
        track.setDistance_km(100);
        track.setName(name);
        track.setDescription("Das ist schön. Das ist wunderschön!");
        return track;
    }

    /**
     * generates a new instance of the class {@link de.thu.tpro.android4bikes.data.model.Profile} for test purposes
     * */
    private Profile createProfile() {
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new KmAchievement("First Mile", 1, 1, 1, 2));
        achievements.add(new KmAchievement("From Olympia to Corinth", 2, 40, 7, 119));

        return new Profile("Kostas", "Kostidis", "00x15dxxx", 10, 250, achievements);
    }

    /**
     * generates a new different instance of the class {@link de.thu.tpro.android4bikes.data.model.Profile} for test purposes
     * */
    private Profile createDifferentProfile() {
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new KmAchievement("First Mile", 1, 1, 1, 2));
        achievements.add(new KmAchievement("From Olympia to Corinth", 2, 40, 7, 119));

        return new Profile("Kostas", "Kostidis", "00x15dxxx", 666, 1000, achievements);
    }

}