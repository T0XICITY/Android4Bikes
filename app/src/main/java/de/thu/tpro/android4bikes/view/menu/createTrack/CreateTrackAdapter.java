package de.thu.tpro.android4bikes.view.menu.createTrack;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.Track;

public class CreateTrackAdapter extends RecyclerView.Adapter<CreateTrackAdapter.CreateTrackViewHolder> {
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

    public class CreateTrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name;
        Button btn_start;
        TextView tv_description;
        RatingBar rb_rating;
        public CreateTrackViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_trackname);
            btn_start = itemView.findViewById(R.id.btn_start);
            tv_description = itemView.findViewById(R.id.tv_description);
            rb_rating = itemView.findViewById(R.id.ratingBar_Rating);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //https://stackoverflow.com/questions/44653323/horizontal-androidanimatelayoutchanges-true-animation-not-smooth
            TransitionManager.beginDelayedTransition((ViewGroup) view.getParent());
            if (btn_start.getVisibility() == View.GONE) {
                rb_rating.setVisibility(View.VISIBLE);
                btn_start.setVisibility(View.VISIBLE);
                tv_description.setVisibility(View.VISIBLE);
            } else {
                rb_rating.setVisibility(View.GONE);
                btn_start.setVisibility(View.GONE);
                tv_description.setVisibility(View.GONE);
            }
        }
    }
}
