package de.thu.tpro.android4bikes.database;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.FineGrainedPositions;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public interface Android4BikeServerDatabase {
    //Profile
    void storeProfileToFireStoreAndLocalDB(Profile Profile);

    void readProfileFromFireStoreAndStoreItToLocalDB(String googleID);

    void updateProfileInFireStoreAndLocalDB(Profile profile);

    void deleteProfileFromFireStoreAndLocalDB(String googleID);

    //BikeRacks
    void storeBikeRackToFireStoreAndLocalDB(BikeRack bikeRack);

    void readBikeRacksFromFireStoreAndStoreItToLocalDB(String postcode);

    //Tracks
    void storeTrackToFireStoreAndLocalDB(Track track, FineGrainedPositions fineGrainedPositions);

    void readCoarseGrainedTracksFromFireStoreAndStoreThemToLocalDB(String fireBaseID);

    void readFineGrainedTracksFromFireStoreAndStoreThemToLocalDB(String fireBaseID);

    void deleteTrackFromFireStoreAndLocalDB(String fireBaseID);

    //HazardAlerts
    void storeHazardAlertToFireStoreAndLocalDB(HazardAlert hazardAlert);

    void readHazardAlertsFromFireStoreAndStoreItToLocalDB(String postcode);

    //Heatmap
    void storeUtilizationToFireStore(List<Position> utilization);
}
