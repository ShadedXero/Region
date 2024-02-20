package com.mortisdevelopment.regionplugin.region;

import com.mortisdevelopment.regionplugin.databases.Database;
import com.mortisdevelopment.regionplugin.utils.ColorUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class RegionManager {

    private final Database database;
    private final Map<UUID, WandSelection> selectionByPlayer;

    public RegionManager(Database database) {
        this.database = database;
        this.selectionByPlayer = new HashMap<>();
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

    public Region getRegion(Location location) {
        for (Region region : database.getRegions()) {
            if (region.isRegion(location)) {
                return region;
            }
        }
        return null;
    }
}
