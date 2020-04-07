package de.thu.tpro.android4bikes.view.menu.roadsideAssistance;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.R;

/**
 * @author Elias, Stefanie
 */
public class ViewModelRoadsideAssistance {


    // Strings for TextView
    private final String[] institutionStrings;

    // Image resources for ImageView
    private final Integer[] institutionImages = {
            R.drawable.ic_emergency__hospital,
            R.drawable.emergency_fire_department,
            R.drawable.emergency_police,
            R.drawable.emergency_roadside_assistance,
            R.drawable.ic_emergency_child,
            R.drawable.ic_emergency_phone
    };

    private final Integer[] callImage = {
            R.drawable.ic_emergency__call,
            R.drawable.ic_emergency__call,
            R.drawable.ic_emergency__call,
            R.drawable.ic_emergency__call,
            R.drawable.ic_emergency__call,
            R.drawable.ic_emergency__call

    };


    // Image resource for ImageButton
    // private final Integer ib_call = R.drawable.ic_emergency__call;

    private List<RoadsideAssistanceEntry> entries;
    private List<ListRoadAssistance> callEntries;

    public ViewModelRoadsideAssistance(String[] institutionStrings) {

        this.institutionStrings = institutionStrings;
    }

    public ViewModelRoadsideAssistance(Resources res) {
        // load strings from XML
        institutionStrings = res.getStringArray(R.array.roadside_assistance_list);

        // init entries list
        entries = new ArrayList<>();
        callEntries = new ArrayList<>();
        for (int i = 0; i < institutionStrings.length; i++) {
            entries.add(new RoadsideAssistanceEntry(
                    institutionStrings[i],
                    institutionImages[i],
                    callImage[i])
            );
        }
    }

    public List<RoadsideAssistanceEntry> getEntries() {
        return entries;
    }

    public Integer[] getCallImage() {
        return callImage;
    }


}

