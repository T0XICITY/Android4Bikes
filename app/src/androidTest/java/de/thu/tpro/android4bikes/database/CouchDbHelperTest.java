package de.thu.tpro.android4bikes.database;

import androidx.test.core.app.ApplicationProvider;

import com.couchbase.lite.Database;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.database.CouchDB.DatabaseNames;
import de.thu.tpro.android4bikes.util.GlobalContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CouchDbHelperTest {
    private static CouchDB couchdb;
    private static CouchDBHelper couchDbHelper;

    @BeforeClass
    public static void setUp() throws Exception {
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
        couchDbHelper = new CouchDBHelper();
        couchdb = CouchDB.getInstance();
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
        List<BikeRack> bikeRacks_with_postcode_89075 = couchDbHelper.readBikeRacks(bikeRack_THU.getPostcode());

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
        List<BikeRack> bikeRacks_with_postcode_89075 = couchDbHelper.readBikeRacks(bikeRack_THU.getPostcode());

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
        List<BikeRack> bikeRacks_with_postcode_89075 = couchDbHelper.readBikeRacks(bikeRack_THU.getPostcode());


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
        List<HazardAlert> hazardAlerts_with_postcode_89075 = couchDbHelper.readHazardAlerts(hazardAlert_thu.getPostcode());

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
        List<HazardAlert> hazardAlerts_with_postcode_89075 = couchDbHelper.readHazardAlerts(hazardAlert_THU.getPostcode());

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
        List<HazardAlert> bikeRacks_with_postcode_89075 = couchDbHelper.readHazardAlerts(hazardAlert_THU.getPostcode());


        //the just stored hazard alert must be in the list of the read bike racks
        assertTrue(bikeRacks_with_postcode_89075.contains(hazardAlert_THU));


        couchDbHelper.deleteHazardAlert(hazardAlert_THU);
    }

    /**
     * generates a new instance of the class BikeRack for test purposes
     *
     * @return instance of a bike rack
     */
    private BikeRack generateTHUBikeRack() {
        //create new BikeRack
        BikeRack bikeRack_THU = new BikeRack(
                "pfo4eIrvzrI0m363KF0K", new Position(9.997507, 48.408880), "THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
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
                HazardAlert.HazardType.GENERAL, new Position(9.997507, 48.408880), 120000, 5, "12345"
        );
        return hazardAlert_thu;
    }
}