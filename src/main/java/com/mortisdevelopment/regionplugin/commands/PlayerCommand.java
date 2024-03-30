package com.mortisdevelopment.regionplugin.commands;

import com.mortisdevelopment.regionplugin.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PlayerCommand extends PermissionCommand {

    private final Component noConsole = ColorUtils.getComponent("&cThis command can only be executed by a player");

    public PlayerCommand(String name, String permission) {
        super(name, permission);
    }

    @Override
    public boolean isSender(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(noConsole);
            return false;
        }
        return super.isSender(sender);
    }
}
