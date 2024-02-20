package com.mortisdevelopment.regionplugin.region;

import com.mortisdevelopment.regionplugin.databases.Database;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.UUID;

@Getter
public class Region {

    private final Permission permission = new Permission("region.bypass");
    private final Database database;
    private String name;
    private Location originLocation;
    private Location endLocation;
    private ArrayList<UUID> whitelist;

    public Region(Database database, String name, Location originLocation, Location endLocation, ArrayList<UUID> whitelist) {
        this.database = database;
        this.name = name;
        this.originLocation = originLocation;
        this.endLocation = endLocation;
        this.whitelist = whitelist;
    }

    public boolean isRegion(Location location) {
        double minX = Math.min(originLocation.getX(), endLocation.getX());
        double minY = Math.min(originLocation.getY(), endLocation.getY());
        double minZ = Math.min(originLocation.getZ(), endLocation.getZ());

        double maxX = Math.max(originLocation.getX(), endLocation.getX());
        double maxY = Math.max(originLocation.getY(), endLocation.getY());
        double maxZ = Math.max(originLocation.getZ(), endLocation.getZ());

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public boolean isWhitelisted(Player player) {
        if (player.hasPermission(permission)) {
            return true;
        }
        return whitelist.contains(player.getUniqueId());
    }

    public void setName(String name) {
        database.setName(this.name, name);
        database.getRegionByName().remove(this.name);
        database.getRegionByName().put(name, this);
        this.name = name;
    }

    public void setOriginLocation(Location originLocation) {
        this.originLocation = originLocation;
        database.setOriginLocation(name, originLocation);
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
        database.setEndLocation(name, endLocation);
    }

    public void setWhitelist(ArrayList<UUID> whitelist) {
        this.whitelist = whitelist;
        database.setWhitelist(name, whitelist);
    }

    public void addWhitelist(UUID player) {
        if (whitelist.contains(player)) {
            return;
        }
        whitelist.add(player);
        database.setWhitelist(name, whitelist);
    }

    public void removeWhitelist(UUID player) {
        whitelist.remove(player);
        database.setWhitelist(name, whitelist);
    }
}
