package de.thu.tpro.android4bikes.view.menu.createTrack;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.services.GpsLocation;

public class ViewModelCreateTrack {
    List<Track> trackList;
    GpsLocation currentLocation;

    //TODO: Backend anbinden
    public ViewModelCreateTrack(List<Track> trackList) {
        this.trackList = trackList;
    }

    public List<Track> getTrackList() {
        return trackList;
    }

    public void setTrackList(List<Track> trackList) {
        this.trackList = trackList;
    }

    /**
     * https://stackoverflow.com/questions/33793948/searchview-with-recyclerview/35144362#35144362
     *
     * @param filterText the search term to filter the list
     * @return filtered list
     */
    public List<Track> filteredTrackList(String filterText) {
        filterText = filterText.toLowerCase();
        final List<Track> filteredTrackList = new ArrayList<>();
        for (Track track : trackList) {
            final String title = track.getName().toLowerCase();
            if (title.contains(filterText)) {
                filteredTrackList.add(track);
                continue;
            }
            final String description = track.getDescription();
            if (description != null && description.toLowerCase().contains(filterText)) {
                filteredTrackList.add(track);
            }
        }
        return filteredTrackList;
    }
    public List<Track> filteredTrackList(int range,int quality, int dificulty, int funfactor){
        final List<Track> filteredTrackList = new ArrayList<>();
        for (Track track: trackList){
            if (track.getRating().getRoadquality() >= quality && track.getRating().getDifficulty() >= dificulty && track.getRating().getFun() >= funfactor){
                filteredTrackList.add(track);
            }

        }
        return filteredTrackList;
    }

    private double calculateDistance(Track track){
        Position startPosition = track.getFineGrainedPositions().get(0);
        Location trackLocation = new Location("");
        trackLocation.setLatitude(startPosition.getLatitude());
        trackLocation.setLongitude(startPosition.getLongitude());
        return currentLocation.distanceTo(trackLocation);
    }

    public void setLocation(GpsLocation gpsLocation) {
        currentLocation = gpsLocation;
    }
}
