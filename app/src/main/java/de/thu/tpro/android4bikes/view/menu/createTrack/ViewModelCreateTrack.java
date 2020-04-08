package de.thu.tpro.android4bikes.view.menu.createTrack;

import java.util.List;

import de.thu.tpro.android4bikes.data.model.Track;

public class ViewModelCreateTrack {
    List<Track> trackList;
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
}
