package de.neiox.utls;

import io.github.cdimascio.dotenv.Dotenv;

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
        return getEnvVar("DB_CONNECTION_STRING", "mongodb://ADMIN:Admin123@172.17.0.2:27017");
    }

    // Getter for DeepL Auth Key
    public static String getDeepLAuthKey() {
        return getEnvVar("DEEPL_AUTH_KEY", "default_deepl_auth_key");
    }
}
