package de.neiox.utls;

import io.github.cdimascio.dotenv.Dotenv;
import kong.unirest.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Vars {
    private static Dotenv dotenv;

    static {
        try {
            dotenv = Dotenv.configure()
                    .directory("./") // Setting directory to the project root
                    .filename(".env") // Specifying the .env file
                    .load();
        } catch (Exception e) {
            System.out.println("Could not load .env file. Falling back to system environment variables.");
        }
    }

    private static String getEnvVar(String key, String defaultValue) {
        String value = null;
        if (dotenv != null) {
            value = dotenv.get(key);
        }
        if (value == null || value.isEmpty()) {
            value = System.getenv(key.replace("-", "_")); // Replace "-" with "_" for compatibility
        }
        return value != null ? value : defaultValue;
    }

    // Getter for DB Connection String
    public static String getDbConnectionString() {
        return getEnvVar("DB_CONNECTION_STRING", "mongodb://Admin:Admin123@localhost:27017");
    }


    public static String getTwitchClientID() {
        return getEnvVar("TWITCH_CLIENT_ID", "");
    }

    public static String getTwitchClientSecret() {
        return getEnvVar("TWITCH_CLIENT_SECRET", "");
    }


    public static String getAuthToken() {
        try {
            // Define the URL for the POST request
            URL url = new URL("https://id.twitch.tv/oauth2/token");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setDoOutput(true);

            // Define the request body with client credentials
            String client_id = getTwitchClientID();
            String client_secret = getTwitchClientSecret();
            String grant_type = "client_credentials";
            String requestBody = "client_id=" + client_id
                    + "&client_secret=" + client_secret
                    + "&grant_type=" + grant_type;

            // Write the request body to the output stream
            try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }

            // Get the response from the input stream
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (Scanner scanner = new Scanner(httpURLConnection.getInputStream(), StandardCharsets.UTF_8.name())) {
                    String jsonResponse = scanner.useDelimiter("\\A").next();
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    return jsonObject.getString("access_token");
                }
            } else {
                System.out.println("Error: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    };

    // Getter for DeepL Auth Key
    public static String getDeepLAuthKey() {
        return getEnvVar("DEEPL_AUTH_KEY", "default_deepl_auth_key");
    }
}
