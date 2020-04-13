package de.thu.tpro.android4bikes.view.menu.createTrack;

import android.location.Location;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.services.GpsLocation;

public class ViewModelCreateTrack {

    private static final String LOG_TAG = "ViewModelCreateTrack";

    private List<TrackDistanceTuple> trackDistanceList;
    private GpsLocation currentLocation;

    //TODO: Backend anbinden
    public ViewModelCreateTrack(List<Track> trackList) {
        trackDistanceList = new ArrayList<>();
        for (Track track : trackList) {
            trackDistanceList.add(new TrackDistanceTuple(track));
        }
    }

    /**
     * @return list of tracks the view model is working with
     */
    public List<TrackDistanceTuple> getTrackDistanceList() {
        return trackDistanceList;
    }

    /**
     * https://stackoverflow.com/questions/33793948/searchview-with-recyclerview/35144362#35144362
     * Searches all tracks (name and description) for the given term
     *
     * @param searchTerm the search term to filter the list
     * @return filtered list
     */
    public List<TrackDistanceTuple> searchTrackList(String searchTerm) {
        searchTerm = searchTerm.toLowerCase();
        final List<TrackDistanceTuple> filteredTrackList = new ArrayList<>();
        for (TrackDistanceTuple tuple : trackDistanceList) {
            Track track = tuple.getTrack();
            final String title = track.getName().toLowerCase();
            if (title.contains(searchTerm)) {
                filteredTrackList.add(tuple);
                continue;
            }
            final String description = track.getDescription();
            if (description != null && description.toLowerCase().contains(searchTerm)) {
                filteredTrackList.add(tuple);
            }
        }
        return filteredTrackList;
    }

    /**
     * filters the track list by various criteria
     *
     * @param range all tracks must be in a distance of lower or equal than range
     * @param quality all tracks must have a road quality of equal or higher than quality
     * @param difficulty all tracks must have a difficulty of equal or higher than difficulty
     * @param funfactor all tracks must have a fun level of equal or higher than funfactor
     * @return list of all tracks that match the given criteria
     */
    public List<TrackDistanceTuple> filterTrackList(int range, int quality, int difficulty, int funfactor){
        final List<TrackDistanceTuple> filteredTrackList = new ArrayList<>();
        for (TrackDistanceTuple tuple : trackDistanceList){
            Track track = tuple.getTrack();
            if (track.getRating().getRoadquality() >= quality && track.getRating().getDifficulty() >= difficulty && track.getRating().getFun() >= funfactor){
                filteredTrackList.add(tuple);
            }

        }
        return filteredTrackList;
    }

    public void udpateUserLocation(GpsLocation userLocation) {
        currentLocation = userLocation;
        calculateAllDistances();
    }

    /**
     * Calculates the distance between a track starting point and the user's current location
     * @param trackListIndex index of the track to calculate distance to in the trackList
     */
    private void calculateDistance(int trackListIndex){
        // abort calculation if user's location is unknown
        if (currentLocation==null)
            return;

        TrackDistanceTuple currentTuple = trackDistanceList.get(trackListIndex);

        Position startPosition = currentTuple.getTrack().getFineGrainedPositions().get(0);
        Location trackLocation = new Location("");

        trackLocation.setLatitude(startPosition.getLatitude());
        trackLocation.setLongitude(startPosition.getLongitude());

        // set distance to track in kilometres
        trackDistanceList.get(trackListIndex)
                .setDistanceToUser(currentLocation.distanceTo(trackLocation)  / 1000);
    }

    /**
     * calculates the distance between all tracks and the user's current location
     */
    private void calculateAllDistances() {
        for (int i = 0; i < trackDistanceList.size(); i++){
            calculateDistance(i);
        }
    }
}
