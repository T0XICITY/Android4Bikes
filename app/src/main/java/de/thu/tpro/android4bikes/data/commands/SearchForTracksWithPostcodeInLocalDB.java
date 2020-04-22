package de.thu.tpro.android4bikes.data.commands;


import de.thu.tpro.android4bikes.database.CouchDBHelper;

public class SearchForTracksWithPostcodeInLocalDB implements Command {
    private String postcode;

    public SearchForTracksWithPostcodeInLocalDB(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public void execute() {
        new CouchDBHelper().readTracks();
    }
}
