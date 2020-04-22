package de.thu.tpro.android4bikes.view.menu.showProfile;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Layout;
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

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.view.MainActivity;
import de.thu.tpro.android4bikes.view.info.FragmentInfoMode;
import de.thu.tpro.android4bikes.view.login.ActivityLogin;
import petrov.kristiyan.colorpicker.ColorPicker;


public class FragmentShowProfile extends Fragment {

    private TextInputEditText nameEdit;
    private TextInputEditText emailEdit;
    private ImageView imageViewCircle;
    private MaterialCardView materialCardView;

    private Button delete;
    private Button dialogColorPicker;
    private ImageView iv_a1, iv_a2, iv_a3, iv_a4, iv_a5, iv_a6, iv_a7, iv_a8, iv_a9;

    /**
     * Author: Elias
     * Dialog View -> shows Dialog to delete the Profile
     * ColorPicker from a Library
     *
     * <p>
     * TODO -> Implement Track & Achievments
     *
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

        //Name & Email
        nameEdit = (TextInputEditText) view.findViewById(R.id.edit_Name_text);
        emailEdit = (TextInputEditText) view.findViewById(R.id.edit_Email_text);


        nameEdit.setText("Max Mustermann");
        nameEdit.setTextColor(Color.GRAY); // Text auf Graustellen
        emailEdit.setText("max.mustermann@gmail.com");
        emailEdit.setTextColor(Color.GRAY);     //Text auf Graustellen

        // Delete Button & ImageView vor Profile
        delete = (Button) view.findViewById(R.id.buttonDelete);
        imageViewCircle = (ImageView) view.findViewById(R.id.imageView_Circle);
        imageViewCircle.setImageBitmap(textToBitmap("M"));
        materialCardView = (MaterialCardView) view.findViewById(R.id.mater_card);
        //ColorPicker
        dialogColorPicker = (Button) view.findViewById(R.id.button_ChangeProfileView);

        //Imageview for Achievements
        iv_a1 = (ImageView) view.findViewById(R.id.iv1);
        iv_a2 = (ImageView) view.findViewById(R.id.iv2);
        iv_a3 = (ImageView) view.findViewById(R.id.iv3);
        iv_a4 = (ImageView) view.findViewById(R.id.iv4);
        iv_a5 = (ImageView) view.findViewById(R.id.iv5);
        iv_a6 = (ImageView) view.findViewById(R.id.iv6);
        iv_a7 = (ImageView) view.findViewById(R.id.iv7);
        iv_a8 = (ImageView) view.findViewById(R.id.iv8);
        iv_a9 = (ImageView) view.findViewById(R.id.iv9);
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

        //If you press the ImageView at the Achievements
        iv_a1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAchievements("Achievement 1", "Hallo1");
            }
        });

        iv_a2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAchievements("Achievement 2", "Hallo2");
            }
        });

        iv_a3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAchievements("Achievement 3", "Hallo3");
            }
        });

        iv_a4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAchievements("Achievement 4", "Hallo4");
            }
        });

        iv_a5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAchievements("Achievement 5", "Hallo5");
            }
        });

        iv_a6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAchievements("Achievement 6", "Hallo6");
            }
        });

        iv_a7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAchievements("Achievement 7", "Hallo7");
            }
        });

        iv_a8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAchievements("Achievement 8", "Hallo8");
            }
        });

        iv_a9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAchievements("Achievement 9", "Hallo9");
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
                        Log.d("positionHallo:", "posi:" + color);
                        //  materialCardView.setCardBackgroundColor(color);
                        imageViewCircle.setBackgroundColor(color);


                        //imageViewCircle.setColorFilter(color); //-> ändert Initialenfarbe
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


    //Text in ImageView
    private Bitmap textToBitmap(String text) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(20);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);

        float baseline = -paint.ascent();
        int width = (int) (paint.measureText(text) + 14f);
        int height = (int) (baseline + paint.descent() + 15f);
        Bitmap imageB = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(imageB);
        canvas.drawText(text, 0, baseline, paint);
        return imageB;
    }

    //open Achievement
    public void openAchievements(String title, String message) {
        DialogAchievements da = new DialogAchievements();
        da.setText(title, message);
        da.show(getParentFragmentManager(), "Open Achievements");
    }




























}
