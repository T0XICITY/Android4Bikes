package de.thu.tpro.android4bikes.services;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.BeforeClass;
import org.junit.Test;

import de.thu.tpro.android4bikes.util.GlobalContext;

public class CreateDataForFirebase {
    private static Context context;

    @BeforeClass
    public static void init() {
        GlobalContext.setContext(ApplicationProvider.getApplicationContext());
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void generateDataForFirebase() {
        //todo
    }
}