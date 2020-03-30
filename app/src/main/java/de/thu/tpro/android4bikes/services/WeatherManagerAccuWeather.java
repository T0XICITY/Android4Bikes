package de.thu.tpro.android4bikes.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.thu.tpro.android4bikes.services.weatherData.accu.AccuWeatherObject;

public class WeatherManagerAccuWeather {
    private final static String apiKey = "5A1Jts43flTKLIHNYHTvypubtJScdGYR";
    private final static String language = "de-de";

    public WeatherManagerAccuWeather() {

    }

    public AccuWeatherObject createAccuWeatherObject(double latitude, double longitude) {
        URL url = getPreparedKeyUrl(latitude, longitude);
        int key = -1;
        if (url != null) {
            key = getKeyFromAPI(url);
            if (key != -1) {
                return getWeatherData(key);
            }
        }
        return null;
    }

    private URL getPreparedKeyUrl(double latitude, double longitude) {
        URL url = null;
        try {
            String sb = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search" +
                    "?apikey=" + apiKey +
                    "&q=" + latitude + "%2C" + longitude +
                    "&language=" + language;
            url = new URL(sb);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private int getKeyFromAPI(URL url) {
        int key = -1;
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String tmp = br.readLine();
            while (tmp != null) {
                sb.append(tmp);
                tmp = br.readLine();
            }
            conn.disconnect();
            String jsonString = sb.toString();
            JsonObject jsonObject_weatherData = new JsonParser().parse(jsonString).getAsJsonObject(); // todo
            key = jsonObject_weatherData.get("Key").getAsInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    private AccuWeatherObject getWeatherData(int key) {
        AccuWeatherObject weatherObject = null;
        try {
            String string_url = "http://dataservice.accuweather.com/forecasts/v1/hourly/12hour/" + key +
                    "?apikey=" + apiKey +
                    "&metric=" + "true" +
                    "&language=" + language;
            URL url = new URL(string_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String tmp = br.readLine();
            sb.append("{weatherData:");
            while (tmp != null) {
                sb.append(tmp);
                tmp = br.readLine();
            }
            conn.disconnect();
            sb.append("}");
            String jsonString = sb.toString();
            JsonObject jsonObject_weatherData = new JsonParser().parse(jsonString).getAsJsonObject(); //todo
            weatherObject = new Gson().fromJson(jsonObject_weatherData, AccuWeatherObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weatherObject;
    }
    /*
    Success Response Code	Response Name	Description
    200	OK	Request was fulfilled
    Error Response Code	Response Name	Description
    400	Bad Request	Request had bad syntax or the parameters supplied were invalid
    403	Forbidden	Valid API Key was not supplied in the query
    404	Not Found	Server has not found a route matching the given URI
    409	Service Unavailable	The server is currently unavailable
    500	Internal Error	Server encountered an unexpected condition which prevented it from fulfilling the request
    503	Conflict	The allowed number of requests has been exceeded
     */
}
