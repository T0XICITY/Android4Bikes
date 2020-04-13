package de.thu.tpro.android4bikes.view.menu.createTrack;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.Rating;
import de.thu.tpro.android4bikes.data.model.Track;

public class CreateTrackAdapter extends RecyclerView.Adapter<CreateTrackViewHolder> {
    private final LayoutInflater inflater;
    private List<Track> entries;
    private Activity context;
    private CardView cardView;

    public CreateTrackAdapter(Activity context, List<Track> entries) {
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
        Track currentTrack = entries.get(position);
        Rating currentRating = currentTrack.getRating();
        // Insert Data into elements
        Log.d("CreateTrackAdapter", currentTrack.getName());
        holder.tv_name.setText(currentTrack.getName());
        holder.tv_description.setText(currentTrack.getDescription());
        holder.rating_roadQuality.setRating(currentRating.getRoadquality());
        holder.rating_dificulty.setRating(currentRating.getDifficulty());
        holder.rating_funfactor.setRating(currentRating.getFun());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void replaceData(List<Track> filteredTrackList) {
        entries = filteredTrackList;
        notifyDataSetChanged();
    }
    private void setRatingBar(){}
}
