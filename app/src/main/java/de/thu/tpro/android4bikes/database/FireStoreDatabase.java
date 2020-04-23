package de.thu.tpro.android4bikes.database;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public interface FireStoreDatabase {
    //Profile
    void storeProfileToFireStoreAndLocalDB(Profile profile);

    void readProfileFromFireStoreAndStoreItToLocalDB(String googleID);

    void updateProfileInFireStoreAndLocalDB(Profile profile);

    void deleteProfileFromFireStoreAndLocalDB(String googleID);

    //BikeRacks
    //void submitBikeRackToFireStore(BikeRack bikeRack);

    //void readBikeRacksFromFireStoreAndStoreItToLocalDB(String postcode);

    //Tracks
    //void storeTrackToFireStoreAndLocalDB(Track track);

    void deleteTrackFromFireStoreAndLocalDB(String fireBaseID);

    //void readTracksFromFireStoreAndStoreItToLocalDB(String fireBaseID);

    //HazardAlerts
    void submitHazardAlertToFireStore(HazardAlert hazardAlert);

    void readHazardAlertsFromFireStoreAndStoreItToLocalDB(String postcode);

    //Heatmap
    void storeBufferedUtilizationToFireStore(List<Position> utilization);

    void readProfilesBasedOnTracks(List<Track> o);

    void storeBufferedProfileToFireStore(Profile p);

    void deleteBufferedProfileFromFireStore(Profile profile);

    void storeBufferedTrackInFireStore(Track t);

    void deleteBufferedTrackFromFireStore(Track t);

    void storeBufferedBikeRackInFireStore(BikeRack b);

    void storeBufferedHazardAlertInFireStore(HazardAlert h);

    //#################################################################
    void readAllOwnTracksAndStoreItToOwnDB(String firebaseID);

}
