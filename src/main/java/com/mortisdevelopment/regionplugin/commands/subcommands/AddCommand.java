package com.mortisdevelopment.regionplugin.commands.subcommands;

import com.mortisdevelopment.regionplugin.commands.PermissionCommand;
import com.mortisdevelopment.regionplugin.region.Region;
import com.mortisdevelopment.regionplugin.region.RegionManager;
import com.mortisdevelopment.regionplugin.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AddCommand extends PermissionCommand {

    private final RegionManager regionManager;

    public AddCommand(RegionManager regionManager) {
        super("add", "region.add");
        this.regionManager = regionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorUtils.getComponent("&cUsage: /region add <name> <username>"));
            return false;
        }
        Region region = regionManager.getRegion(args[0]);
        if (region == null) {
            sender.sendMessage(ColorUtils.getComponent("&cPlease enter a valid region name"));
            return false;
        }
        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage(ColorUtils.getComponent("&cPlease enter a valid player name"));
            return false;
        }
        region.addWhitelist(player.getUniqueId());
        sender.sendMessage(ColorUtils.getComponent("&aAdded the player to the whitelist of the region"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(regionManager.getRegionByName().keySet());
        }
        return null;
    }
}
