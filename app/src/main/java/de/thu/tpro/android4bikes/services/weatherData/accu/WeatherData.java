
package de.thu.tpro.android4bikes.services.weatherData.accu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherData {

    @SerializedName("DateTime")
    @Expose
    private String dateTime;
    @SerializedName("EpochDateTime")
    @Expose
    private Integer epochDateTime;
    @SerializedName("WeatherIcon")
    @Expose
    private Integer weatherIcon;
    @SerializedName("IconPhrase")
    @Expose
    private String iconPhrase;
    @SerializedName("HasPrecipitation")
    @Expose
    private Boolean hasPrecipitation;
    @SerializedName("IsDaylight")
    @Expose
    private Boolean isDaylight;
    @SerializedName("Temperature")
    @Expose
    private Temperature temperature;
    @SerializedName("PrecipitationProbability")
    @Expose
    private Integer precipitationProbability;
    @SerializedName("MobileLink")
    @Expose
    private String mobileLink;
    @SerializedName("Link")
    @Expose
    private String link;

    /**
     * No args constructor for use in serialization
     * 
     */
    public WeatherData() {
    }

    /**
     * 
     * @param dateTime
     * @param hasPrecipitation
     * @param precipitationProbability
     * @param isDaylight
     * @param weatherIcon
     * @param epochDateTime
     * @param temperature
     * @param link
     * @param iconPhrase
     * @param mobileLink
     */
    public WeatherData(String dateTime, Integer epochDateTime, Integer weatherIcon, String iconPhrase, Boolean hasPrecipitation, Boolean isDaylight, Temperature temperature, Integer precipitationProbability, String mobileLink, String link) {
        super();
        this.dateTime = dateTime;
        this.epochDateTime = epochDateTime;
        this.weatherIcon = weatherIcon;
        this.iconPhrase = iconPhrase;
        this.hasPrecipitation = hasPrecipitation;
        this.isDaylight = isDaylight;
        this.temperature = temperature;
        this.precipitationProbability = precipitationProbability;
        this.mobileLink = mobileLink;
        this.link = link;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public WeatherData withDateTime(String dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public Integer getEpochDateTime() {
        return epochDateTime;
    }

    public void setEpochDateTime(Integer epochDateTime) {
        this.epochDateTime = epochDateTime;
    }

    public WeatherData withEpochDateTime(Integer epochDateTime) {
        this.epochDateTime = epochDateTime;
        return this;
    }

    public Integer getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(Integer weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public WeatherData withWeatherIcon(Integer weatherIcon) {
        this.weatherIcon = weatherIcon;
        return this;
    }

    public String getIconPhrase() {
        return iconPhrase;
    }

    public void setIconPhrase(String iconPhrase) {
        this.iconPhrase = iconPhrase;
    }

    public WeatherData withIconPhrase(String iconPhrase) {
        this.iconPhrase = iconPhrase;
        return this;
    }

    public Boolean getHasPrecipitation() {
        return hasPrecipitation;
    }

    public void setHasPrecipitation(Boolean hasPrecipitation) {
        this.hasPrecipitation = hasPrecipitation;
    }

    public WeatherData withHasPrecipitation(Boolean hasPrecipitation) {
        this.hasPrecipitation = hasPrecipitation;
        return this;
    }

    public Boolean getIsDaylight() {
        return isDaylight;
    }

    public void setIsDaylight(Boolean isDaylight) {
        this.isDaylight = isDaylight;
    }

    public WeatherData withIsDaylight(Boolean isDaylight) {
        this.isDaylight = isDaylight;
        return this;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public WeatherData withTemperature(Temperature temperature) {
        this.temperature = temperature;
        return this;
    }

    public Integer getPrecipitationProbability() {
        return precipitationProbability;
    }

    public void setPrecipitationProbability(Integer precipitationProbability) {
        this.precipitationProbability = precipitationProbability;
    }

    public WeatherData withPrecipitationProbability(Integer precipitationProbability) {
        this.precipitationProbability = precipitationProbability;
        return this;
    }

    public String getMobileLink() {
        return mobileLink;
    }

    public void setMobileLink(String mobileLink) {
        this.mobileLink = mobileLink;
    }

    public WeatherData withMobileLink(String mobileLink) {
        this.mobileLink = mobileLink;
        return this;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public WeatherData withLink(String link) {
        this.link = link;
        return this;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "dateTime='" + dateTime + '\'' +
                ", epochDateTime=" + epochDateTime +
                ", weatherIcon=" + weatherIcon +
                ", iconPhrase='" + iconPhrase + '\'' +
                ", hasPrecipitation=" + hasPrecipitation +
                ", isDaylight=" + isDaylight +
                ", temperature=" + temperature.toString() +
                ", precipitationProbability=" + precipitationProbability +
                ", mobileLink='" + mobileLink + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
