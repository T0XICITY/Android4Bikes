package de.thu.tpro.android4bikes.database;

import androidx.test.core.app.ApplicationProvider;

import com.couchbase.lite.Database;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
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
     * generates a new instance of the class BikeRack for test purposes
     *
     * @return
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