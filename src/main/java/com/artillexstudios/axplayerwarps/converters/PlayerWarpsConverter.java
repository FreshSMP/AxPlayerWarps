package com.artillexstudios.axplayerwarps.converters;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axplayerwarps.AxPlayerWarps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.UUID;

public class PlayerWarpsConverter implements ConverterBase {
    private final String url = "jdbc:sqlite:" + "plugins/PlayerWarps/data/database.db";

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void run() {
        final String sql = "SELECT name, uuid, world, x, y, z, pitch, yaw, description, date FROM playerwarps_warps;";
        HashSet<OfflinePlayer> players = new HashSet<>();
        int warps = 0;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString(1)));
                    players.add(offlinePlayer);
                    warps++;
                    Location location = new Location(Bukkit.getWorld(rs.getString(2)), rs.getDouble(3), rs.getDouble(4), rs.getDouble(5), rs.getFloat(6), rs.getFloat(7));
                    AxPlayerWarps.getDatabase().createWarp(offlinePlayer, location, rs.getString(1));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#33FF33[AxPlayerWarps] Finished converting " + warps + " warps of " + players.size() + " players!"));
    }
}
