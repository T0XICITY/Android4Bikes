package de.thu.tpro.android4bikes.services.weatherData.accu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Temperature {

    @SerializedName("Value")
    @Expose
    private Double value;
    @SerializedName("Unit")
    @Expose
    private String unit;
    @SerializedName("UnitType")
    @Expose
    private Integer unitType;

    /**
     * No args constructor for use in serialization
     */
    public Temperature() {
    }

    /**
     * @param unitType
     * @param unit
     * @param value
     */
    public Temperature(Double value, String unit, Integer unitType) {
        super();
        this.value = value;
        this.unit = unit;
        this.unitType = unitType;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Temperature withValue(Double value) {
        this.value = value;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Temperature withUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public Integer getUnitType() {
        return unitType;
    }

    public void setUnitType(Integer unitType) {
        this.unitType = unitType;
    }

    public Temperature withUnitType(Integer unitType) {
        this.unitType = unitType;
        return this;
    }

    @Override
    public String toString() {
        return "Temperature{" +
                "value=" + value +
                ", unit='" + unit + '\'' +
                ", unitType=" + unitType +
                '}';
    }
}
