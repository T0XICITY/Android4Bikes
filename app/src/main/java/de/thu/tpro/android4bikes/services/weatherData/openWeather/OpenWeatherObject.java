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
    private java.util.List<de.thu.tpro.android4bikes.services.weatherData.openWeather.List> list;
    @SerializedName("city")
    @Expose
    private City city;

    /**
     * No args constructor for use in serialization
     */
    public OpenWeatherObject() {
        list = null;
    }

    /**
     * @param city
     * @param cnt
     * @param cod
     * @param message
     * @param list
     */
    public OpenWeatherObject(String cod, Integer message, Integer cnt, java.util.List<de.thu.tpro.android4bikes.services.weatherData.openWeather.List> list, City city) {
        super();
        this.cod = cod;
        this.message = message;
        this.cnt = cnt;
        this.list = null;
        this.list = list;
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

    public java.util.List<de.thu.tpro.android4bikes.services.weatherData.openWeather.List> getList() {
        return list;
    }

    public void setList(java.util.List<de.thu.tpro.android4bikes.services.weatherData.openWeather.List> list) {
        this.list = list;
    }

    public OpenWeatherObject withList(java.util.List<de.thu.tpro.android4bikes.services.weatherData.openWeather.List> list) {
        this.list = list;
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
                ", list=" + list.toString() +
                ", city=" + city.toString() +
                '}';
    }
}
