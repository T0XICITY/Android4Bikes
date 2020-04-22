package de.thu.tpro.android4bikes.data.commands;

import de.thu.tpro.android4bikes.firebase.FirebaseConnection;

public class SearchForTracksWithPostalCodeInFireStore implements Command {
    private String postcode;

    public SearchForTracksWithPostalCodeInFireStore(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public void execute() {
        FirebaseConnection.getInstance().readTracksFromFireStoreAndStoreItToLocalDB(postcode);
    }
}
