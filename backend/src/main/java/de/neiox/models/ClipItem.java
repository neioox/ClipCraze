package de.neiox.models;

public class ClipItem {
    private String id;
    private String clip;

    public ClipItem(String id, String clip) {
        this.id = id;
        this.clip = clip;
    }

    public String getId() {
        return id;
    }

    public String getClip() {
        return clip;
    }

    @Override
    public String toString() {
        return "ClipItem{id='" + id + "', clip='" + clip + "'}";
    }
}