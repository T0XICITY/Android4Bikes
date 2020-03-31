package de.thu.tpro.android4bikes.database;

import androidx.test.core.app.ApplicationProvider;

import org.junit.BeforeClass;
import org.junit.Test;

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
    public void saveAndReadPosition() {
        double pLatPut = 48.304493;
        double pLongPut = 9.836146;
        Position positionPut = new Position(pLongPut, pLatPut);

        couchdb.clearDB(couchdb.getDatabaseFromName(DatabaseNames.DATABASE_POSITION));

        couchDbHelper.addToUtilization(positionPut);

        /*List<Position> positions = couchDbHelper.getAllPositions();
        Position positionRead = positions.get(0);

        assertEquals(positionPut,positionRead);*/
    }
}