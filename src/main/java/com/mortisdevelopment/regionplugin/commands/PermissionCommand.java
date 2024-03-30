package com.mortisdevelopment.regionplugin.commands;

import com.mortisdevelopment.regionplugin.utils.ColorUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

@Getter
public abstract class PermissionCommand extends BaseCommand {

    private final Component noPermission = ColorUtils.getComponent("&cYou don't have permission to use this");
    private final String permission;

    public PermissionCommand(String name, String permission) {
        super(name);
        this.permission = permission;
    }

    @Override
    public boolean isSender(CommandSender sender) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(noPermission);
            return false;
        }
        return true;
    }
}
