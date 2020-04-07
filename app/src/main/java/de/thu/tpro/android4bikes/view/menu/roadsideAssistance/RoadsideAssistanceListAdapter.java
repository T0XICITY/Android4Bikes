package de.thu.tpro.android4bikes.view.menu.roadsideAssistance;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.view.MainActivity;

/**
 * @author Elias, Stefanie
 * Custom adapter to display list of {@link RoadsideAssistanceEntry} in ListView
 */
public class RoadsideAssistanceListAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private List<RoadsideAssistanceEntry> entries;
    private Activity context;


    public RoadsideAssistanceListAdapter(Activity context, List<RoadsideAssistanceEntry> entries) {
        super();

        this.context = context;
        this.entries = entries;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public RoadsideAssistanceEntry getItem(int i) {
        return entries.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Populates a row with view elements and data from the corresponding
     * {@link RoadsideAssistanceEntry}.
     * <p/>
     * This method is called per row of the ListView.
     *
     * @param position    current row number
     * @param convertView view element of current row
     * @param parent      parent element
     * @return view element of current row with data
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Inflate Layout of current Row
        View row = convertView;
        if (row == null) {
            row = inflater.inflate(R.layout.list_road_assistance, parent, false);
        }

        // Inflate UI elements of current Row
        ImageView iv = row.findViewById(R.id.iv_institution);
        TextView tv = row.findViewById(R.id.tv_institution);
        ImageButton ib = row.findViewById(R.id.image_Call);

        // Insert Data into elements
        iv.setImageResource(entries.get(position).resId_institution);
        tv.setText(entries.get(position).text_institution);
        ib.setImageResource(entries.get(position).resId_call);

        return row;
    }


}
