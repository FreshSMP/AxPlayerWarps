package com.artillexstudios.axplayerwarps.database.impl;

import com.artillexstudios.axplayerwarps.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

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
        execute("""
                CREATE TABLE IF NOT EXISTS axplayerwarps_players (
                	id INT NOT NULL AUTO_INCREMENT,
                	uuid VARCHAR(36) NOT NULL,
                	name VARCHAR(128) NOT NULL,
                	PRIMARY KEY (id),
                	UNIQUE (uuid)
                );
        """);

        execute("""
                CREATE TABLE IF NOT EXISTS axplayerwarps_currencies (
                	id INT NOT NULL AUTO_INCREMENT,
                	currency VARCHAR(512) NOT NULL,
                	PRIMARY KEY (id),
                	UNIQUE (currency)
                );
        """);

        execute("""
                CREATE TABLE IF NOT EXISTS axplayerwarps_worlds (
                	id INT NOT NULL AUTO_INCREMENT,
                	world VARCHAR(512) NOT NULL,
                	PRIMARY KEY (id),
                	UNIQUE (world)
                );
        """);

        execute("""
                CREATE TABLE IF NOT EXISTS axplayerwarps_warps (
                	id INT NOT NULL AUTO_INCREMENT,
                	owner_id INT NOT NULL,
                	world_id INT NOT NULL,
                	x FLOAT NOT NULL,
                	y FLOAT NOT NULL,
                	z FLOAT NOT NULL,
                	name VARCHAR(1024) NOT NULL,
                	description TEXT NOT NULL,
                	category_id INT NOT NULL,
                	created BIGINT NOT NULL,
                    currency_id INT NOT NULL,
                    price DOUBLE NOT NULL DEFAULT '0',
                	PRIMARY KEY (id)
                );
        """);

        execute("""
                CREATE TABLE IF NOT EXISTS axplayerwarps_visits (
                	id INT NOT NULL AUTO_INCREMENT,
                	visitor_id INT NOT NULL,
                	warp_id INT,
                	date BIGINT,
                	PRIMARY KEY (id)
                );
        """);

        execute("""
                CREATE TABLE IF NOT EXISTS axplayerwarps_ratings (
                	id INT NOT NULL AUTO_INCREMENT,
                	reviewer_id INT NOT NULL,
                	warp_id INT NOT NULL,
                	stars TINYINT NOT NULL,
                	PRIMARY KEY (id)
                );
        """);

        execute("""
                CREATE TABLE IF NOT EXISTS axplayerwarps_categories (
                	id INT NOT NULL AUTO_INCREMENT,
                	category VARCHAR(512) NOT NULL,
                	PRIMARY KEY (id),
                	UNIQUE (category)
                );
        """);
    }

    private void execute(String sql, Object... obj) {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            int n = 1;
            for (Object o : obj) stmt.setObject(n++, o);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private ResultSet executeQuery(String sql, Object... obj) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            int n = 1;
            for (Object o : obj) stmt.setObject(n++, o);
            return stmt.executeQuery();
        }
    }

    private ResultSet insert(String sql, Object... obj) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int n = 1;
            for (Object o : obj) stmt.setObject(n++, o);
            stmt.executeUpdate();
            return stmt.getGeneratedKeys();
        }
    }

    public int getPlayerId(OfflinePlayer offlinePlayer) {
        return getPlayerId(offlinePlayer.getUniqueId());
    }

    public int getPlayerId(UUID uuid) {
        try (ResultSet rs = executeQuery("SELECT id FROM axplayerwarps_players WHERE uuid = ?", uuid)) {
            if (rs.next()) return rs.getInt(1);
            else {
                OfflinePlayer pl = Bukkit.getOfflinePlayer(uuid);
                try (ResultSet rs2 = insert("INSERT INTO axplayerwarps_players (uuid, name) VALUES (?, ?)", uuid, pl.getName())) {
                    if (rs2.next()) return rs2.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        throw new RuntimeException("Player not found!");
    }

    public UUID getUUIDFromId(int id) {
        try (ResultSet rs = executeQuery("SELECT uuid FROM axplayerwarps_players WHERE id = ?", id)) {
            if (rs.next()) return UUID.fromString(rs.getString(1));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        throw new RuntimeException("Player not found!");
    }

    public int getWorldId(String world) {
        return getWorldId(Bukkit.getWorld(world));
    }

    public int getWorldId(World world) {
        try (ResultSet rs = executeQuery("SELECT id FROM axplayerwarps_worlds WHERE world = ?", world.getName())) {
            if (rs.next()) return rs.getInt(1);
            else {
                try (ResultSet rs2 = insert("INSERT INTO axplayerwarps_players (world) VALUES (?)", world.getName())) {
                    if (rs2.next()) return rs2.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        throw new RuntimeException("World not found!");
    }

    public World getWorldFromId(int id) {
        try (ResultSet rs = executeQuery("SELECT world FROM axplayerwarps_worlds WHERE id = ?", id)) {
            if (rs.next()) return Bukkit.getWorld(rs.getString(1));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        throw new RuntimeException("World not found!");
    }

    @Override
    public int getCategoryId(String category) {
        try (ResultSet rs = executeQuery("SELECT id FROM axplayerwarps_categories WHERE category = ?", category)) {
            if (rs.next()) return rs.getInt(1);
            else {
                try (ResultSet rs2 = insert("INSERT INTO axplayerwarps_categories (category) VALUES (?)", category)) {
                    if (rs2.next()) return rs2.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        throw new RuntimeException("Category not found!");
    }

    @Override
    public void disable() {
    }
}
