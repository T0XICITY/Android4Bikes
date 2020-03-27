package de.thu.tpro.android4bikes.database;

import androidx.test.core.app.ApplicationProvider;

import org.junit.BeforeClass;
import org.junit.Test;

import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.util.GlobalContext;

import static org.junit.Assert.*;

public class CouchDbHelperTest {
    private static CouchDB couchdb;
    private CouchDbHelper couchDbHelper;

    @BeforeClass
    public void setUp() throws Exception {
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
        couchDbHelper = new CouchDbHelper();
    }

    @Test
    public void savePosition() {
        double pLat = 48.304493;
        double pLong = 9.836146;
        Position position = new Position("NullAchtFunfzehn",pLong,pLat);
        couchDbHelper.savePosition();
    }
}