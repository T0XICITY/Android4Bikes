package de.thu.tpro.android4bikes.view.menu.showProfile;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import de.thu.tpro.android4bikes.R;

public class DialogAchievements extends AppCompatDialogFragment {


    private String title;
    private String message;

    private MaterialTextView tv_achi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);


        return view;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setBackground(getResources().getDrawable(R.drawable.alert_dialog_deleteprofile, null));
        return builder.create();
    }

    public void setText(String title, String message) {
        this.title = title;
        this.message = message;
    }

}
