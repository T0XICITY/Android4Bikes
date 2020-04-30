package de.thu.tpro.android4bikes.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Rating {
    @Expose
    @SerializedName("difficulty")
    private int difficulty;
    @Expose
    @SerializedName("fun")
    private int fun;
    @Expose
    @SerializedName("roadquality")
    private int roadquality;

    /**
     * no-arg Constructor needed for Firebase auto-cast
     */
    public Rating() {
    }

    public Rating(int difficulty, int fun, int roadquality) {
        this.difficulty = difficulty;
        this.fun = fun;
        this.roadquality = roadquality;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getFun() {
        return fun;
    }

    public void setFun(int fun) {
        this.fun = fun;
    }

    public int getRoadquality() {
        return roadquality;
    }

    public void setRoadquality(int roadquality) {
        this.roadquality = roadquality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rating)) return false;
        Rating rating = (Rating) o;
        return getDifficulty() == rating.getDifficulty() &&
                getFun() == rating.getFun() &&
                getRoadquality() == rating.getRoadquality();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDifficulty(), getFun(), getRoadquality());
    }

    @Override
    public String toString() {
        return "Rating{" +
                "difficulty=" + difficulty +
                ", fun=" + fun +
                ", roadquality=" + roadquality +
                '}';
    }

    public enum ConstantsRating {
        DIFFICULTY("difficulty"),
        FUN("fun"),
        ROADQUALITY("roadquality");

        private String type;

        ConstantsRating(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }
}
