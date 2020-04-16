package de.thu.tpro.android4bikes.data.commands;

import de.thu.tpro.android4bikes.database.CouchDBHelper;

public class SearchForHazardAlertsWithPostalCodeInLocalDB implements Command{
    private String postcode;

    public SearchForHazardAlertsWithPostalCodeInLocalDB(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public void execute() {
        new CouchDBHelper().readHazardAlerts(postcode);
    }
}
