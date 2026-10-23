package com.gathin.tvhome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class WeatherService {

    public interface WeatherCallback {
        void onWeatherLoaded(String text);
    }

    private static final String PREF_NAME = "weather_pref";
    private static final String KEY_TEXT = "weather_text";
    private static final String KEY_TIMESTAMP = "weather_timestamp";
    private static final long CACHE_DURATION_MS = 60 * 60 * 1000L; // 1 hour

    private static final String API_URL =
            "https://api.open-meteo.com/v1/forecast?latitude=-8.65&longitude=115.22&current=temperature_2m,weather_code&timezone=Asia%2FMakassar";

    private WeatherService() {}

    public static void loadWeather(Context context, WeatherCallback callback) {
        if (context == null || callback == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String cached = prefs.getString(KEY_TEXT, null);
        long lastTime = prefs.getLong(KEY_TIMESTAMP, 0);
        long now = System.currentTimeMillis();

        if (cached != null && !cached.isEmpty()) {
            callback.onWeatherLoaded(cached);
        } else {
            callback.onWeatherLoaded("⛅ 28°C · Cerah Berawan · Denpasar");
        }

        if (now - lastTime < CACHE_DURATION_MS && cached != null) {
            return; // Cache is still fresh
        }

        Handler mainHandler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(API_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setRequestProperty("User-Agent", "CleanYourTV/2.6");

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    JSONObject json = new JSONObject(sb.toString());
                    JSONObject current = json.getJSONObject("current");
                    double tempVal = current.getDouble("temperature_2m");
                    int temp = (int) Math.round(tempVal);
                    int code = current.getInt("weather_code");

                    String icon;
                    String desc;
                    if (code == 0) {
                        icon = "☀️";
                        desc = "Cerah";
                    } else if (code <= 3) {
                        icon = "⛅";
                        desc = "Cerah Berawan";
                    } else if (code == 45 || code == 48) {
                        icon = "🌫️";
                        desc = "Berkabut";
                    } else if ((code >= 51 && code <= 67) || (code >= 80 && code <= 82)) {
                        icon = "🌧️";
                        desc = "Hujan";
                    } else if (code >= 95) {
                        icon = "⛈️";
                        desc = "Hujan Petir";
                    } else {
                        icon = "⛅";
                        desc = "Cerah Berawan";
                    }

                    String formatted = icon + " " + temp + "°C · " + desc + " · Denpasar";
                    prefs.edit()
                            .putString(KEY_TEXT, formatted)
                            .putLong(KEY_TIMESTAMP, System.currentTimeMillis())
                            .apply();

                    mainHandler.post(() -> callback.onWeatherLoaded(formatted));
                }
            } catch (Exception e) {
                android.util.Log.e("WeatherService", "Fetch failed: " + e.getMessage(), e);
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}
