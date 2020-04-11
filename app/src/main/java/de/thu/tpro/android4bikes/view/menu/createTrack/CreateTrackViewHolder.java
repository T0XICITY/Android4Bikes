package de.thu.tpro.android4bikes.view.menu.createTrack;

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

public class CreateTrackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView tv_name;
    Button btn_start;
    TextView tv_description;
    RatingBar rating_roadQuality;
    RatingBar rating_dificulty;
    RatingBar rating_funfactor;
    LinearLayout detailView;

    public CreateTrackViewHolder(@NonNull View itemView) {
        super(itemView);
        initCardView();
        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        TransitionManager.beginDelayedTransition((ViewGroup) view.getParent());
        toggleViewVisibility();

    }

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

    private void initCardView() {
        tv_name = itemView.findViewById(R.id.tv_trackname);
        btn_start = itemView.findViewById(R.id.btn_start);
        tv_description = itemView.findViewById(R.id.tv_description);
        rating_roadQuality = itemView.findViewById(R.id.ratingBar_roadQuality);
        rating_dificulty = itemView.findViewById(R.id.ratingBar_dificulty);
        rating_funfactor = itemView.findViewById(R.id.ratingBar_funfactor);
        detailView = itemView.findViewById(R.id.linear_detailView);
    }
}
