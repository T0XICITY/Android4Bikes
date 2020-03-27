package de.thu.tpro.android4bikes.util;

import android.annotation.SuppressLint;
import android.content.Context;

public class GlobalContext {
    @SuppressLint("StaticFieldLeak")
    private static Context globalContext;

    public static Context getContext() {
        if (globalContext == null) {
            throw new RuntimeException("There was no context set at the GlobalContext");
        }
        return globalContext;
    }

    public static void setContext(Context context) {
        globalContext = context;
    }
}
