package de.thu.tpro.android4bikes.view.menu.showProfile;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.Profile;
import de.thu.tpro.android4bikes.util.GlobalContext;
import de.thu.tpro.android4bikes.view.MainActivity;
import de.thu.tpro.android4bikes.viewmodel.ViewModelOwnProfile;
import petrov.kristiyan.colorpicker.ColorPicker;


public class FragmentShowProfile extends Fragment implements Observer<Profile> {

    private ViewModelOwnProfile vmProfile;

    private MainActivity parent;

    private TextInputEditText nameEdit;
    private TextInputEditText emailEdit;
    private ImageView imageViewCircle;

    private TextView tvTracks;
    private ImageView ivKeyboard;

    private Button delete;
    private Button dialogColorPicker;
    private ImageView iv_a1, iv_a2, iv_a3, iv_a4, iv_a5, iv_a6, iv_a7, iv_a8, iv_a9;

    // TODO -> Implement Achievments
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_profile, container, false);

        vmProfile = new ViewModelProvider(requireActivity()).get(ViewModelOwnProfile.class);
        vmProfile.getMyProfile().observe(getViewLifecycleOwner(), this);


        //Name & Email
        nameEdit = view.findViewById(R.id.edit_Name_text);
        emailEdit = view.findViewById(R.id.edit_Email_text);

        // if there's an existing profile
        Profile currentProfile = vmProfile.getMyProfile().getValue();
        if (currentProfile != null)
            onChanged(currentProfile);

        // Text auf grau stellen
        nameEdit.setTextColor(Color.GRAY);
        emailEdit.setTextColor(Color.GRAY);

        // Delete Button & ImageView vor Profile
        delete = view.findViewById(R.id.buttonDelete);
        delete.setOnClickListener(v -> confirmDeletion());
        imageViewCircle = view.findViewById(R.id.imageView_Circle);
        imageViewCircle.setImageBitmap(textToBitmap("M"));
        //ColorPicker
        dialogColorPicker = view.findViewById(R.id.button_ChangeProfileView);

        //Imageview for Achievements
        // TODO make a dynamic list instead of hard-coded
        iv_a1 = view.findViewById(R.id.iv1);
        iv_a2 = view.findViewById(R.id.iv2);
        iv_a3 = view.findViewById(R.id.iv3);
        iv_a4 = view.findViewById(R.id.iv4);
        iv_a5 = view.findViewById(R.id.iv5);
        iv_a6 = view.findViewById(R.id.iv6);
        iv_a7 = view.findViewById(R.id.iv7);
        iv_a8 = view.findViewById(R.id.iv8);
        iv_a9 = view.findViewById(R.id.iv9);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        parent = (MainActivity) getActivity();

        //If you press the ColorPicker
        dialogColorPicker.setOnClickListener(v -> openColorPicker());

        //If you press the ImageView at the Achievements
        iv_a1.setOnClickListener(v -> openAchievements("Achievement 1", "Hallo1"));
        iv_a2.setOnClickListener(v -> openAchievements("Achievement 2", "Hallo2"));
        iv_a3.setOnClickListener(v -> openAchievements("Achievement 3", "Hallo3"));
        iv_a4.setOnClickListener(v -> openAchievements("Achievement 4", "Hallo4"));
        iv_a5.setOnClickListener(v -> openAchievements("Achievement 5", "Hallo5"));
        iv_a6.setOnClickListener(v -> openAchievements("Achievement 6", "Hallo6"));
        iv_a7.setOnClickListener(v -> openAchievements("Achievement 7", "Hallo7"));
        iv_a8.setOnClickListener(v -> openAchievements("Achievement 8", "Hallo8"));
        iv_a9.setOnClickListener(v -> openAchievements("Achievement 9", "Hallo9"));

        //If you press the Cancel-Button
        delete.setOnClickListener(v -> openDialogDelete());
    }

    // Color Picker
    public void openColorPicker() {
        final ColorPicker colorPicker = new ColorPicker(getActivity());
        ArrayList<String> colors = new ArrayList<>();
        // TODO use values/colors.xml
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
                        imageViewCircle.setBackgroundColor(color);
                    }
                    @Override
                    public void onCancel() {/*remains empty*/}
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

    public void deleteMyProfile() {
        // TODO uncomment this line when we really want to mess around with profile deletion
        // vmProfile.deleteMyProfile();
        parent.goToLoginActivity();
    }

    public void confirmDeletion() {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(GlobalContext.getContext());
        dialogBuilder.setTitle(R.string.confirmation);
        dialogBuilder.setMessage(R.string.confirmation_message);
        dialogBuilder.setPositiveButton(R.string.delete, (dialogInterface, i) -> deleteMyProfile());
        dialogBuilder.setNegativeButton(R.string.cancel, null); // do nothing on cancel
    }

    @Override
    public void onChanged(Profile profile) {
        Log.d("PROFILE", "" + profile);
        if (profile!=null) {
            String fullName = String.format("%s %s", profile.getFirstName(), profile.getFamilyName());
            nameEdit.setText(fullName);
            // TODO: Load email address from profile -> reading from FirebaseAuth doesn't seem right
            emailEdit.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
    }
}
