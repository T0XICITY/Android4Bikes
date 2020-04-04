package de.thu.tpro.android4bikes.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FineGrainedPositions)) return false;
        FineGrainedPositions that = (FineGrainedPositions) o;
        return getFirebaseID().equals(that.getFirebaseID()) &&
                getPositions().equals(that.getPositions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirebaseID(), getPositions());
    }

    @Override
    public String toString() {
        return "FineGrainedPositions{" +
                "firebaseID='" + firebaseID + '\'' +
                ", positions=" + positions +
                '}';
    }

    public enum ConstantsFineGrainedPosition {
        POSITIONS("positions"),
        FIREBASID("firebaseID");
        private String type;

        ConstantsFineGrainedPosition(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }
}
