package de.thu.tpro.android4bikes.firebase;

import androidx.test.core.app.ApplicationProvider;

import org.junit.BeforeClass;
import org.junit.Test;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.util.GlobalContext;

import static org.junit.Assert.*;

public class FirebaseConnectionTest {

    @BeforeClass
    public static void setUp(){
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void addProfileToFirestore() {
    }

    @Test
    public void storeBikeRackInFireStoreAndLocalDB() {
        FirebaseConnection firebaseConnection = FirebaseConnection.getInstance();
        BikeRack bikeRack_THU = new BikeRack(
          "", new Position(9.997507,48.408880),"THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true,false
        );
        firebaseConnection.storeBikeRackInFireStoreAndLocalDB(bikeRack_THU);
    }

    @Test
    public void deleteBikeRackFromFireStoreAndLocalDB() {
    }

    @Test
    public void updateBikeRackInFireStoreAndLocalDB() {
    }

    @Test
    public void readOfficialBikeRackFromFireStore() {
    }

    @Test
    public void updateToken() {
    }
}