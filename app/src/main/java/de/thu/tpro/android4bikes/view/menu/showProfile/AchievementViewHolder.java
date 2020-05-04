package de.thu.tpro.android4bikes.view.menu.showProfile;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import de.thu.tpro.android4bikes.R;

public class AchievementViewHolder extends RecyclerView.ViewHolder {
    // all attributes are package private on purpose to make accessible from adapter
    final MaterialCardView cardView_achievement;
    final ImageView iv_achievement;

    public AchievementViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView_achievement = itemView.findViewById(R.id.cardview_achievement);
        iv_achievement = itemView.findViewById(R.id.iv_achievement);
    }
}
