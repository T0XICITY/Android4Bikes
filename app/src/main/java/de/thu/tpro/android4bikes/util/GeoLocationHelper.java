package de.thu.tpro.android4bikes.util;

import android.location.Address;
import android.location.Geocoder;

import java.util.List;
import java.util.Locale;

import de.thu.tpro.android4bikes.data.model.Position;

public class GeoLocationHelper {
    private static Geocoder geocoder;
    private static List<Address> list_addresses;

    /**
     * calculates the postalcode of a given position.
     *
     * @param position position to calculate the postcode from
     * @return postcode as a string or null if something went wrong.
     */
    public static String convertPositionToPostcode(Position position) {
        String postalcode = null;
        //compare: https://stackoverflow.com/questions/35022319/how-to-get-post-code-from-lat-and-long-android
        if (geocoder == null) {
            geocoder = new Geocoder(GlobalContext.getContext(), Locale.getDefault());
        }
        try {
            //list that consists of one result:
            list_addresses = geocoder.getFromLocation(position.getLatitude(), position.getLongitude(), 1);

            //get the postcode of the single element in the list of addresses
            postalcode = list_addresses.get(0).getPostalCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postalcode;
    }
}
