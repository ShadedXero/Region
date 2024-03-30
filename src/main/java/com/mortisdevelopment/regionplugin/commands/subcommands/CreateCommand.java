package com.mortisdevelopment.regionplugin.commands.subcommands;

import com.mortisdevelopment.regionplugin.commands.PlayerCommand;
import com.mortisdevelopment.regionplugin.region.Region;
import com.mortisdevelopment.regionplugin.region.RegionManager;
import com.mortisdevelopment.regionplugin.region.WandSelection;
import com.mortisdevelopment.regionplugin.utils.ColorUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CreateCommand extends PlayerCommand {

    private final RegionManager regionManager;

    public CreateCommand(RegionManager regionManager) {
        super("create", "region.create");
        this.regionManager = regionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length < 1) {
            sender.sendMessage(ColorUtils.getComponent("&cUsage: /region create <name>"));
            return false;
        }
        String name = args[0];
        if (regionManager.getRegionByName().containsKey(name)) {
            sender.sendMessage(ColorUtils.getComponent("&cPlease enter a unique name. A region with that name already exists"));
            return false;
        }
        UUID uuid = player.getUniqueId();
        WandSelection selection = regionManager.getSelectionByPlayer().get(uuid);
        if (!selection.isValid()) {
            sender.sendMessage(ColorUtils.getComponent("&cPlease select the two corners"));
            return false;
        }
        regionManager.getSelectionByPlayer().put(uuid, new WandSelection());
        Region region = new Region(regionManager, name, selection.getOriginLocation(), selection.getEndLocation(), new ArrayList<>(Collections.singletonList(uuid)));
        regionManager.addRegion(region);
        sender.sendMessage(ColorUtils.getComponent("&aSuccessfully created the region"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        return null;
    }
}
