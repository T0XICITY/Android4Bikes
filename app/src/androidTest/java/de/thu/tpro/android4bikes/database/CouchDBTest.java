package de.thu.tpro.android4bikes.database;

import androidx.test.core.app.ApplicationProvider;

import com.couchbase.lite.Database;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.util.WorkManagerHelper;

import static de.thu.tpro.android4bikes.database.CouchDB.DatabaseNames;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CouchDBTest {
    private static CouchDB couchdb;

    @BeforeClass
    public static void setUp() {
        //Context simulieren
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
        couchdb = CouchDB.getInstance();
        WorkManagerHelper.stopUploadTaskWithWorkManager();
    }

    @AfterClass
    public static void after() {
        couchdb.closeDBConnection(couchdb.getDatabaseFromName(DatabaseNames.DATABASE_ACHIEVEMENT));
        couchdb.closeDBConnection(couchdb.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK));
        couchdb.closeDBConnection(couchdb.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT));
        couchdb.closeDBConnection(couchdb.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE));
        couchdb.closeDBConnection(couchdb.getDatabaseFromName(DatabaseNames.DATABASE_RATING));
        couchdb.closeDBConnection(couchdb.getDatabaseFromName(DatabaseNames.DATABASE_TRACK));
        WorkManagerHelper.stopUploadTaskWithWorkManager();

    }

    @Test
    public void getInstance() {
        assertEquals(CouchDB.class, couchdb.getClass());
        assertNotEquals(null, couchdb.getDatabaseFromName(DatabaseNames.DATABASE_ACHIEVEMENT));
        assertNotEquals(null, couchdb.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK));
        assertNotEquals(null, couchdb.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT));
        assertNotEquals(null, couchdb.getDatabaseFromName(DatabaseNames.DATABASE_RATING));
        assertNotEquals(null, couchdb.getDatabaseFromName(DatabaseNames.DATABASE_TRACK));
    }

    @Test
    public void saveAndReadMutableDocumentFromDatabaseByID() {
        Database database_achievements = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_ACHIEVEMENT);

        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(database_achievements);

        //create new mutable (modifiable) document
        MutableDocument savedDocument = new MutableDocument()
                .setFloat("version", 2.7F)
                .setString("type", "SDK");
        couchdb.saveMutableDocumentToDatabase(database_achievements, savedDocument);
        MutableDocument readMutableDoc = couchdb.readDocumentByID(database_achievements, savedDocument.getId());

        //is the read document the same as the saved document?
        assertEquals(savedDocument, readMutableDoc);

        //is there one more document in the database
        assertEquals(initialNumberOfDocuments + 1, couchdb.getNumberOfStoredDocuments(database_achievements)); //there

        couchdb.deleteDocumentByID(database_achievements, savedDocument.getId()); //Remove added document from db
    }

    @Test
    public void deleteDocumentByID() {
        Database database_achievements = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_ACHIEVEMENT);
        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(database_achievements);

        MutableDocument mutableDoc = new MutableDocument()
                .setFloat("version", 2.0F)
                .setString("type", "SDK");
        couchdb.saveMutableDocumentToDatabase(database_achievements, mutableDoc);
        couchdb.deleteDocumentByID(database_achievements, mutableDoc.getId());

        assertEquals(initialNumberOfDocuments, couchdb.getNumberOfStoredDocuments(database_achievements));
    }

    @Test
    public void clearDB() {
        Database database_achievements = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_ACHIEVEMENT);
        for (int i = 0; i < 10; ++i) {
            MutableDocument mutableDoc = new MutableDocument()
                    .setFloat("version", 2.0F)
                    .setString("type", "SDK");
            couchdb.saveMutableDocumentToDatabase(database_achievements, mutableDoc);
        }
        couchdb.clearDB(database_achievements);
        assertEquals(0, couchdb.getNumberOfStoredDocuments(database_achievements));
    }

    @Test
    public void clearBuffer() {
        Database db_buffer_hazardAlerts = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_HAZARD_ALERT);
        Database db_buffer_tracks = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_TRACK);
        Database db_buffer_bikeRacks = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_BIKERACK);
        Database db_buffer_profile = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_PROFILE);
        Database db_positions_buffer = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_WRITEBUFFER_POSITION);

        couchdb.clearDB(db_buffer_hazardAlerts);
        couchdb.clearDB(db_buffer_tracks);
        couchdb.clearDB(db_buffer_bikeRacks);
        couchdb.clearDB(db_buffer_profile);
        couchdb.clearDB(db_positions_buffer);
    }

    @Test
    public void queryDatabaseForRegularExpression() {
        Database database_bikeRacks = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK);
        couchdb.clearDB(database_bikeRacks);

        MutableDocument mutableDocument = new MutableDocument()
                .setString("name", "THUBikeRack")
                .setString("geoHash", "ADAC");
        couchdb.saveMutableDocumentToDatabase(database_bikeRacks, mutableDocument);

        String regexStart = ""; //start of the regular expression
        String regexEnd = "+.*"; //regular expression to match any character
        char[] adac = {'A', 'D', 'A', 'C'};

        //Will perform tests with following regular expressions:
        //A.+*
        //AD.+*
        //ADA.+*
        //ADAC.+*
        for (int i = 0; i < adac.length; ++i) {
            regexStart += adac[i];
            String regex = regexStart + regexEnd;
            ResultSet results = couchdb.queryDatabaseForRegularExpression(database_bikeRacks, "geoHash", regex);
            Map<String, Object> map_readMutable_Document = null;
            for (Result r : results) {
                map_readMutable_Document = r.toMap();
                map_readMutable_Document = (Map<String, Object>) map_readMutable_Document.get("bikerackdb");
            }
            assertEquals(mutableDocument.toMap(), map_readMutable_Document);
        }
    }

    @Test
    public void clearAllDatabasesPolski() {
        Database database_hazardAlerts = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT);
        couchdb.clearDB(database_hazardAlerts);
        long initialNumberOfDocouments = couchdb.getNumberOfStoredDocuments(database_hazardAlerts);
        assertEquals(0, initialNumberOfDocouments);

        for (int i = 0; i < 10; ++i) {
            MutableDocument mutableDocument = new MutableDocument()
                    .setFloat("version", i)
                    .setString("type", "SDK");

            couchdb.saveMutableDocumentToDatabase(database_hazardAlerts, mutableDocument);
        }
        long newNumberOfDocuments = couchdb.getNumberOfStoredDocuments(database_hazardAlerts);
        assertEquals(10, newNumberOfDocuments);

        couchdb.clearAllDatabases();
        assertEquals(0, initialNumberOfDocouments);
    }

}