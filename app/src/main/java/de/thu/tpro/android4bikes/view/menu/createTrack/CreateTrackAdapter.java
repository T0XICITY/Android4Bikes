package de.thu.tpro.android4bikes.view.menu.createTrack;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.thu.tpro.android4bikes.R;
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
        // Insert Data into elements
        Log.d("CreateTrackAdapter", entries.get(position).getName());
        holder.tv_name.setText(entries.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void replaceData(List<Track> filteredTrackList) {
        entries = filteredTrackList;
        notifyDataSetChanged();
    }
}
