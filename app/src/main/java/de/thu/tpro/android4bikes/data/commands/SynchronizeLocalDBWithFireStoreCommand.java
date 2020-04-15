package de.thu.tpro.android4bikes.data.commands;

import de.thu.tpro.android4bikes.database.CouchDBHelper;

public class SynchronizeLocalDBWithFireStoreCommand implements Command {
    @Override
    public void execute() {
        new CouchDBHelper().synchronizeDataWithFireStore();
    }
}
