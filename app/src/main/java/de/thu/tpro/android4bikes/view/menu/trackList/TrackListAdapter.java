package de.thu.tpro.android4bikes.view.menu.trackList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.Rating;
import de.thu.tpro.android4bikes.data.model.Track;
import de.thu.tpro.android4bikes.util.GeoLocationHelper;

/**
 * @author Stefanie
 * RecyclerView Adapter to create a Set of CardViews for Tracks
 */
public class TrackListAdapter extends RecyclerView.Adapter<TrackListViewHolder> {

    private final LayoutInflater inflater;
    private List<TrackDistanceTuple> entries;
    private FragmentTrackList parent;

    public TrackListAdapter(FragmentTrackList parent, List<TrackDistanceTuple> entries) {
        super();
        this.parent = parent;
        this.entries = entries;
        this.inflater = LayoutInflater.from(parent.getContext());
    }

    @NonNull
    @Override
    public TrackListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout of current Row
        View row = inflater.inflate(R.layout.cardview_track_list, parent, false);
        return new TrackListViewHolder(row, this.parent);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListViewHolder holder, int position) {
        // Init tracks and ratings
        TrackDistanceTuple tuple = entries.get(position);
        Track currentTrack = tuple.getTrack();
        Rating currentRating = currentTrack.getRating();

        // Insert Data into elements
        holder.tv_name.setText(currentTrack.getName());
        holder.tv_description.setText(currentTrack.getDescription());
        holder.rating_roadQuality.setRating(currentRating.getRoadquality());
        holder.rating_difficulty.setRating(currentRating.getDifficulty());
        holder.rating_funfactor.setRating(currentRating.getFun());
        // TODO: replace ID with actual name (-> waiting for backend)
        holder.tv_author.setText(currentTrack.getAuthor_googleID());
        holder.tv_trackLength.setText(String.format(
                parent.getContext().getResources().getString(R.string.distance),
                (double) currentTrack.getDistance_km())
        );
        // TODO replace postcode with actual location name (-> waiting for backend)
        String postcodeStart = GeoLocationHelper.convertPositionToPostcode(currentTrack.getStartPosition());
        holder.tv_trackLocation.setText(postcodeStart);


        // Only display distance when available
        double currentDistance = tuple.getDistanceToUser();
        String distanceText;
        if (currentDistance > 0) {
            distanceText = String.format(parent.getContext().getResources().getString(R.string.distance), currentDistance);
        } else {
            distanceText = (String) parent.getContext().getResources().getText(R.string.not_available);
        }
        holder.tv_trackDistance.setText(distanceText);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    /**
     * Replaces the list of tracks with a new one (e.g. when filtered) and updates view
     *
     * @param newTrackList
     */
    public void replaceData(List<TrackDistanceTuple> newTrackList) {
        entries = newTrackList;
        notifyDataSetChanged();
    }
}
