package de.thu.tpro.android4bikes.data.model;

import com.google.rpc.Help;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.thu.tpro.android4bikes.database.JsonRepresentation;
import de.thu.tpro.android4bikes.exception.InvalidJsonException;

public class RawGeopos implements JsonRepresentation {
    public enum ConstantsRawGeoPos{
        POSITIONS("positions");

        private String type;

        ConstantsRawGeoPos(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }


    private List<Position> positions;

    public RawGeopos(){
        positions = new LinkedList<>();
    }

    public void addPosition(Position position){
        if(position!=null){
            positions.add(position);
        }
    }

    @Override
    public JSONObject toJSON() throws InvalidJsonException {
        return null;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map_positions = new HashMap<>();
        List<Map<String, Object>> list_position = new LinkedList<>();
        for(Position pos : positions){
            list_position.add(pos.toMap());
        }
        map_positions.put(ConstantsRawGeoPos.POSITIONS.toString(), list_position);
        return map_positions;
    }
}
