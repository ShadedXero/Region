package com.mortisdevelopment.regionplugin.listeners;

import com.mortisdevelopment.regionplugin.RegionPlugin;
import com.mortisdevelopment.regionplugin.menus.Menu;
import com.mortisdevelopment.regionplugin.region.Region;
import com.mortisdevelopment.regionplugin.region.RegionManager;
import com.mortisdevelopment.regionplugin.region.WandSelection;
import com.mortisdevelopment.regionplugin.utils.ColorUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public record RegionListener(RegionPlugin plugin, RegionManager regionManager) implements Listener {

    @EventHandler
    public void onWandSelect(PlayerInteractEvent e) {
        if (e.useItemInHand().equals(Event.Result.DENY)) {
            return;
        }
        Player player = e.getPlayer();
        if (!player.hasPermission("region.create")) {
            return;
        }
        Block block = e.getClickedBlock();
        if (block == null) {
            return;
        }
        ItemStack item = e.getItem();
        if (item == null || !regionManager.isWand(item)) {
            return;
        }
        e.setCancelled(true);
        Location location = block.getLocation();
        WandSelection selection = regionManager.getSelectionByPlayer().get(player.getUniqueId());
        if (e.getAction().isRightClick()) {
            selection.setEndLocation(location);
            player.sendMessage(ColorUtils.getComponent("&aSelected the second location"));
        }else {
            selection.setOriginLocation(location);
            player.sendMessage(ColorUtils.getComponent("&aSelected the first location"));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        regionManager.getSelectionByPlayer().put(e.getPlayer().getUniqueId(), new WandSelection());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        regionManager.getSelectionByPlayer().remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onRegionInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null) {
            return;
        }
        Region region = regionManager.getRegion(block.getLocation());
        Player player = e.getPlayer();
        if (region == null || region.isWhitelisted(player)) {
            return;
        }
        e.setCancelled(true);
        player.sendMessage(ColorUtils.getComponent("&cYou are not permitted to interact in this region"));
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        Inventory inventory = e.getClickedInventory();
        if (inventory == null || !(inventory.getHolder() instanceof Menu menu)) {
            return;
        }
        e.setCancelled(true);
        menu.click((Player) e.getWhoClicked(), e.getSlot());
    }
}
