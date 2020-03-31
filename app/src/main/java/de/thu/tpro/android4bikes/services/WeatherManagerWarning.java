package de.thu.tpro.android4bikes.services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import de.thu.tpro.android4bikes.services.weatherData.warning.DWDwarning;

public class WeatherManagerWarning extends Observable {
    private final static String string_url = "https://www.dwd.de/DWD/warnungen/warnapp/json/warnings.json";
    private static Set<DWDwarning> dwDwarnings;
    private static Gson gson;

    public WeatherManagerWarning() {
        dwDwarnings = new HashSet<>();
        gson = new Gson();
    }

    public void startWeatherTask(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Set<DWDwarning> newWarning = loadWeatherWarnings();
                if (newWarning.equals(dwDwarnings)){
                    dwDwarnings.clear();
                    dwDwarnings.addAll(loadWeatherWarnings());
                    setChanged();
                    notifyObservers();
                }
            }
        }, 10, 60000); //one minute
    }

    public Set<DWDwarning> getDWDwarnings(){
        return dwDwarnings;
    }

    private Set<DWDwarning> loadWeatherWarnings() {
        Set<DWDwarning> warningList = new HashSet<>();
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
