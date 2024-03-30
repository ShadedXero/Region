package com.mortisdevelopment.regionplugin.commands.subcommands;

import com.mortisdevelopment.regionplugin.commands.PermissionCommand;
import com.mortisdevelopment.regionplugin.region.Region;
import com.mortisdevelopment.regionplugin.region.RegionManager;
import com.mortisdevelopment.regionplugin.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WhitelistCommand extends PermissionCommand {

    private final RegionManager regionManager;

    public WhitelistCommand(RegionManager regionManager) {
        super("whitelist", "region.whitelist");
        this.regionManager = regionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ColorUtils.getComponent("&cUsage: /region whitelist <name>"));
            return false;
        }
        Region region = regionManager.getRegion(args[0]);
        if (region == null) {
            sender.sendMessage(ColorUtils.getComponent("&cPlease enter a valid region name"));
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

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(regionManager.getRegionByName().keySet());
        }
        return null;
    }
}
