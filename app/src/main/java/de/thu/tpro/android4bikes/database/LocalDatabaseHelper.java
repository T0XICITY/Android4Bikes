package de.thu.tpro.android4bikes.database;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.FineGrainedPositions;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

public interface LocalDatabaseHelper {
    //Profile
    void storeProfile(Profile Profile);

    Profile readProfile(String googleID);

    void updateProfile(Profile profile);

    void deleteProfile(String googleID);

    void deleteProfile(Profile profile); //Internally see readFineGrainedPositions

    //BikeRacks
    void storeBikeRack(BikeRack bikeRack);

    List<BikeRack> readBikeRacks(String postcode);

    void deleteBikeRack(String fireBaseID);

    void deleteBikeRack(BikeRack bikeRack); //Internally see readFineGrainedPositions

    //Tracks
    void storeTrack(Track track);

    void storeFineGrainedPositions(FineGrainedPositions fineGrainedPositions);//id in local db equals to track id (firebase id)

    List<Track> readTracks(String postcode);

    //query first "fineGrainedPositionsDB" with postcodes to get TrackID. Then query "trackDB" with this TrackID to get the tracks.
    void deleteTrack(String fireBaseID);

    FineGrainedPositions readFineGrainedPositions(String firebaseID);

    FineGrainedPositions readFineGrainedPositions(Track track); //internally: readFineGrainedPositions(track.getID());

    //HazardAlerts
    void storeHazardAlerts(HazardAlert hazardAlert);

    List<HazardAlert> readHazardAlerts(String postcode);

    void deleteHazardAlert(String fireBaseID);

    void deleteHazardAlert(HazardAlert hazardAlert); //Internally see readFineGrainedPositions

    //Heatmap:
    void addToUtilization(Position position); //>50: stores to firebase

    void resetUtilization(); //resets local utilization db

    /**
     * Method to get the last position from the local database
     * @return the last saved position in the database
     * */
    Position getLastPosition();
}
