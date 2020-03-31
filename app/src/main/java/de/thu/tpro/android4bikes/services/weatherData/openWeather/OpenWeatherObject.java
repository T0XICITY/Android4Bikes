package de.thu.tpro.android4bikes.services.weatherData.openWeather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OpenWeatherObject {

    @SerializedName("cod")
    @Expose
    private String cod;
    @SerializedName("message")
    @Expose
    private Integer message;
    @SerializedName("cnt")
    @Expose
    private Integer cnt;
    @SerializedName("list")
    @Expose
    private java.util.List<ForecastList> forecastList;
    @SerializedName("city")
    @Expose
    private City city;

    /**
     * No args constructor for use in serialization
     */
    public OpenWeatherObject() {
        forecastList = null;
    }

    /**
     * @param city
     * @param cnt
     * @param cod
     * @param message
     * @param forecastList
     */
    public OpenWeatherObject(String cod, Integer message, Integer cnt, java.util.List<ForecastList> forecastList, City city) {
        super();
        this.cod = cod;
        this.message = message;
        this.cnt = cnt;
        this.forecastList = null;
        this.forecastList = forecastList;
        this.city = city;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public OpenWeatherObject withCod(String cod) {
        this.cod = cod;
        return this;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
    }

    public OpenWeatherObject withMessage(Integer message) {
        this.message = message;
        return this;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public OpenWeatherObject withCnt(Integer cnt) {
        this.cnt = cnt;
        return this;
    }

    public java.util.List<ForecastList> getForecastList() {
        return forecastList;
    }

    public void setForecastList(java.util.List<ForecastList> forecastList) {
        this.forecastList = forecastList;
    }

    public OpenWeatherObject withList(java.util.List<ForecastList> forecastList) {
        this.forecastList = forecastList;
        return this;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public OpenWeatherObject withCity(City city) {
        this.city = city;
        return this;
    }

    @Override
    public String toString() {
        return "OpenWeatherObject{" +
                "cod='" + cod + '\'' +
                ", message=" + message +
                ", cnt=" + cnt +
                ", list=" + forecastList.toString() +
                ", city=" + city.toString() +
                '}';
    }
}
