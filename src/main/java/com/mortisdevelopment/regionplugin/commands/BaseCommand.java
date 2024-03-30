package com.mortisdevelopment.regionplugin.commands;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class BaseCommand extends Command {

    private final List<BaseCommand> subCommands;

    public BaseCommand(String name) {
        super(name);
        this.subCommands = new ArrayList<>();
    }

    public boolean isName(String name) {
        if (getName().equalsIgnoreCase(name)) {
            return true;
        }
        for (String alias : getAliases()) {
            if (alias.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void addSubCommand(BaseCommand subCommand) {
        this.subCommands.add(subCommand);
    }

    public abstract boolean isSender(CommandSender sender);

    public abstract boolean onCommand(CommandSender sender, String label, String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, String label, String[] args);

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (!isSender(sender)) {
            return false;
        }
        if (onCommand(sender, label, args)) {
            return true;
        }
        return onSubCommand(sender, label, args);
    }

    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (!isSender(sender)) {
            return super.tabComplete(sender, label, args);
        }
        List<String> tab = onTabComplete(sender, label, args);
        if (tab != null) {
            return tab;
        }
        List<String> subTab = onSubTabComplete(sender, label, args);
        if (subTab != null) {
            return subTab;
        }
        return super.tabComplete(sender, label, args);
    }

    public void register(JavaPlugin plugin) {
        plugin.getServer().getCommandMap().register(getName(), this);
    }

    public boolean onSubCommand(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        for (BaseCommand subCommand : getSubCommands()) {
            if (!subCommand.isName(args[0])) {
                continue;
            }
            if (!isSender(sender)) {
                continue;
            }
            if (subCommand.onCommand(sender, label, getNewArgs(args))) {
                return true;
            }
            return subCommand.onSubCommand(sender, label, getNewArgs(args));
        }
        return false;
    }

    public List<String> onSubTabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            return null;
        }
        for (BaseCommand subCommand : getSubCommands()) {
            if (!subCommand.isName(args[0])) {
                continue;
            }
            if (!isSender(sender)) {
                continue;
            }
            List<String> tab = subCommand.onTabComplete(sender, label, getNewArgs(args));
            if (tab != null) {
                return tab;
            }
            return subCommand.onSubTabComplete(sender, label, getNewArgs(args));
        }
        return null;
    }

    public String[] getNewArgs(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }
}
