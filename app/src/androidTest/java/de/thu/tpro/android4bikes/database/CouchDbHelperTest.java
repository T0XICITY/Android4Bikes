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
        Database db_bikeRack = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK);
        couchdb.clearDB(db_bikeRack);

        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(db_bikeRack);
        BikeRack bikeRack_THU = new BikeRack(
                "pfo4eIrvzrI0m363KF0K", new Position(9.997507, 48.408880), "THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true, false
        );
        couchDbHelper.storeBikeRack(bikeRack_THU);
        long numberOfDocuments = couchdb.getNumberOfStoredDocuments(db_bikeRack);

        List<BikeRack> bikeRacks = couchDbHelper.readBikeRacks(bikeRack_THU.getPostcode());
        couchDbHelper.deleteBikeRack(bikeRacks.get(0).getFirebaseID());
    }
}