package de.thu.tpro.android4bikes.view.menu.showProfile;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.view.MainActivity;
import de.thu.tpro.android4bikes.view.info.FragmentInfoMode;

public class FragmentShowProfile extends Fragment {

    private TextInputEditText mtV;
    private TextInputEditText mBtn;
    private Calendar c;
    private DatePickerDialog dpd;
    private Button cancel;
    private Button logout;
    private Button confirm;
    private Button googleAccount;

    /**
     * Author: Elias
     * Dialog View -> shows Calendar
     * Button cancel --> closes Fragment & opens the Fragment from
     * <p>
     * TODO -> Implement Logout & Confirm logic
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_profile, container, false);
        mtV = view.findViewById(R.id.birthday_edit);
        mBtn = view.findViewById(R.id.birthday_edit);
        cancel = view.findViewById(R.id.buttonCancel);
        googleAccount = view.findViewById(R.id.buttonForGoogle);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DateInformation", "test");
                c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_MONTH);
                int month = c.get(Calendar.MONTH);
                int year = c.get(Calendar.YEAR);

                dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {

                        mtV.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);

                    }
                }, day, month, year);
                dpd.show();
            }
        });

        googleAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://accounts.google.com/ServiceLogin/signinchooser?service=accountsettings&hl=de&continue=https%3A%2F%2Fmyaccount.google.com%2Fintro&csig=AF-SEnYXmQ3kq7-JScVk%3A1586881730&flowName=GlifWebSignIn&flowEntry=ServiceLogin"));
                startActivity(browserIntent);
            }
        });


    }


}
