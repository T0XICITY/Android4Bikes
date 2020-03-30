package de.thu.tpro.android4bikes.firebase;

import androidx.test.core.app.ApplicationProvider;

import org.junit.BeforeClass;
import org.junit.Test;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.util.GlobalContext;

import static org.junit.Assert.*;

public class FirebaseConnectionTest {
    private static FirebaseConnection firebaseConnection;
    @BeforeClass
    public static void setUp(){
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
        firebaseConnection = FirebaseConnection.getInstance();
    }

    @Test
    public void addProfileToFirestore() {
    }

    @Test
    public void storeBikeRackInFireStoreAndLocalDB() {

        BikeRack bikeRack_THU = new BikeRack(
                  "pfo4eIrvzrI0m363KF0K", new Position(9.997507,48.408880),"THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true,false
        );
        firebaseConnection.storeBikeRackInFireStoreAndLocalDB(bikeRack_THU);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readOfficialBikeRackFromFireStore() {
        firebaseConnection.readOfficialBikeRackFromFireStoreAndStoreItToLocalDB("89075");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void deleteBikeRackFromFireStoreAndLocalDB() {
        BikeRack bikeRack_THU = new BikeRack(
                "pfo4eIrvzrI0m363KF0K", new Position(9.997507,48.408880),"THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true,false
        );
        firebaseConnection.deleteBikeRackFromFireStoreAndLocalDB(bikeRack_THU);
    }

    @Test
    public void updateBikeRackInFireStoreAndLocalDB() {
        BikeRack bikeRack_THU = new BikeRack(
                "pfo4eIrvzrI0m363KF0K", new Position(9.997507,48.408880),"THUBikeRack", BikeRack.ConstantsCapacity.MEDIUM,
                true, true,false
        );
        firebaseConnection.updateBikeRackInFireStoreAndLocalDB(bikeRack_THU);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateToken() {
    }
}