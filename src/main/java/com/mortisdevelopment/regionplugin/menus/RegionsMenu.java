package com.mortisdevelopment.regionplugin.menus;

import com.mortisdevelopment.regionplugin.RegionPlugin;
import com.mortisdevelopment.regionplugin.region.Region;
import com.mortisdevelopment.regionplugin.region.RegionManager;
import com.mortisdevelopment.regionplugin.utils.ColorUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@Getter
public class RegionsMenu extends Menu {

    private final Random random = new Random();
    private final RegionPlugin plugin;
    private final RegionManager regionManager;
    private final Map<Integer, Region> regionBySlot;
    private final List<Material> icons;
    private final Inventory inventory;

    public RegionsMenu(RegionPlugin plugin, RegionManager regionManager, Collection<Region> regions) {
        this.plugin = plugin;
        this.regionManager = regionManager;
        this.regionBySlot = new HashMap<>();
        this.icons = createIcons();
        this.inventory = createInventory(regions);
    }

    private List<Material> createIcons() {
        List<Material> materials = new ArrayList<>();
        materials.add(Material.COBBLESTONE);
        materials.add(Material.DIRT);
        materials.add(Material.GRASS_BLOCK);
        materials.add(Material.ICE);
        materials.add(Material.PODZOL);
        materials.add(Material.SNOW_BLOCK);
        return materials;
    }

    private Inventory createInventory(Collection<Region> regions) {
        Inventory inventory = Bukkit.createInventory(this, 54, ColorUtils.getComponent("&cRegions Menu"));
        int index = 0;
        for (Region region : regions) {
            if (index >= inventory.getSize()) {
                break;
            }
            regionBySlot.put(index, region);
            ItemStack item = getIcon(region.getName());
            inventory.setItem(index, item);
            index++;
        }
        return inventory;
    }

    private ItemStack getIcon(String name) {
        ItemStack item = new ItemStack(icons.get(random.nextInt(0, icons.size())));
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ColorUtils.getComponent("&a" + name));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void click(Player player, int slot) {
        Region region = regionBySlot.get(slot);
        if (region == null) {
            return;
        }
        new RegionMenu(plugin, regionManager, region).open(player);
    }
}
