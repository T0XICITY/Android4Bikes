package de.thu.tpro.android4bikes.database;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public interface Android4BikesDatabaseHelper {
    List<BikeRack> readBikeRack(String postcode);

    void saveBikeRack(BikeRack bikeRack);

    void updateBikeRack(BikeRack bikeRack);

    void deleteBikeRack(BikeRack bikeRack);

    List<HazardAlert> readHazardAlert(String postcode);

    void saveHazardAlert(HazardAlert hazardAlert);

    void updateHazardAlert(HazardAlert hazardAlert);

    void deleteHazardAlert(HazardAlert hazardAlert);

    Profile readProfile(String firebaseAccountID);

    void saveProfile(Profile profile);

    void updateProfile(Profile profile);

    void deleteProfile(Profile profile);


    Track getTrack(long trackID);
}
