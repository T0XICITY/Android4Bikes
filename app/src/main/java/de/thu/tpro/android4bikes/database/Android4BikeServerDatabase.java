package de.thu.tpro.android4bikes.database;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.FineGrainedPositions;
import de.thu.tpro.android4bikes.data.model.Track;

public interface Android4BikeServerDatabase {
    //Profile
    public void storeProfileToFireStoreAndLocalDB(Profile Profile);
    public void readProfileFromFireStoreAndStoreItToLocalDB(String googleID);
    public void updateProfileInFireStoreAndLocalDB(Profile profile);
    public void deleteProfileFromFireStoreAndLocalDB(String googleID);

    //BikeRacks
    public void storeBikeRackToFireStoreAndLocalDB(BikeRack bikeRack);
    public void readBikeRacksFromFireStoreAndStoreItToLocalDB(String postcode);

    //Tracks
    public void storeTrackToFireStoreAndLocalDB(Track track, FineGrainedPositions fineGrainedPositions);
    public void readCoarseGrainedTracksFromFireStoreAndStoreThemToLocalDB(String fireBaseID);
    public void readFineGrainedTracksFromFireStoreAndStoreThemToLocalDB(String fireBaseID);
    public void deleteTrackFromFireStoreAndLocalDB(String fireBaseID);

    //HazardAlerts
    public void storeHazardAlertToFireStoreAndLocalDB(HazardAlert hazardAlert);
    public void readHazardAlertsFromFireStoreAndStoreItToLocalDB(String postcode);

    //Heatmap
    public void storeUtilizationToFireStore(List<Position> utilization);
}
