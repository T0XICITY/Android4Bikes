package de.thu.tpro.android4bikes.view.menu.showProfile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.view.login.ActivityLogin;


public class DialogDeleteProfile extends AppCompatDialogFragment {

    /**
     * Author: Elias
     * <p>
     * Creating a Dialog for deleting the profile
     */


    private ImageView imageView;
    private TextView textView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_deleteprofile, container, false);

        textView = (TextView) view.findViewById(R.id.textviewTitle);
        textView.setText("Delete profile");


        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        //  builder.setTitle("Delete profile");
        //   builder.setMessage("Are you sure that you want to delete your profile?");




/*
        // Yes or No choise
        builder.setSingleChoiceItems(chooseSequence, 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean lokalBool;
                //if true -> you can delete account if you press the delete button
               if(which == 0){
                   Log.d("Wert: " + which,"Wert");
                    lokalBool = true;
                    setBool(lokalBool);


                }
                Log.d("Wert2: " + which,"Werte2");
                lokalBool = false;
                setBool(lokalBool);

            }
         });
*/

        // Cancel
        builder.setPositiveButton("No cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //remain Empty
            }
        });


        // Button Delete
        builder.setNegativeButton("Yes delete it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Logoutlogic -> TODO -> to delete the Profile -> at the moment: only a logout

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), ActivityLogin.class);
                startActivity(intent);

            }
        });

        //round edges
        builder.setBackground(getResources().getDrawable(R.drawable.alert_dialog_deleteprofile, null));
        builder.setView(R.layout.dialog_deleteprofile);
        return builder.create();
    }

    /*
    public boolean setBool(boolean bb){
       this.bool = bb;
        return this.bool;
    }

    public boolean getBool(){
        return this.bool;
    }

*/

}
