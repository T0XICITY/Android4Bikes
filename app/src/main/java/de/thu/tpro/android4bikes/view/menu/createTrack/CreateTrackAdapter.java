package de.thu.tpro.android4bikes.view.menu.createTrack;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.Rating;
import de.thu.tpro.android4bikes.data.model.Track;

public class CreateTrackAdapter extends RecyclerView.Adapter<CreateTrackViewHolder> {
    private final LayoutInflater inflater;
    private List<TrackDistanceTuple> entries;
    private Activity context;
    private CardView cardView;

    public CreateTrackAdapter(Activity context, List<TrackDistanceTuple> entries) {
        super();
        this.context = context;
        this.entries = entries;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CreateTrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout of current Row
        View row = inflater.inflate(R.layout.list_create_tracks, parent, false);
        return new CreateTrackViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateTrackViewHolder holder, int position) {
        // Init tracks and ratings
        TrackDistanceTuple tuple = entries.get(position);
        Track currentTrack = tuple.getTrack();
        Rating currentRating = currentTrack.getRating();

        // Insert Data into elements
        holder.tv_name.setText(currentTrack.getName());
        holder.tv_description.setText(currentTrack.getDescription());
        holder.rating_roadQuality.setRating(currentRating.getRoadquality());
        holder.rating_dificulty.setRating(currentRating.getDifficulty());
        holder.rating_funfactor.setRating(currentRating.getFun());
        holder.tv_author.setText(currentTrack.getAuthor_googleID());
        holder.tv_trackLength.setText(""+currentTrack.getDistance_km());

        // Only display distance when available
        double currentDistance = tuple.getDistanceToUser();
        String distanceText;
        if (currentDistance > 0) {
            distanceText = String.format(context.getResources().getString(R.string.distance), currentDistance);
        } else {
            distanceText = (String) context.getResources().getText(R.string.not_available);
        }
        holder.tv_trackDistance.setText(distanceText);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    /**
     * Replaces the list of tracks with a new one (e.g. when filtered) and updates view
     * @param newTrackList
     */
    public void replaceData(List<TrackDistanceTuple> newTrackList) {
        entries = newTrackList;
        notifyDataSetChanged();
    }
}
