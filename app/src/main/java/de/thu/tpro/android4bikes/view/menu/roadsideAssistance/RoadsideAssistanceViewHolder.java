package de.thu.tpro.android4bikes.view.menu.roadsideAssistance;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.thu.tpro.android4bikes.R;

public class RoadsideAssistanceViewHolder extends RecyclerView.ViewHolder {

    ImageView iv_institution;
    TextView tv_institution;
    ImageView ib_call;

    public RoadsideAssistanceViewHolder(@NonNull View itemView) {
        super(itemView);
        initCardView();
    }

    public void initCardView() {
        iv_institution = itemView.findViewById(R.id.iv_institution);
        tv_institution = itemView.findViewById(R.id.tv_institution);
        ib_call = itemView.findViewById(R.id.ib_Call);
    }
}
