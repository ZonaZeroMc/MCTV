package com.MCTVObserver.utils;

import org.bukkit.configuration.file.FileConfiguration;
import com.MCTVObserver.MCTVObserver;
import java.util.List;

/**
 * Provides utility methods to interact with the plugin's configuration.
 */
public class ConfigUtils {

    private final MCTVObserver plugin;
    private FileConfiguration config;

    public ConfigUtils(MCTVObserver plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    /**
     * Saves the configuration.
     */
    public void saveConfig() {
        plugin.saveConfig();
    }

    /**
     * Reloads the configuration from the file.
     */
    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public String getString(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    public void setString(String path, String value) {
        config.set(path, value);
    }

    public int getInt(String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }

    public void setInt(String path, int value) {
        config.set(path, value);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return config.getBoolean(path, defaultValue);
    }

    public void setBoolean(String path, boolean value) {
        config.set(path, value);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public void setStringList(String path, List<String> values) {
        config.set(path, values);
    }
}
