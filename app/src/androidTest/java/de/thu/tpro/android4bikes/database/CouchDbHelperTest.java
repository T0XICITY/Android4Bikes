package de.thu.tpro.android4bikes.database;

import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.database.CouchDB.DatabaseNames;
import de.thu.tpro.android4bikes.util.GlobalContext;

import static org.junit.Assert.*;

public class CouchDbHelperTest {
    private static CouchDB couchdb;
    private static CouchDbHelper couchDbHelper;

    @BeforeClass
    public static void setUp() throws Exception {
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
        couchDbHelper = new CouchDbHelper();
        couchdb = CouchDB.getInstance();
    }

    @Test
    public void saveAndReadPosition() {
        double pLatPut = 48.304493;
        double pLongPut = 9.836146;
        Position positionPut = new Position("NullAchtFunfzehn",pLongPut,pLatPut);

        couchdb.clearDB(couchdb.getDatabaseFromName(DatabaseNames.DATABASE_POSITION));

        couchDbHelper.savePosition(positionPut);

        List<Position> positions = couchDbHelper.getAllPositions();
        Position positionRead = positions.get(0);

        assertEquals(positionPut,positionRead);
    }
}