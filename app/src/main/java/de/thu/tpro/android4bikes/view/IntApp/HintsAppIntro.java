package de.thu.tpro.android4bikes.view.IntApp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroCustomLayoutFragment;
import com.github.appintro.AppIntroFragment;


import de.thu.tpro.android4bikes.R;

public class HintsAppIntro extends AppIntro {


    @Nullable

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.appintro_hint, container, false);

        return view;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hints Google
        addSlide(new AppIntroCustomLayoutFragment().newInstance(R.layout.appintro_googlehint));
        //Hints Driving
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.appintro_hint));

    }


    @Override
    protected void onSkipPressed(@org.jetbrains.annotations.Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    protected void onDonePressed(@org.jetbrains.annotations.Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }
}
