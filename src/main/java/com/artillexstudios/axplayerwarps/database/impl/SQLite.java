package com.artillexstudios.axplayerwarps.database.impl;

import com.artillexstudios.axplayerwarps.AxPlayerWarps;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLite extends Base {
    private final String url = String.format("jdbc:sqlite:%s/data.db", AxPlayerWarps.getInstance().getDataFolder());

    @Override
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public String getType() {
        return "SQLite";
    }

    @Override
    public void setup() {
        super.setup();
    }
}
