package com.mortisdevelopment.regionplugin.region;

import com.mortisdevelopment.regionplugin.databases.Database;
import com.mortisdevelopment.regionplugin.utils.ColorUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class RegionManager {

    private final Database database;
    private final Map<UUID, WandSelection> selectionByPlayer = new HashMap<>();
    private final Map<String, Region> regionByName;

    public RegionManager(Database database) {
        this.database = database;
        this.regionByName = database.loadRegions(this);
    }

    public ItemStack getWandItem() {
        ItemStack wand = new ItemStack(Material.STICK);
        ItemMeta meta = wand.getItemMeta();
        meta.displayName(ColorUtils.getComponent("&aWand"));
        wand.setItemMeta(meta);
        return wand;
    }

    public boolean isWand(ItemStack item) {
        return getWandItem().isSimilar(item);
    }

    public void addRegion(Region region) {
        regionByName.put(region.getName(), region);
        database.addRegion(region);
    }

    public Region getRegion(Location location) {
        for (Region region : getRegions()) {
            if (region.isRegion(location)) {
                return region;
            }
        }
        return null;
    }

    public Region getRegion(String name) {
        return regionByName.get(name);
    }

    public Collection<Region> getRegions() {
        return regionByName.values();
    }
}
