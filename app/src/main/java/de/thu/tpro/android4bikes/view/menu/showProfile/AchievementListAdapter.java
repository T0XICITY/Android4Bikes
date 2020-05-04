package de.thu.tpro.android4bikes.view.menu.showProfile;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.achievements.Achievement;
import de.thu.tpro.android4bikes.util.GlobalContext;

public class AchievementListAdapter extends RecyclerView.Adapter<AchievementViewHolder> implements View.OnClickListener {

    private LayoutInflater inflater;
    private Resources resources;

    private FragmentShowProfile parentFragment;
    private List<Achievement> achievementList;

    public AchievementListAdapter(FragmentShowProfile parent) {
        super();
        this.parentFragment = parent;
        this.inflater = LayoutInflater.from(parent.requireActivity());
        this.resources = parent.getResources();
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cardview_achievement, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Achievement currentAchievement = achievementList.get(position);

        int colorId = resources.getColor(R.color.colorPrimary,
                parentFragment.getActivity().getTheme());
        holder.cardView_achievement.setCardBackgroundColor(colorId);

        // make both card- and imageview clickable
        holder.cardView_achievement.setOnClickListener(this);
        holder.iv_achievement.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return achievementList.size();
    }

    /**
     * Replaces the current list of achievements with a new one and updates the UI
     * @param achievementList new list of Achievements
     */
    public void replaceData(List<Achievement> achievementList) {
        this.achievementList = achievementList;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        // TODO: display actually interesting text
        String title = resources.getString(R.string.achievement);
        String message = resources.getString(R.string.achievement_text);
        parentFragment.openAchievements(title, message);
    }
}
