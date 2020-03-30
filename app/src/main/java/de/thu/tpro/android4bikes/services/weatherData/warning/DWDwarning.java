
package de.thu.tpro.android4bikes.services.weatherData.warning;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DWDwarning {

    @SerializedName("regionName")
    @Expose
    private String regionName;
    @SerializedName("start")
    @Expose
    private Integer start;
    @SerializedName("end")
    @Expose
    private Integer end;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("level")
    @Expose
    private Integer level;
    @SerializedName("stateShort")
    @Expose
    private String stateShort;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("event")
    @Expose
    private String event;
    @SerializedName("headline")
    @Expose
    private String headline;
    @SerializedName("instruction")
    @Expose
    private String instruction;
    @SerializedName("altitudeStart")
    @Expose
    private Object altitudeStart;
    @SerializedName("altitudeEnd")
    @Expose
    private Object altitudeEnd;

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getStateShort() {
        return stateShort;
    }

    public void setStateShort(String stateShort) {
        this.stateShort = stateShort;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Object getAltitudeStart() {
        return altitudeStart;
    }

    public void setAltitudeStart(Object altitudeStart) {
        this.altitudeStart = altitudeStart;
    }

    public Object getAltitudeEnd() {
        return altitudeEnd;
    }

    public void setAltitudeEnd(Object altitudeEnd) {
        this.altitudeEnd = altitudeEnd;
    }

}
