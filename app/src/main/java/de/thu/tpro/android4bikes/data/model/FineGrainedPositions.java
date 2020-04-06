package de.thu.tpro.android4bikes.data.model;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.thu.tpro.android4bikes.database.JsonRepresentation;
import de.thu.tpro.android4bikes.exception.InvalidJsonException;

public class FineGrainedPositions implements JsonRepresentation {
    private String firebaseID;
    private List<Position> positions;

    /**
     * no-arg Constructor needed for Firebase auto-cast
     */
    public FineGrainedPositions() {
        positions = new LinkedList<>();
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public void addPosition(Position position) {
        if (position != null) {
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
        for (Position pos : positions) {
            list_position.add(pos.toMap());
        }
        map_positions.put(ConstantsFineGrainedPosition.POSITIONS.toString(), positions);
        map_positions.put(ConstantsFineGrainedPosition.POSITIONS.toString(), list_position);
        return map_positions;
    }

    public enum ConstantsFineGrainedPosition {
        POSITIONS("positions");
        private String type;

        ConstantsFineGrainedPosition(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }
}
