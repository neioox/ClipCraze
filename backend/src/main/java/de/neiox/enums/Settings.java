package de.neiox.enums;

public enum Settings {
    WEBHOOK("webhook"),
    ASSIGNED("assigned"),
    DAYPERIODEOFTHECLIP("dayPeriodOfTheClip"),;

    private final String setting;

    Settings(String setting) {
        this.setting = setting;
    }

    public String getSetting() {
        return setting;
    }
}
