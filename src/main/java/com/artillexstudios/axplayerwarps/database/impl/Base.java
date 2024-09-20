package com.artillexstudios.axplayerwarps.database.impl;

import com.artillexstudios.axplayerwarps.database.Database;

import java.sql.Connection;

public class Base implements Database {
    public Connection getConnection() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void setup() {
    }

    @Override
    public void disable() {
    }
}
