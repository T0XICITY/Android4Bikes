package de.thu.tpro.android4bikes.view.menu.roadsideAssistance;

import java.util.ArrayList;
import java.util.List;

import de.thu.tpro.android4bikes.R;

/**
 * @author Elias,Stefanie
 */
public class ViewModelRoadsideAssistance {

    // Strings for TextView
    private final String[] institutionStrings = {"Ambulance service", "Fire department", "Police", "Roadride assistance"
            , "Youth Helpline", "Manual input"};

    // Image resources for ImageView
    private final Integer[] institutionImages ={R.drawable.ic_emergency__hospital,R.drawable.emergency_fire_department,
            R.drawable.emergency_police,R.drawable.emergency_roadside_assistance,R.drawable.ic_emergency_child,R.drawable.ic_emergency_phone};

    // Image resource for ImageButton
    private final Integer ib_call = R.drawable.ic_emergency__call;

    private List<RoadsideAssistanceEntry> entries;

    public ViewModelRoadsideAssistance() {
        // init entries list
        entries = new ArrayList<>();
        for (int i = 0; i< institutionStrings.length; i++) {
            entries.add(new RoadsideAssistanceEntry(
                    institutionStrings[i],
                    institutionImages[i],
                    ib_call)
            );
        }
    }

    public List<RoadsideAssistanceEntry> getEntries() {
        return entries;
    }
}
