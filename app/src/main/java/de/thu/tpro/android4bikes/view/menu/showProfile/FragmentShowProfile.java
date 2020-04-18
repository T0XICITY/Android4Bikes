package de.thu.tpro.android4bikes.view.menu.showProfile;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.DialogFragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.view.MainActivity;
import de.thu.tpro.android4bikes.view.info.FragmentInfoMode;
import de.thu.tpro.android4bikes.view.login.ActivityLogin;
import petrov.kristiyan.colorpicker.ColorPicker;


public class FragmentShowProfile extends Fragment {

    private TextInputEditText nameEdit;
    private TextInputEditText emailEdit;
    private ImageView imageViewCircle;
    private ImageView imageViewCircleBar;

    private Button delete;
    private Button confirm;
    private Button dialogColorPicker;



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

        nameEdit = (TextInputEditText) view.findViewById(R.id.edit_Name_text);
        emailEdit = (TextInputEditText) view.findViewById(R.id.edit_Email_text);
        nameEdit.setText("Max Mustermann");
        emailEdit.setText("max.mustermann@gmail.com");

        delete = (Button) view.findViewById(R.id.buttonDelete);


        imageViewCircle = (ImageView) view.findViewById(R.id.imageView_Circle);
        imageViewCircleBar = (ImageView) view.findViewById(R.id.imageView_profile);
        dialogColorPicker = (Button) view.findViewById(R.id.button_ChangeProfileView);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialogColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        //If you press the Cancel-Button
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogDelete();

            }
        });


    }

    // Color Picker
    public void openColorPicker() {
        final ColorPicker colorPicker = new ColorPicker(getActivity());
        ArrayList<String> colors = new ArrayList<>();
        colors.add("#82B926");
        colors.add("#a276eb");
        colors.add("#6a3ab2");
        colors.add("#666666");
        colors.add("#FFFF00");
        colors.add("#3C8D2F");
        colors.add("#FA9F00");
        colors.add("#FF0000");
        colors.add("#000088");
        colors.add("#ffc0cb");

        colorPicker
                .setDefaultColorButton(Color.parseColor("#f84c44"))
                .setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        Log.d("position", "" + position);
                        // will be fired only when OK button was tapped

                        imageViewCircle.setBackgroundColor(color);          //Profile
                    }

                    @Override
                    public void onCancel() {
                        // remains empty
                    }
                })
                .show();

    }

    //Dialog to delete your Profile
    public void openDialogDelete() {
        DialogDeleteProfile ddp = new DialogDeleteProfile();
        ddp.show(getParentFragmentManager(), "Delete Profiletag");

    }


}
