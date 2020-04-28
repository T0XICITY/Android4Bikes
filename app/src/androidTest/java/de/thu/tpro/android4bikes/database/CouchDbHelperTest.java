package de.thu.tpro.android4bikes.database;

import androidx.test.core.app.ApplicationProvider;

import com.couchbase.lite.Database;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.database.CouchDB.DatabaseNames;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.util.TestObjectsGenerator;

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
        Database db_bikeRack_normal = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK);

        //get count of initially stored documents:
        long initialNumberOfDocuments_normal = couchdb.getNumberOfStoredDocuments(db_bikeRack_normal);

        //create new BikeRack
        BikeRack bikeRack_THU_normal = TestObjectsGenerator.generateTHUBikeRack();

        //store BikeRack in local database
        couchDbHelper.storeBikeRack(bikeRack_THU_normal);

        //read new amount of stored documents
        long newNumberOfDocuments_normal = couchdb.getNumberOfStoredDocuments(db_bikeRack_normal);

        //after storing the bike rack there must be one bike rack more
        assertEquals(initialNumberOfDocuments_normal + 1, newNumberOfDocuments_normal);

        //read the just stored bike rack (THUBikeRack)
        List<BikeRack> bikeRacks_with_postcode_89075_normal = couchDbHelper.readBikeRacks();

        //the just stored bike rack must be in the list of the read bike racks
        assertTrue(bikeRacks_with_postcode_89075_normal.contains(bikeRack_THU_normal));

        couchDbHelper.deleteBikeRack(bikeRack_THU_normal.getFirebaseID());
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
        BikeRack bikeRack_THU = TestObjectsGenerator.generateTHUBikeRack();

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
        BikeRack bikeRack_THU = TestObjectsGenerator.generateTHUBikeRack();

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
        HazardAlert hazardAlert_thu = TestObjectsGenerator.generateHazardAlert();

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
        HazardAlert hazardAlert_THU = TestObjectsGenerator.generateHazardAlert();

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
        HazardAlert hazardAlert_THU = TestObjectsGenerator.generateHazardAlert();

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

        Profile profile = TestObjectsGenerator.createProfile();

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

        Profile profile = TestObjectsGenerator.createProfile();

        couchDbHelper.storeProfile(profile);

        Profile readProfile = couchDbHelper.readProfile(profile.getGoogleID());

        assertEquals(profile,readProfile);

        couchDbHelper.deleteProfile(profile);
    }

    @Test
    public void updateProfile(){
        Database db_profile = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE);

        couchdb.clearDB(db_profile);

        Profile profile = TestObjectsGenerator.createProfile();

        Profile profileUpdate = TestObjectsGenerator.createDifferentProfile();

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

        Profile profile = TestObjectsGenerator.createProfile();

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

        Track track = TestObjectsGenerator.generateTrack();

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

        Track track = TestObjectsGenerator.generateTrack();

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

        Track track = TestObjectsGenerator.generateTrack();

        String postcode = "89075";

        couchDbHelper.storeTrack(track);

        assertEquals(initialNumberOfDocuments+1,couchdb.getNumberOfStoredDocuments(db_track));

        couchDbHelper.deleteTrack(track.getFirebaseID());

        long newNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_track);

        assertEquals(initialNumberOfDocuments, newNumberOfDocuments);

        List<Track> readTracks = couchDbHelper.readTracks();

        assertTrue(readTracks.isEmpty());
    }

    /*
    @Test
    public void addUtilisation(){
        Database db_position = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_POSITION);
        couchdb.clearDB(db_position);
        Position position;
        double k = 0;
        for (int i = 0; i < 99; i++) {
            position = new Position(50.999999 + k, 10.999999 + k);
            k = k + 0.000001;
            couchDbHelper.addToUtilization(position);
        }
        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_position);
        assertEquals(99,initialNumberOfDocuments);
        couchdb.clearDB(db_position);
    }*/

    @Test
    public void resetUtilisation(){
        Database db_position = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_POSITION);

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

        Track track = TestObjectsGenerator.generateTrack();

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

        Track track_Own = TestObjectsGenerator.generateTrack();

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

        Track track_WB = TestObjectsGenerator.generateTrack();
        Track track_Normal = TestObjectsGenerator.generateDifferentTrack("Normal");
        Track track_Own = TestObjectsGenerator.generateDifferentTrack("OWN");

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

    @Test
    public void storeBikeRackWriteBuffer() {
        //Get database:
        Database db_bikeRack_WB = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_BIKERACK);

        couchdb.clearDB(db_bikeRack_WB);

        //get count of initially stored documents:
        long initialNumberOfDocuments_WB = couchdb.getNumberOfStoredDocuments(db_bikeRack_WB);

        //create new BikeRack
        BikeRack bikeRack_THU_WB = TestObjectsGenerator.generateTHUBikeRack();

        //store BikeRack in local database
        cdbWriteBuffer.storeBikeRack(bikeRack_THU_WB);

        //read new amount of stored documents
        long newNumberOfDocuments_WB = couchdb.getNumberOfStoredDocuments(db_bikeRack_WB);

        //after storing the bike rack there must be one bike rack more
        assertEquals(initialNumberOfDocuments_WB + 1, newNumberOfDocuments_WB);

        //read the just stored bike rack (THUBikeRack)
        List<BikeRack> bikeRacks_with_postcode_89075_WB = cdbWriteBuffer.readBikeRacks();

        //the just stored bike rack must be in the list of the read bike racks
        assertTrue(bikeRacks_with_postcode_89075_WB.contains(bikeRack_THU_WB));

        cdbWriteBuffer.deleteBikeRack(bikeRack_THU_WB.getFirebaseID());
    }

    @Test
    public void storeBikeRackOwn() {
        //Get database:
        Database db_bikeRack_own = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_BIKERACK);

        couchdb.clearDB(db_bikeRack_own);

        //get count of initially stored documents:
        long initialNumberOfDocuments_OWN = couchdb.getNumberOfStoredDocuments(db_bikeRack_own);

        //create new BikeRack
        BikeRack bikeRack_THU_OWN = TestObjectsGenerator.generateTHUBikeRack();

        //store BikeRack in local database
        cdbOwn.storeBikeRack(bikeRack_THU_OWN);

        //read new amount of stored documents
        long newNumberOfDocuments_OWN = couchdb.getNumberOfStoredDocuments(db_bikeRack_own);

        //after storing the bike rack there must be one bike rack more
        assertEquals(initialNumberOfDocuments_OWN + 1, newNumberOfDocuments_OWN);

        //read the just stored bike rack (THUBikeRack)
        List<BikeRack> bikeRacks_with_postcode_89075_OWN = cdbOwn.readBikeRacks();

        //the just stored bike rack must be in the list of the read bike racks
        assertTrue(bikeRacks_with_postcode_89075_OWN.contains(bikeRack_THU_OWN));

        cdbOwn.deleteBikeRack(bikeRack_THU_OWN.getFirebaseID());
    }

    @Test
    public void testBikeRackInterference(){
        //Get database:
        Database db_bikeRack_own = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_OWNDATA_BIKERACK);
        Database db_bikeRack_WB = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_BIKERACK);
        Database db_bikeRack_normal = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK);

        couchdb.clearDB(db_bikeRack_own);
        couchdb.clearDB(db_bikeRack_WB);
        couchdb.clearDB(db_bikeRack_normal);

        //get count of initially stored documents:
        long initialNumberOfDocuments_OWN = couchdb.getNumberOfStoredDocuments(db_bikeRack_own);
        long initialNumberOfDocuments_WB = couchdb.getNumberOfStoredDocuments(db_bikeRack_WB);
        long initialNumberOfDocuments_normal = couchdb.getNumberOfStoredDocuments(db_bikeRack_normal);

        //create new BikeRack
        BikeRack bikeRack_THU_OWN = TestObjectsGenerator.generateTHUBikeRack();
        bikeRack_THU_OWN.setName("own");
        BikeRack bikeRack_THU_WB = TestObjectsGenerator.generateTHUBikeRack();
        bikeRack_THU_WB.setName("WB");
        BikeRack bikeRack_THU_normal = TestObjectsGenerator.generateTHUBikeRack();

        //store BikeRack in local database
        cdbOwn.storeBikeRack(bikeRack_THU_OWN);
        cdbWriteBuffer.storeBikeRack(bikeRack_THU_WB);
        couchDbHelper.storeBikeRack(bikeRack_THU_normal);

        //read new amount of stored documents
        long newNumberOfDocuments_OWN = couchdb.getNumberOfStoredDocuments(db_bikeRack_own);
        long newNumberOfDocuments_WB = couchdb.getNumberOfStoredDocuments(db_bikeRack_WB);
        long newNumberOfDocuments_normal = couchdb.getNumberOfStoredDocuments(db_bikeRack_normal);

        //after storing the bike rack there must be one bike rack more
        assertEquals(initialNumberOfDocuments_OWN + 1, newNumberOfDocuments_OWN);
        assertEquals(initialNumberOfDocuments_WB + 1, newNumberOfDocuments_WB);
        assertEquals(initialNumberOfDocuments_normal + 1, newNumberOfDocuments_normal);

        //read the just stored bike rack (THUBikeRack)
        List<BikeRack> bikeRacks_with_postcode_89075_OWN = cdbOwn.readBikeRacks();
        List<BikeRack> bikeRacks_with_postcode_89075_WB = cdbWriteBuffer.readBikeRacks();
        List<BikeRack> bikeRacks_with_postcode_89075_normal = couchDbHelper.readBikeRacks();

        //the just stored bike rack must be in the list of the read bike racks
        assertTrue(bikeRacks_with_postcode_89075_OWN.contains(bikeRack_THU_OWN));
        assertFalse(bikeRacks_with_postcode_89075_OWN.contains(bikeRack_THU_WB));
        assertFalse(bikeRacks_with_postcode_89075_OWN.contains(bikeRack_THU_normal));

        assertTrue(bikeRacks_with_postcode_89075_normal.contains(bikeRack_THU_normal));
        assertFalse(bikeRacks_with_postcode_89075_normal.contains(bikeRack_THU_WB));
        assertFalse(bikeRacks_with_postcode_89075_normal.contains(bikeRack_THU_OWN));

        assertTrue(bikeRacks_with_postcode_89075_WB.contains(bikeRack_THU_WB));
        assertFalse(bikeRacks_with_postcode_89075_WB.contains(bikeRack_THU_OWN));
        assertFalse(bikeRacks_with_postcode_89075_WB.contains(bikeRack_THU_normal));

        cdbOwn.deleteBikeRack(bikeRack_THU_OWN.getFirebaseID());
        cdbWriteBuffer.deleteBikeRack(bikeRack_THU_WB.getFirebaseID());
        couchDbHelper.deleteBikeRack(bikeRack_THU_normal.getFirebaseID());
    }

}