package com.mortisdevelopment.regionplugin.commands;

import com.mortisdevelopment.regionplugin.RegionPlugin;
import com.mortisdevelopment.regionplugin.commands.subcommands.*;
import com.mortisdevelopment.regionplugin.menus.RegionMenu;
import com.mortisdevelopment.regionplugin.menus.RegionsMenu;
import com.mortisdevelopment.regionplugin.region.*;
import com.mortisdevelopment.regionplugin.utils.ColorUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RegionCommand extends BaseCommand {

    private final RegionPlugin plugin;
    private final RegionManager regionManager;

    public RegionCommand(RegionPlugin plugin, RegionManager regionManager) {
        super("region");
        this.plugin = plugin;
        this.regionManager = regionManager;
        addSubCommand(new CreateCommand(regionManager));
        addSubCommand(new WandCommand(regionManager));
        addSubCommand(new AddCommand(regionManager));
        addSubCommand(new RemoveCommand(regionManager));
        addSubCommand(new WhitelistCommand(regionManager));
    }

    @Override
    public boolean isSender(CommandSender sender) {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        if (args.length == 0) {
            if (!player.hasPermission("region.menu")) {
                player.sendMessage(ColorUtils.getComponent("&cYou don't have permission to use this"));
                return false;
            }
            new RegionsMenu(plugin, regionManager, regionManager.getRegions()).open(player);
            return true;
        }
        if (!player.hasPermission("region.menu")) {
            return false;
        }
        Region region = regionManager.getRegion(args[0]);
        if (region == null) {
            return false;
        }
        new RegionMenu(plugin, regionManager, region).open(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            arguments.add("create");
            arguments.add("wand");
            arguments.add("add");
            arguments.add("remove");
            arguments.add("whitelist");
            return arguments;
        }
        return null;
    }
}
