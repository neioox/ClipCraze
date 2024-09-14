package de.neiox.models;

import kong.unirest.json.JSONObject;
import de.neiox.enums.Settings;

import java.util.EnumMap;
import java.util.Map;

public class Setting {
    private String id;
    private Map<Settings, String> settings;

    // Constructor
    public Setting(String id) {
        this.id = id;
        this.settings = new EnumMap<>(Settings.class);
    }

    // Getters and setters for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Setter for a setting based on the enum
    public void setSetting(Settings setting, String value) {
        settings.put(setting, value);
    }

    // Getter for a setting based on the enum
    public String getSetting(Settings setting) {
        return settings.get(setting);
    }

    // Convert the settings to a JSONObject
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);

        // Add each setting in the enum map to the JSON
        for (Map.Entry<Settings, String> entry : settings.entrySet()) {
            json.put(entry.getKey().getSetting(), entry.getValue());
        }

        return json;
    }
}
