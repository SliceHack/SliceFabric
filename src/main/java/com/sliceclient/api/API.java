package com.sliceclient.api;

import com.sliceclient.util.HardwareUtil;
import com.sliceclient.util.LoggerUtil;
import net.minecraft.client.MinecraftClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Client API communication
 *
 * @author Nick & Dylan
 * **/
public class API {

    /** API url */
    private static final String API_URL = "https://api.sliceclient.com/";

    /**
     * Checks if a user is authenticated with the server
     * **/
    @SuppressWarnings("all")
    public static void sendAuthRequest(IRC irc) {
        try {
            URL url = new URL(API_URL + "checkAuth/" + HardwareUtil.getHardwareID());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.connect();
            String response = readResponse(connection);

            JSONObject json = null;
            if(response != null) {
                json = new JSONObject(response);
            }

            if(json != null) {
                boolean success = json.getBoolean("status");

                if (!success) {
                    LoggerUtil.addTerminalMessage("[Slice] Authentication failed");
                    System.exit(-1);
                }
                return;
            }
            LoggerUtil.addTerminalMessage("[Slice] Authentication failed");
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Reads the response from the server
     *
     * @param connection the connection to the server
     * **/
    public static String readResponse(HttpURLConnection connection) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (Exception ignored) {}
        return null;
    }
}
