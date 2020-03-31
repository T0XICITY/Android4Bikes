package de.thu.tpro.android4bikes.data.model;

public class Rating {
    private int difficulty;
    private int fun;
    private int roadquality;

    /**
     * no-arg Constructor needed for Firebase auto-cast
     */
    public Rating() {
    }

    public Rating(int difficulty, int fun, int roadquality, String firebaseID) {
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
