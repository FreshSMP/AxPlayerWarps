package com.artillexstudios.axplayerwarps.enums;

public enum AccessList {
    WHITELIST("axplayerwarps_whitelisted", "whitelisted"),
    BLACKLIST("axplayerwarps_blacklisted", "blacklisted");

    private final String table;
    private final String route;
    AccessList(String table, String route) {
        this.table = table;
        this.route = route;
    }

    public String getTable() {
        return table;
    }

    public String getRoute() {
        return route;
    }
}
