package de.thu.tpro.android4bikes.view.menu.roadsideAssistance;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import de.thu.tpro.android4bikes.R;

public class Custom_Listview extends ArrayAdapter<String> {

    private String[] tv_institutions;
    private Integer [] ib_call;
    private Integer[] iv_institutions;
    private Activity context;


    public Custom_Listview(Activity context, String[] tv_institutions,Integer[] ib_call,  Integer[] iv_institutions) {
        super(context, R.layout.fragment_road_assistance,tv_institutions);

        this.context = context;
        this.ib_call = ib_call;
        this.iv_institutions = iv_institutions;
        this.tv_institutions = tv_institutions;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder = null;
        if(r==null){
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.fragment_road_assistance,null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) r.getTag();
        }
            viewHolder.iv.setImageResource(iv_institutions[position]);
            viewHolder.tv1.setText(tv_institutions[position]);
            viewHolder.ib_call.setImageResource(ib_call[position]);


        return r;
    }

    class ViewHolder {
        TextView tv1;
        ImageView iv;
        ImageView ib_call;

        ViewHolder(View v) {

            tv1 = (TextView) v.findViewById(R.id.tv_institution);
            iv = (ImageView) v.findViewById(R.id.imageView);
            ib_call = (ImageView) v.findViewById(R.id.imageView);

        }

    }


}
