package de.thu.tpro.android4bikes.view.menu.trackList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import de.thu.tpro.android4bikes.R;

/**
 * @author Stefanie
 * View Holder class for Track CardView to be displayed inside a RecyclerView
 */
public class TrackListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private FragmentTrackList parent;

    // package private for easy access
    TextView tv_name;
    TextView tv_description;
    TextView tv_trackLength;
    TextView tv_trackDistance;
    TextView tv_author;
    TextView tv_trackLocation;
    RatingBar rating_roadQuality;
    RatingBar rating_difficulty;
    RatingBar rating_funfactor;
    LinearLayout detailView;

    public TrackListViewHolder(@NonNull View itemView, FragmentTrackList parent) {
        super(itemView);
        initCardView();
        itemView.setOnClickListener(this);
        this.parent = parent;
    }


    @Override
    public void onClick(View view) {
        parent.searchView.clearFocus();
        TransitionManager.beginDelayedTransition((ViewGroup) view.getParent());
        toggleViewVisibility();

    }

    /**
     * shows or hides elements inside detail view
     */
    private void toggleViewVisibility() {
        //https://stackoverflow.com/questions/44653323/horizontal-androidanimatelayoutchanges-true-animation-not-smooth
        if (detailView.getVisibility() == View.GONE) {
                detailView.setVisibility(View.VISIBLE);
            } else {
                detailView.setVisibility(View.GONE);
        }
    }

    /**
     * Initiates view elements of the card view
     */
    private void initCardView() {
        tv_name = itemView.findViewById(R.id.tv_trackname);
        tv_description = itemView.findViewById(R.id.tv_description);
        rating_roadQuality = itemView.findViewById(R.id.ratingBar_roadQuality);
        rating_difficulty = itemView.findViewById(R.id.ratingBar_difficulty);
        rating_funfactor = itemView.findViewById(R.id.ratingBar_funfactor);
        detailView = itemView.findViewById(R.id.layout_detailView);
        tv_trackLength = itemView.findViewById(R.id.tv_tracklength);
        tv_trackDistance = itemView.findViewById(R.id.tv_Trackdistance);
        tv_author = itemView.findViewById(R.id.tv_author);
        tv_trackLocation = itemView.findViewById(R.id.tv_trackLocation);
    }
}
