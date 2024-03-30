package com.mortisdevelopment.regionplugin.databases;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import com.mortisdevelopment.regionplugin.RegionPlugin;
import com.mortisdevelopment.regionplugin.region.Region;
import com.mortisdevelopment.regionplugin.region.RegionManager;
import com.mysql.cj.jdbc.Driver;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class Database {

    private final RegionPlugin plugin;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private Connection connection;

    public Database(RegionPlugin plugin, String host, int port, String database, String username, String password) {
        this.plugin = plugin;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.connection = getConnection();
        initialize();
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        if (username != null) {
            properties.setProperty("user", username);
        }
        if (password != null) {
            properties.setProperty("password", password);
        }
        return properties;
    }

    public Connection getConnection() {
        if (connection != null) {
            return connection;
        }
        try {
            Driver driver = new Driver();
            this.connection = driver.connect("jdbc:mysql://" + host + ":" + port + "/" + database, getProperties());
            return connection;
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    private void initialize() {
        try {
            Statement statement = getConnection().createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS Regions(name varchar(255) primary key, origin_location varchar(500), end_location varchar(500), whitelist varchar(1000))");
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    public Map<String, Region> loadRegions(RegionManager regionManager) {
        Map<String, Region> regionByName = new HashMap<>();
        try {
            Statement statement = getConnection().createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM Regions");
            while (result.next()) {
                String name = result.getString("name");
                Location originLocation = getLocation(result.getString("origin_location"));
                Location endLocation = getLocation(result.getString("end_location"));
                ArrayList<UUID> whitelist = getWhitelist(result.getString("whitelist"));
                Region region = new Region(regionManager, name, originLocation, endLocation, whitelist);
                regionByName.put(name, region);
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
        return regionByName;
    }

    private Location getLocation(String location) {
        String[] raw = location.split(",");
        World world = Bukkit.getWorld(raw[0]);
        double x = Double.parseDouble(raw[1]);
        double y = Double.parseDouble(raw[2]);
        double z = Double.parseDouble(raw[3]);
        return new Location(world, x, y, z);
    }

    private String getLocation(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ();
    }

    private ArrayList<UUID> getWhitelist(String whitelist) {
        if (whitelist == null || whitelist.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(whitelist.split(",")).map(UUID::fromString).collect(Collectors.toCollection(ArrayList::new));
    }

    private String getWhitelist(ArrayList<UUID> whitelist) {
        if (whitelist.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(",");
        for (UUID uuid : whitelist) {
            joiner.add(uuid.toString());
        }
        return joiner.toString();
    }

    public void addRegion(Region region) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement statement = getConnection().prepareStatement("INSERT INTO Regions(name, origin_location, end_location, whitelist) VALUES (?, ?, ?, ?)");
                    statement.setString(1, region.getName());
                    statement.setString(2, getLocation(region.getOriginLocation()));
                    statement.setString(3, getLocation(region.getEndLocation()));
                    statement.setString(4, getWhitelist(region.getWhitelist()));
                    statement.executeUpdate();
                } catch (SQLException exp) {
                    throw new RuntimeException(exp);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void setName(String name, String newName) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement statement = getConnection().prepareStatement("UPDATE Regions SET name = ? WHERE name = ?");
                    statement.setString(1, newName);
                    statement.setString(2, name);
                    statement.executeUpdate();
                }catch (SQLException exp) {
                    throw new RuntimeException(exp);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void setLocation(String name, Location location, boolean origin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                String field = origin ? "origin_location" : "end_location";
                try {
                    PreparedStatement statement = getConnection().prepareStatement("UPDATE Regions SET " + field + " = ? WHERE name = ?");
                    statement.setString(1, getLocation(location));
                    statement.setString(2, name);
                    statement.executeUpdate();
                } catch (SQLException exp) {
                    throw new RuntimeException(exp);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void setOriginLocation(String name, Location originLocation) {
        setLocation(name, originLocation, true);
    }

    public void setEndLocation(String name, Location endLocation) {
        setLocation(name, endLocation, false);
    }

    public void setWhitelist(String name, ArrayList<UUID> uuids) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement statement = getConnection().prepareStatement("UPDATE Regions SET whitelist = ? WHERE name = ?");
                    statement.setString(1, getWhitelist(uuids));
                    statement.setString(2, name);
                    statement.executeUpdate();
                }catch (SQLException exp) {
                    throw new RuntimeException(exp);
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
