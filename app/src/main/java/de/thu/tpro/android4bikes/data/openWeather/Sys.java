package de.thu.tpro.android4bikes.data.openWeather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sys {

    @SerializedName("pod")
    @Expose
    private String pod;

    /**
     * No args constructor for use in serialization
     */
    public Sys() {
    }

    /**
     * @param pod
     */
    public Sys(String pod) {
        super();
        this.pod = pod;
    }

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }

    public Sys withPod(String pod) {
        this.pod = pod;
        return this;
    }

    @Override
    public String toString() {
        return "Sys{" +
                "pod='" + pod + '\'' +
                '}';
    }
}
