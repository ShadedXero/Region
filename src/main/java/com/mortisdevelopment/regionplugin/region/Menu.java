package com.mortisdevelopment.regionplugin.region;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public abstract class Menu implements InventoryHolder {

    public abstract void click(Player player, int slot);

    public void open(Player player) {
        player.openInventory(getInventory());
    }

    public void close(Player player) {
        player.closeInventory();
    }
}
