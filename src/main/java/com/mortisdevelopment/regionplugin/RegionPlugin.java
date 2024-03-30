package com.mortisdevelopment.regionplugin;

import com.mortisdevelopment.regionplugin.databases.Database;
import com.mortisdevelopment.regionplugin.commands.RegionCommand;
import com.mortisdevelopment.regionplugin.listeners.RegionListener;
import com.mortisdevelopment.regionplugin.region.RegionManager;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

@Getter
public final class RegionPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        RegionManager regionManager = new RegionManager(getDatabase());
        getServer().getPluginManager().registerEvents(new RegionListener(this, regionManager), this);
        new RegionCommand(this, regionManager).register(this);
    }

    private Database getDatabase() {
        saveResource("config.yml", false);
        File file = new File(getDataFolder(), "config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = Objects.requireNonNull(config.getConfigurationSection("database"));
        String host = section.getString("host");
        int port = section.getInt("port");
        String database = section.getString("database");
        String username = section.getString("username");
        String password = section.getString("password");
        return new Database(this, host, port, database, username, password);
    }
}
