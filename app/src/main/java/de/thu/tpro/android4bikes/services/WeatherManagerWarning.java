package de.thu.tpro.android4bikes.services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.thu.tpro.android4bikes.services.weatherData.warning.DWDwarning;

public class WeatherManagerWarning {
    private final static String string_url = "https://www.dwd.de/DWD/warnungen/warnapp/json/warnings.json";
    private Gson gson;

    public WeatherManagerWarning() {
        gson = new Gson();
    }

    public List<DWDwarning> loadWeatherWarnings() {
        List<DWDwarning> warningList = new ArrayList<>();
        try {
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
            while (tmp != null) {
                sb.append(tmp);
                tmp = br.readLine();
            }
            conn.disconnect();
            String result = sb.toString();
            result = result.replace("warnWetter.loadWarnings(", "");
            result = result.substring(0, result.length() - 2);
            JsonObject json_warning = new JsonParser().parse(result).getAsJsonObject();
            json_warning = json_warning.get("warnings").getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : json_warning.entrySet()) {
                warningList.add(gson.fromJson(json_warning.get(entry.getKey()).getAsJsonArray().get(0).getAsJsonObject(), DWDwarning.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return warningList;
    }
}
