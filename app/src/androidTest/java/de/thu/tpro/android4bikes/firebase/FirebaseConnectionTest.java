package de.thu.tpro.android4bikes.firebase;

import org.junit.Test;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.Position;

import static org.junit.Assert.*;

public class FirebaseConnectionTest {

    @Test
    public void addProfileToFirestore() {
    }

    @Test
    public void storeBikeRackInFireStoreAndLocalDB() {
        BikeRack bikeRack_THU = new BikeRack(
          "", new Position(9.997507,48.408880),"THUBikeRack", BikeRack.ConstantsCapacity.SMALL,
                false, true,false
        );
        FirebaseConnection.storeBikeRackInFireStoreAndLocalDB(bikeRack_THU);
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