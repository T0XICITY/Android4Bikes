package de.thu.tpro.android4bikes.services.weatherData.openWeather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {

    @SerializedName("temp")
    @Expose
    private Double temp;
    @SerializedName("feels_like")
    @Expose
    private Double feelsLike;
    @SerializedName("temp_min")
    @Expose
    private Double tempMin;
    @SerializedName("temp_max")
    @Expose
    private Double tempMax;
    @SerializedName("pressure")
    @Expose
    private Integer pressure;
    @SerializedName("sea_level")
    @Expose
    private Integer seaLevel;
    @SerializedName("grnd_level")
    @Expose
    private Integer grndLevel;
    @SerializedName("humidity")
    @Expose
    private Integer humidity;
    @SerializedName("temp_kf")
    @Expose
    private Integer tempKf;

    /**
     * No args constructor for use in serialization
     */
    public Main() {
    }

    /**
     * @param feelsLike
     * @param tempMax
     * @param temp
     * @param seaLevel
     * @param humidity
     * @param pressure
     * @param tempKf
     * @param grndLevel
     * @param tempMin
     */
    public Main(Double temp, Double feelsLike, Double tempMin, Double tempMax, Integer pressure, Integer seaLevel, Integer grndLevel, Integer humidity, Integer tempKf) {
        super();
        this.temp = temp;
        this.feelsLike = feelsLike;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.pressure = pressure;
        this.seaLevel = seaLevel;
        this.grndLevel = grndLevel;
        this.humidity = humidity;
        this.tempKf = tempKf;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Main withTemp(Double temp) {
        this.temp = temp;
        return this;
    }

    public Double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(Double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public Main withFeelsLike(Double feelsLike) {
        this.feelsLike = feelsLike;
        return this;
    }

    public Double getTempMin() {
        return tempMin;
    }

    public void setTempMin(Double tempMin) {
        this.tempMin = tempMin;
    }

    public Main withTempMin(Double tempMin) {
        this.tempMin = tempMin;
        return this;
    }

    public Double getTempMax() {
        return tempMax;
    }

    public void setTempMax(Double tempMax) {
        this.tempMax = tempMax;
    }

    public Main withTempMax(Double tempMax) {
        this.tempMax = tempMax;
        return this;
    }

    public Integer getPressure() {
        return pressure;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

    public Main withPressure(Integer pressure) {
        this.pressure = pressure;
        return this;
    }

    public Integer getSeaLevel() {
        return seaLevel;
    }

    public void setSeaLevel(Integer seaLevel) {
        this.seaLevel = seaLevel;
    }

    public Main withSeaLevel(Integer seaLevel) {
        this.seaLevel = seaLevel;
        return this;
    }

    public Integer getGrndLevel() {
        return grndLevel;
    }

    public void setGrndLevel(Integer grndLevel) {
        this.grndLevel = grndLevel;
    }

    public Main withGrndLevel(Integer grndLevel) {
        this.grndLevel = grndLevel;
        return this;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Main withHumidity(Integer humidity) {
        this.humidity = humidity;
        return this;
    }

    public Integer getTempKf() {
        return tempKf;
    }

    public void setTempKf(Integer tempKf) {
        this.tempKf = tempKf;
    }

    public Main withTempKf(Integer tempKf) {
        this.tempKf = tempKf;
        return this;
    }

    @Override
    public String toString() {
        return "Main{" +
                "temp=" + temp +
                ", feelsLike=" + feelsLike +
                ", tempMin=" + tempMin +
                ", tempMax=" + tempMax +
                ", pressure=" + pressure +
                ", seaLevel=" + seaLevel +
                ", grndLevel=" + grndLevel +
                ", humidity=" + humidity +
                ", tempKf=" + tempKf +
                '}';
    }
}
