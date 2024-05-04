package de.neiox.utls;

import io.github.cdimascio.dotenv.Dotenv;

public class Vars {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./") // Setting directory to the project root
            .filename(".env") // Specifying the .env file
            .load();

    private static final String dbConnectionString = dotenv.get("DB-CONNECTION-STRING");
    private static final String deepLAuthKey = dotenv.get("ef2e406f-3cc7-0fe3-f8be-78d3e6643367:fx");

    // Getter for DB Connection String
    public static String getDbConnectionString() {
        return dbConnectionString;
    }

    // Getter for DeepL Auth Key
    public static String getDeepLAuthKey() {
        return deepLAuthKey;
    }
}
