package de.thu.tpro.android4bikes.database;

import java.util.List;
import java.util.Map;

import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.FineGrainedPositions;
import de.thu.tpro.android4bikes.data.model.HazardAlert;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.data.model.Track;

/**
 * Interface which defines the needed functionality for a local database in Android4Bikes
 */
public interface LocalDatabaseHelper {
    /**
     * Method to save a profile in the local Database
     *
     * @param profile is the profile which should be stored in the local database
     */
    void storeProfile(Profile profile);

    /**
     * Method to save a profile in the local Database
     *
     * @param map_profile is the map of a profile which should be stored in the local database
     */
    void storeProfile(Map map_profile);

    /**
     * Method to read a profile from the local database
     *
     * @param googleID is the googleID of the profile which should be loaded form the locale database
     * @return the profile which was requested
     */
    Profile readProfile(String googleID);

    /**
     * Method which updates an existing profile in the local database
     *
     * @param profile is the profile which should be updated with the new values
     */
    void updateProfile(Profile profile);

    /**
     * Method to delete a profile form the local Database
     *
     * @param googleID is the googleID of the profile which should be deleted
     */
    void deleteProfile(String googleID);

    /**
     * Method to delete a profile form the local database
     *
     * @param profile is the profile which should be deleted
     */
    void deleteProfile(Profile profile); //Internally see readFineGrainedPositions

    /**
     * Method to store a BikeRack in the local database
     *
     * @param bikeRack is the BikeRack which should be stored in the local database
     */
    void storeBikeRack(BikeRack bikeRack);

    /**
     * Method to read BikeRacks form the local database
     *
     * @param postcode is the postcode of the area of the BikeRacks
     * @return returns a list of all BikeRacks in the postcode area
     */
    List<BikeRack> readBikeRacks(String postcode);

    /**
     * Method to delete a BikeRack from the local database
     *
     * @param fireBaseID is the firebaseID of the BikeRack which should be deleted
     */
    void deleteBikeRack(String fireBaseID);

    /**
     * Method to delete a BikeRack from the local database
     *
     * @param bikeRack is the BikeRack which should be deleted
     */
    void deleteBikeRack(BikeRack bikeRack); //Internally see readFineGrainedPositions

    /**
     * Method to store a track in the local database
     *
     * @param track is the track which should be stored in the local database
     */
    void storeTrack(Track track);

    /**
     * Method to read tracks from the local database
     *
     * @param postcode is the postcode of the area where the tracks are
     * @return list of tracks which are in the area of the postcode
     */
    List<Track> readTracks(String postcode);

    //query first "fineGrainedPositionsDB" with postcodes to get TrackID. Then query "trackDB" with this TrackID to get the tracks.

    /**
     * Method to delete a track from the local database
     *
     * @param fireBaseID is the firebaseID of the track which should be deleted
     */
    void deleteTrack(String fireBaseID);

    /**
     * Method to store fine grained positions to the local database
     *
     * @param fineGrainedPositions are the fine grained positions which should be saved
     */
    void storeFineGrainedPositions(FineGrainedPositions fineGrainedPositions);//id in local db equals to track id (firebase id)

    /**
     * Method to read fine grained positions from the loacal database
     *
     * @param firebaseID is the firebaseID of the fine grained positions
     * @return fine grained positions which where requested
     */
    FineGrainedPositions readFineGrainedPositions(String firebaseID);

    /**
     * Method to read fine grained positions from the loacal database
     *
     * @param track is the track with the fine grained positions
     * @return fine grained positions which where requested
     */
    FineGrainedPositions readFineGrainedPositions(Track track); //internally: readFineGrainedPositions(track.getID());

    /**
     * Method to store a hazard alert in the local database
     *
     * @param hazardAlert is the hazard alert which should be stored
     */
    void storeHazardAlerts(HazardAlert hazardAlert);

    /**
     * Method to read hazard alerts from the local database
     *
     * @param postcode is the area of the hazard alerts
     * @return list of all hazard alerts in the area of the postcode
     */
    List<HazardAlert> readHazardAlerts(String postcode);

    /**
     * Method to delete a hazard alert from the local database
     *
     * @param fireBaseID is the firebaseID of the hazard alert which should be deleted
     */
    void deleteHazardAlert(String fireBaseID);

    /**
     * Method to delete a hazard alert from the local database
     *
     * @param hazardAlert is the hazard alert which should be deleted
     */
    void deleteHazardAlert(HazardAlert hazardAlert); //Internally see readFineGrainedPositions

    /**
     * Method to add a position to the utilisation.
     * After 50 stored positions the utilisation is send to the server and
     * the local database gets cleared
     */
    void addToUtilization(Position position); //>50: stores to firebase

    /**
     * Method to reset the utilisation (positions) from the local database
     */
    void resetUtilization(); //resets local utilization db

    /**
     * Method to get the last position from the local database
     * @return the last saved position in the database
     * */
    Position getLastPosition();
}
