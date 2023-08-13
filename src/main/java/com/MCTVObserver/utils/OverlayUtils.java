package com.MCTVObserver.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.MCTVObserver.MCTVObserver;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class OverlayUtils {

    private final MCTVObserver plugin;
    private final FileConfiguration config;

    public OverlayUtils(MCTVObserver plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void displayTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(colorize(title), colorize(subtitle), fadeIn, stay, fadeOut);
    }

    public void displayPlayerInfo(Player observer, Player target) {
        if (config.getBoolean("displayPlayerInfo.enabled")) {
            String title = ChatColor.translateAlternateColorCodes('&',
                    config.getString("displayPlayerInfo.title").replace("{player}", target.getName()));
            String subtitle = ChatColor.translateAlternateColorCodes('&',
                    config.getString("displayPlayerInfo.subtitle")
                            .replace("{health}", String.valueOf(target.getHealth()))
                            .replace("{maxHealth}", String.valueOf(target.getMaxHealth())));
            displayTitle(observer, title, subtitle, 10, 40, 10);
        }
    }

    public void displayPlayerDetailedInfo(Player observer, Player target) {
        // Aquí puedes expandir con más detalles, como antes.
    }

    public void displayTemporaryMessage(final Player player, String message, int duration) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(colorize(message)));

        new BukkitRunnable() {
            @Override
            public void run() {
                clearActionBarMessage(player);
            }
        }.runTaskLater(plugin, duration);
    }

    public void clearActionBarMessage(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
    }

    public void highlightPlayerAchievement(Player player, String achievement) {
        String message = ChatColor.GOLD + player.getName() + " ha conseguido el logro: " + ChatColor.AQUA + achievement;
        displayCommentOverlay(player, message);
    }

    public void notifyCameraChange(Player observer, String newMode) {
        if (config.getBoolean("notifyCameraChange.enabled")) {
            String message = ChatColor.translateAlternateColorCodes('&',
                    config.getString("notifyCameraChange.message").replace("{mode}", newMode));
            displayTemporaryMessage(observer, message, 60);
        }
    }

    public void displayPlayerScore(Player player, String objectiveName, String scoreName, int scoreValue) {
        // Aquí puedes mostrar el marcador del jugador, si lo tienes configurado.
    }

    public void clearPlayerScore(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void displayCommentOverlay(Player player, String message) {
        displayTemporaryMessage(player, message, 100);
    }
}
