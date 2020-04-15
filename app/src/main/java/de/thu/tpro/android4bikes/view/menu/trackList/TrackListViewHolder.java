package de.thu.tpro.android4bikes.view.menu.trackList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    TextView tv_name;
    Button btn_start;
    TextView tv_description;
    TextView tv_trackLength;
    TextView tv_trackDistance;
    TextView tv_author;
    RatingBar rating_roadQuality;
    RatingBar rating_dificulty;
    RatingBar rating_funfactor;
    LinearLayout detailView;

    public TrackListViewHolder(@NonNull View itemView) {
        super(itemView);
        initCardView();
        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

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
                btn_start.setVisibility(View.VISIBLE);
            } else {
                detailView.setVisibility(View.GONE);
                btn_start.setVisibility(View.GONE);
        }
    }

    /**
     * Initiates view elements of the card view
     */
    private void initCardView() {
        tv_name = itemView.findViewById(R.id.tv_trackname);
        btn_start = itemView.findViewById(R.id.btn_start);
        tv_description = itemView.findViewById(R.id.tv_description);
        rating_roadQuality = itemView.findViewById(R.id.ratingBar_roadQuality);
        rating_dificulty = itemView.findViewById(R.id.ratingBar_dificulty);
        rating_funfactor = itemView.findViewById(R.id.ratingBar_funfactor);
        detailView = itemView.findViewById(R.id.linear_detailView);
        tv_trackLength = itemView.findViewById(R.id.tv_tracklength);
        tv_trackDistance = itemView.findViewById(R.id.tv_Trackdistance);
        tv_author = itemView.findViewById(R.id.tv_author);
    }
}
