package de.thu.tpro.android4bikes.data.model;

import org.json.JSONObject;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.database.Content;
import de.thu.tpro.android4bikes.util.GlobalContext;

public class HazardAlert implements Content {
    private HazardType type;

    public HazardAlert(HazardType type) {
        this.type = type;
    }

    public String getType() {
        return type.getType();
    }

    @Override
    public JSONObject getJsonRepresentation() {
        return null;
    }

    public enum HazardType{
        DAMAGED_ROAD(GlobalContext.getContext().getString(R.string.HazardAlert_HazardType_DamagedRoad)),
        ICY_ROAD(GlobalContext.getContext().getString(R.string.HazardAlert_HazardType_IcyRoad)),
        SLIPPERY_ROAD(GlobalContext.getContext().getString(R.string.HazardAlert_HazardType_SlipperyRoad)),
        ROADKILL(GlobalContext.getContext().getString(R.string.HazardAlert_HazardType_Roadkill)),
        ROCKFALL(GlobalContext.getContext().getString(R.string.HazardAlert_HazardType_Rockfall)),
        GENERAL(""); //todo

        private String type;
        HazardType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
