package de.thu.tpro.android4bikes.database;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public interface Android4BikesDatabaseHelper {
    public List<BikeRack> readBikeRack(String postcode);
    public void saveBikeRack(BikeRack bikeRack);
    public void updateBikeRack(BikeRack bikeRack);
    public void deleteBikeRack(BikeRack bikeRack);

    public List<HazardAlert> readHazardAlert(String postcode);
    public void saveHazardAlert(HazardAlert hazardAlert);
    public void updateHazardAlert(HazardAlert hazardAlert);
    public void deleteHazardAlert(HazardAlert hazardAlert);

    public Profile readProfile(String firebaseAccountID);
    public void saveProfile(Profile profile);
    public void updateProfile(Profile profile);
    public void deleteProfile(Profile profile);


    Track getTrack(long trackID);
}
