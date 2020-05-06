package de.thu.tpro.android4bikes.view.menu.roadsideAssistance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.thu.tpro.android4bikes.R;

/**
 * @author Elias, Stefanie
 * Custom adapter to display list of {@link RoadsideAssistanceEntry} in ListView
 */
public class RoadsideAssistanceListAdapter extends RecyclerView.Adapter<RoadsideAssistanceViewHolder> {

    private static final int REQUEST_PHONE_CALL = 1;

    private final LayoutInflater inflater;
    private List<RoadsideAssistanceEntry> entries;
    private String[] listTel;
    private FragmentRoadsideAssistance parent;
    private Intent intent;

    public RoadsideAssistanceListAdapter(FragmentRoadsideAssistance parent, List<RoadsideAssistanceEntry> entries, String[] paraListTel) {
        super();

        this.parent = parent;
        this.entries = entries;
        this.listTel = paraListTel;
        inflater = LayoutInflater.from(parent.getActivity());
    }

    @NonNull
    @Override
    public RoadsideAssistanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = inflater.inflate(R.layout.cardview_emergency_number, parent, false);
        return new RoadsideAssistanceViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RoadsideAssistanceViewHolder holder, int position) {

        // Insert Data into elements from view holder
        holder.iv_institution.setImageResource(entries.get(position).resId_institution);
        holder.tv_institution.setText(entries.get(position).text_institution);
        holder.iv_call.setImageResource(entries.get(position).resId_call);

        holder.cardView.setOnClickListener(view -> {makePhoneCall(listTel[position]);});
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void makePhoneCall(String tel) {
        //Log.d("testMake", "testMake");
        intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));

        if (ContextCompat.checkSelfPermission(parent.getActivity(), Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED) {
            parent.startActivity(intent);
        } else {
            //Start dialog requesting permission
            ActivityCompat.requestPermissions(parent.getActivity(), new String[]{
                    Manifest.permission.CALL_PHONE
            }, REQUEST_PHONE_CALL);
        }
    }


    //called when dialog is finished
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResult) {
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                parent.startActivity(intent);
            }
        }
    }



}
