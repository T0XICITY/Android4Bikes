package de.thu.tpro.android4bikes.view.menu.roadsideAssistance;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.Arrays;
import java.util.List;

import de.thu.tpro.android4bikes.R;

public class RoadsideAssistanceListAdapter extends BaseAdapter {

    private List<RoadsideAssistanceEntry> entries;
    private Activity context;
    private final LayoutInflater inflater;


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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View row;

        Log.d("Custom_ListView",String.format("%s: %s",
                position, entries.get(position).text_institution));

        if (convertView == null) {
            row = inflater.inflate(R.layout.list_road_assistance,parent,false);
        }else{
            row = convertView;
        }

        ImageView iv = row.findViewById(R.id.iv_institution);
        TextView tv = row.findViewById(R.id.tv_institution);
        ImageButton ib = row.findViewById(R.id.ib_Call);

        iv.setImageResource(entries.get(position).resId_institution);
        tv.setText(entries.get(position).text_institution);
        ib.setImageResource(entries.get(position).resId_call);
        return row;
    }


}
