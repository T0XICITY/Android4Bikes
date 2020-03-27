package de.thu.tpro.android4bikes.database;


import android.content.Context;

import com.couchbase.lite.CouchbaseLite;

import de.thu.tpro.android4bikes.util.GlobalContext;

public class CouchDB implements Andoid4BikeDatabse{
    private static CouchDB instance;
    private CouchDB(Context context){
        CouchbaseLite.init(context);
    }

    public static CouchDB getInstance(){
        if (instance == null){
            instance = new CouchDB(GlobalContext.getContext());
        }
        return instance;
    }

    @Override
    public void prepareDatabase() {

    }
}
