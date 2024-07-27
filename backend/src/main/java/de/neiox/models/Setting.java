package de.neiox.models;

import kong.unirest.json.JSONObject;

public class Setting {
    private String id;
    private String webhook;
    private String assigned;

    // Constructor
    public Setting(String id, String webhook, String assigned) {
        this.id = id;
        this.webhook = webhook;
        this.assigned = assigned;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getAssigned() {
        return assigned;
    }

    public void setAssigned(String assigned) {
        this.assigned = assigned;
    }



    // Convert to JSON
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("webhook", webhook);
        json.put("assigned", assigned);
        return json;

    }
}
