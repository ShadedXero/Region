package com.mortisdevelopment.regionplugin.region;

import com.mortisdevelopment.regionplugin.RegionPlugin;
import com.mortisdevelopment.regionplugin.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RegionCommand extends BukkitCommand {

    private final Component noPermission = ColorUtils.getComponent("&cYou don't have permission to use this");
    private final Component invalidPlayer = ColorUtils.getComponent("&cPlease enter a valid player name");
    private final Component noConsole = ColorUtils.getComponent("&cThis can only be executed by a player");
    private final Component invalidSelection = ColorUtils.getComponent("&cPlease select the two corners");
    private final Component invalidRegion = ColorUtils.getComponent("&cPlease enter a valid region name");
    private final RegionPlugin plugin;
    private final RegionManager regionManager;

    public RegionCommand(RegionPlugin plugin, RegionManager regionManager) {
        super("region");
        this.plugin = plugin;
        this.regionManager = regionManager;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (!sender.hasPermission("region.menu")) {
                sender.sendMessage(noPermission);
                return false;
            }
            if (!(sender instanceof Player player)) {
                sender.sendMessage(noConsole);
                return false;
            }
            new RegionsMenu(plugin, regionManager, regionManager.getDatabase().getRegions()).open(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (!sender.hasPermission("region.create")) {
                sender.sendMessage(noPermission);
                return false;
            }
            if (!(sender instanceof Player player)) {
                sender.sendMessage(noConsole);
                return false;
            }
            if (args.length < 2) {
                sender.sendMessage(ColorUtils.getComponent("&cUsage: /region create <name>"));
                return false;
            }
            String name = args[1];
            if (regionManager.getDatabase().getRegionByName().containsKey(name)) {
                sender.sendMessage(ColorUtils.getComponent("&cPlease enter a unique name. A region with that name already exists"));
                return false;
            }
            UUID uuid = player.getUniqueId();
            WandSelection selection = regionManager.getSelectionByPlayer().get(uuid);
            if (!selection.isValid()) {
                sender.sendMessage(invalidSelection);
                return false;
            }
            regionManager.getSelectionByPlayer().put(uuid, new WandSelection());
            Region region = new Region(regionManager.getDatabase(), name, selection.getOriginLocation(), selection.getEndLocation(), new ArrayList<>(Collections.singletonList(uuid)));
            regionManager.getDatabase().addRegion(region);
            sender.sendMessage(ColorUtils.getComponent("&aSuccessfully created the region"));
            return true;
        }
        if (args[0].equalsIgnoreCase("wand")) {
            if (!sender.hasPermission("region.create")) {
                sender.sendMessage(noPermission);
                return false;
            }
            if (!(sender instanceof Player player)) {
                sender.sendMessage(noConsole);
                return false;
            }
            player.getInventory().addItem(regionManager.getWandItem());
            return true;
        }
        if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
            boolean add;
            if (args[0].equalsIgnoreCase("add")) {
                if (!sender.hasPermission("region.add")) {
                    sender.sendMessage(noPermission);
                    return false;
                }
                add = true;
            }else {
                if (!sender.hasPermission("region.remove")) {
                    sender.sendMessage(noPermission);
                    return false;
                }
                add = false;
            }
            if (args.length < 3) {
                if (add) {
                    sender.sendMessage(ColorUtils.getComponent("&cUsage: /region add <name> <username>"));
                }else {
                    sender.sendMessage(ColorUtils.getComponent("&cUsage: /region remove <name> <username>"));
                }
                return false;
            }
            Region region = regionManager.getDatabase().getRegion(args[1]);
            if (region == null) {
                sender.sendMessage(invalidRegion);
                return false;
            }
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) {
                sender.sendMessage(invalidPlayer);
                return false;
            }
            if (add) {
                region.addWhitelist(player.getUniqueId());
                sender.sendMessage(ColorUtils.getComponent("&aAdded the player to the whitelist of the region"));
            }else {
                region.removeWhitelist(player.getUniqueId());
                sender.sendMessage(ColorUtils.getComponent("&aRemoved the player to the whitelist of the region"));
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("whitelist")) {
            if (!sender.hasPermission("region.whitelist")) {
                sender.sendMessage(noPermission);
                return false;
            }
            if (args.length < 2) {
                sender.sendMessage(ColorUtils.getComponent("&cUsage: /region whitelist <name>"));
                return false;
            }
            Region region = regionManager.getDatabase().getRegion(args[1]);
            if (region == null) {
                sender.sendMessage(invalidRegion);
                return false;
            }
            for (UUID uuid : region.getWhitelist()) {
                String name = Bukkit.getOfflinePlayer(uuid).getName();
                if (name == null) {
                    continue;
                }
                sender.sendMessage(ColorUtils.getComponent("&a" + name));
            }
            return true;
        }
        if (!sender.hasPermission("region.menu")) {
            sender.sendMessage(noPermission);
            return false;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(noConsole);
            return false;
        }
        Region region = regionManager.getDatabase().getRegion(args[0]);
        if (region == null) {
            sender.sendMessage(invalidRegion);
            return false;
        }
        new RegionMenu(plugin, regionManager, region).open(player);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("create");
            arguments.add("wand");
            arguments.add("add");
            arguments.add("remove");
            arguments.add("whitelist");
            return arguments;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("whitelist")) {
                return new ArrayList<>(regionManager.getDatabase().getRegionByName().keySet());
            }
        }
        return super.tabComplete(sender, alias, args);
    }
}
