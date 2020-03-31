package de.thu.tpro.android4bikes.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class FineGrainedPositions {
    @Expose
    @SerializedName("firebaseID")
    private String firebaseID;
    @Expose
    @SerializedName("positions")
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
