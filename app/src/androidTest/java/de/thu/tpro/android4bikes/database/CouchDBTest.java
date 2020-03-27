package de.thu.tpro.android4bikes.database;

import androidx.test.core.app.ApplicationProvider;

import com.couchbase.lite.Database;
import com.couchbase.lite.MutableDocument;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.thu.tpro.android4bikes.util.GlobalContext;

import static de.thu.tpro.android4bikes.database.CouchDB.*;
import static org.junit.Assert.*;

public class CouchDBTest {
    private static CouchDB couchdb;

    @BeforeClass
    public static void setUp(){
        //Context simulieren
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
        couchdb = CouchDB.getInstance();
    }

    @Test
    public void getInstance() {
        assertEquals(CouchDB.class, couchdb.getClass());
        assertNotEquals(null, couchdb.getDatabaseFromName(DatabaseNames.DATABASE_ACHIEVEMENT));
        assertNotEquals(null, couchdb.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK));
        assertNotEquals(null, couchdb.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT));
        assertNotEquals(null, couchdb.getDatabaseFromName(DatabaseNames.DATABASE_POSITION));
        assertNotEquals(null, couchdb.getDatabaseFromName(DatabaseNames.DATABASE_RATING));
        assertNotEquals(null, couchdb.getDatabaseFromName(DatabaseNames.DATABASE_TRACK));
    }

    @Test
    public void saveAndReadMutableDocumentFromDatabaseByID(){
        Database database_achievements = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_ACHIEVEMENT);

        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(database_achievements);

        //create new mutable (modifiable) document
        MutableDocument savedDocument = new MutableDocument()
                .setFloat("version", 2.7F)
                .setString("type", "SDK");
        couchdb.saveMutableDocumentToDatabase(database_achievements,savedDocument);
        MutableDocument readMutableDoc = couchdb.readDocumentByID(database_achievements, savedDocument.getId());

        //is the read document the same as the saved document?
        assertEquals(savedDocument, readMutableDoc);

        //is there one more document in the database
        assertEquals(initialNumberOfDocuments+1, couchdb.getNumberOfStoredDocuments(database_achievements)); //there

        couchdb.deleteDocumentByID(database_achievements, savedDocument.getId()); //Remove added document from db
    }

    @Test
    public void deleteDocumentByID(){
        Database database_achievements = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_ACHIEVEMENT);
        long initialNumberOfDocuments = couchdb.getNumberOfStoredDocuments(database_achievements);

        MutableDocument mutableDoc = new MutableDocument()
                .setFloat("version", 2.0F)
                .setString("type", "SDK");
        couchdb.saveMutableDocumentToDatabase(database_achievements,mutableDoc);
        couchdb.deleteDocumentByID(database_achievements,mutableDoc.getId());

        assertEquals(initialNumberOfDocuments, couchdb.getNumberOfStoredDocuments(database_achievements));
    }

    @Test
    public void clearDB() {
        Database database_achievements = couchdb.getDatabaseFromName(DatabaseNames.DATABASE_ACHIEVEMENT);
        for(int i=0; i<10; ++i){
            MutableDocument mutableDoc = new MutableDocument()
                    .setFloat("version", 2.0F)
                    .setString("type", "SDK");
            couchdb.saveMutableDocumentToDatabase(database_achievements,mutableDoc);
        }
        couchdb.clearDB(database_achievements);
        assertEquals(0, couchdb.getNumberOfStoredDocuments(database_achievements));
    }

    @AfterClass
    public static void after(){
        couchdb.closeDBConnection(couchdb.getDatabaseFromName(DatabaseNames.DATABASE_ACHIEVEMENT));
        couchdb.closeDBConnection(couchdb.getDatabaseFromName(DatabaseNames.DATABASE_BIKERACK));
        couchdb.closeDBConnection(couchdb.getDatabaseFromName(DatabaseNames.DATABASE_HAZARD_ALERT));
        couchdb.closeDBConnection(couchdb.getDatabaseFromName(DatabaseNames.DATABASE_POSITION));
        couchdb.closeDBConnection(couchdb.getDatabaseFromName(DatabaseNames.DATABASE_PROFILE));
        couchdb.closeDBConnection(couchdb.getDatabaseFromName(DatabaseNames.DATABASE_RATING));
        couchdb.closeDBConnection(couchdb.getDatabaseFromName(DatabaseNames.DATABASE_TRACK));
    }
}