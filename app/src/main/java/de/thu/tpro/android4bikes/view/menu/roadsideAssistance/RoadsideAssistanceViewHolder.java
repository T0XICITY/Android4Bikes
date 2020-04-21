package de.thu.tpro.android4bikes.view.menu.roadsideAssistance;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.thu.tpro.android4bikes.R;

public class RoadsideAssistanceViewHolder extends RecyclerView.ViewHolder {

    CardView cardView;
    ImageView iv_institution;
    TextView tv_institution;
    ImageView iv_call;

    public RoadsideAssistanceViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = (CardView) itemView;
        initCardView();
    }

    public void initCardView() {
        iv_institution = itemView.findViewById(R.id.iv_institution);
        tv_institution = itemView.findViewById(R.id.tv_institution);
        iv_call = itemView.findViewById(R.id.iv_call);
    }
}
