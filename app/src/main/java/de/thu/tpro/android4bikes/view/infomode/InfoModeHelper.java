package de.thu.tpro.android4bikes.view.infomode;

import android.content.res.Resources;
import android.location.Location;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.BikeRack;
import de.thu.tpro.android4bikes.data.model.Position;
import de.thu.tpro.android4bikes.services.PositionTracker;

public class InfoModeHelper {

    public static double calculateDistance(Position from, Position to) {
        Location fromLoc = new Location("");
        fromLoc.setLatitude(from.getLatitude());
        fromLoc.setLongitude(from.getLongitude());

        Location toLoc = new Location("");
        toLoc.setLatitude(to.getLatitude());
        toLoc.setLongitude(to.getLongitude());

        return fromLoc.distanceTo(toLoc) / 1000;
    }

    public static double calculateDistanceFromMe(Position to) {
        Position from = PositionTracker.getLastPosition();
        return calculateDistance(from, to);
    }

    public static String localizeCapacity(Resources resources, BikeRack.ConstantsCapacity capacity) {
        String[] capacityTexts = resources.getStringArray(R.array.rack_size);
        String preformatted = resources.getString(R.string.preformattedCapacity);
        return String.format(preformatted, capacityTexts[capacity.toInt()]);
    }

}
