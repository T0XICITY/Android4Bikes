package de.thu.tpro.android4bikes.view.menu.roadsideAssistance;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.crypto.spec.RC2ParameterSpec;

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
    private Activity context;
    private Intent intent;

    public RoadsideAssistanceListAdapter(Activity context, List<RoadsideAssistanceEntry> entries, String[] paraListTel) {
        super();

        this.context = context;
        this.entries = entries;
        this.inflater = LayoutInflater.from(context);
        this.listTel = paraListTel;
    }

    @NonNull
    @Override
    public RoadsideAssistanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = inflater.inflate(R.layout.cardview_emergency_number, null);
        return new RoadsideAssistanceViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RoadsideAssistanceViewHolder holder, int position) {

        // Insert Data into elements from view holder
        holder.iv_institution.setImageResource(entries.get(position).resId_institution);
        holder.tv_institution.setText(entries.get(position).text_institution);
        holder.ib_call.setImageResource(entries.get(position).resId_call);

        holder.ib_call.setOnClickListener(view -> {makePhoneCall(listTel[position]);});
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void makePhoneCall(String tel) {
        Log.d("testMake", "testMake");
        intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));


        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED) {
            Log.d("test", context.toString());
            context.startActivity(intent);
        } else {
            //Start dialog requesting permission
            ActivityCompat.requestPermissions(context, new String[]{
                    Manifest.permission.CALL_PHONE
            }, REQUEST_PHONE_CALL);
        }
    }


    //called when dialog is finished
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResult) {
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                context.startActivity(intent);
            }
        }
    }



}
