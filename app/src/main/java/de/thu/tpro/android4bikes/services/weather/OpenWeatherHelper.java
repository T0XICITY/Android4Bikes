package de.thu.tpro.android4bikes.services.weather;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.thu.tpro.android4bikes.data.openWeather.OpenWeatherObject;

public class OpenWeatherHelper {
    private static final String appID = "27d59ee2b5c90cf9beed248c6b4ef026";

    public OpenWeatherHelper() {
    }

    private URL getPreparedUrl(double latitude, double longitude) {
        URL url = null;
        try {
            String sb = "https://api.openweathermap.org/data/2.5/forecast" +
                    "?lat=" + latitude +
                    "&lon=" + longitude +
                    "&units=metric" +
                    "&appid=" + appID;
            url = new URL(sb);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public OpenWeatherObject createOpenWeatherObject(double latitude, double longitude) {
        OpenWeatherObject weatherObject = null;
        try {
            URL url = getPreparedUrl(latitude, longitude);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuffer sb = new StringBuffer();
            String tmp = br.readLine();
            while (tmp != null) {
                sb.append(tmp);
                tmp = br.readLine();
            }
            conn.disconnect();
            String jsonString = sb.toString();
            JsonObject jsonObject_weatherData = new JsonParser().parse(jsonString).getAsJsonObject();
            weatherObject = new Gson().fromJson(jsonObject_weatherData, OpenWeatherObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weatherObject;
    }
}
