package com.mortisdevelopment.regionplugin.commands.subcommands;

import com.mortisdevelopment.regionplugin.commands.PlayerCommand;
import com.mortisdevelopment.regionplugin.region.RegionManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WandCommand extends PlayerCommand {

    private final RegionManager regionManager;

    public WandCommand(RegionManager regionManager) {
        super("wand", "region.wand");
        this.regionManager = regionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        player.getInventory().addItem(regionManager.getWandItem());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return null;
    }
}
