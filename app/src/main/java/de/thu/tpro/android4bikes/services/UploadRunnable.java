package de.thu.tpro.android4bikes.services;

import de.thu.tpro.android4bikes.database.CouchDBHelper;

public class UploadRunnable implements Runnable {
    @Override
    public void run() {
        CouchDBHelper localDBWriteBuffer = new CouchDBHelper(CouchDBHelper.DBMode.WRITEBUFFER);
        CouchDBHelper localDBDeleteBuffer = new CouchDBHelper(CouchDBHelper.DBMode.DELETEBUFFER);

        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
