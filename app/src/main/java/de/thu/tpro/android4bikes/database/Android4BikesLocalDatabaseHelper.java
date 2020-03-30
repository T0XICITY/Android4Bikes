package de.thu.tpro.android4bikes.database;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.FineGrainedPositions;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public interface Android4BikesLocalDatabaseHelper {
    //Profile
    public void storeProfile(Profile Profile);
    public Profile readProfile(String googleID);
    public void updateProfile(Profile profile);
    public void deleteProfile(String googleID);
    public void deleteProfile(Profile profile); //Internally see readFineGrainedPositions

    //BikeRacks
    public void storeBikeRack(BikeRack bikeRack);
    public List<BikeRack> readBikeRacks(String postcode);
    public void deleteBikeRack(String fireBaseID);
    public void deleteBikeRack(BikeRack bikeRack); //Internally see readFineGrainedPositions

    //Tracks
    public void storeTrack(Track track);
    public void storeFineGrainedPositions(FineGrainedPositions fineGrainedPositions);//id in local db equals to track id (firebase id)
    public List<Track> readTracks(String postcode);
    //query first "fineGrainedPositionsDB" with postcodes to get TrackID. Then query "trackDB" with this TrackID to get the tracks.
    public void deleteTrack(String fireBaseID);
    public FineGrainedPositions readFineGrainedPositions(String firebaseID);
    public FineGrainedPositions readFineGrainedPositions(Track track); //internally: readFineGrainedPositions(track.getID());

    //HazardAlerts
    public void storeHazardAlerts(HazardAlert hazardAlert);
    public List<HazardAlert> readHazardAlerts(String postcode);
    public void deleteHazardAlert(String fireBaseID);
    public void deleteHazardAlert(HazardAlert hazardAlert); //Internally see readFineGrainedPositions

    //Heatmap:
    public void addToUtilization(Position position); //>50: stores to firebase
    public void resetUtilization(); //resets local utilization db
}
