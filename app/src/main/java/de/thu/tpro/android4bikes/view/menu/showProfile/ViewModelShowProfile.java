package de.thu.tpro.android4bikes.view.menu.showProfile;

import android.app.AppComponentFactory;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;

import de.thu.tpro.android4bikes.R;

/**
 * Author: Elias
 * Target: Get a Calendar by Clicking on the Birthday-Image
 */


public class ViewModelShowProfile extends BaseAdapter {

    private TextView mtV;
    private Button mBtn;
    private Calendar c;
    private DatePickerDialog dpd;


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        return null;
    }
}
