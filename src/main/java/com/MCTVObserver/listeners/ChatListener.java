package com.MCTVObserver.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.MCTVObserver.MCTVObserver;
import com.MCTVObserver.utils.OverlayUtils;

public class ChatListener implements Listener {

    private MCTVObserver plugin;
    private OverlayUtils overlayUtils;

    public ChatListener(MCTVObserver plugin) {
        this.plugin = plugin;
        this.overlayUtils = new OverlayUtils(plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (player.getGameMode() == org.bukkit.GameMode.SPECTATOR) {
            handleSpectatorCommentary(player, message);
            event.setCancelled(true);
        } else if (message.startsWith("!MCTV")) {
            handleSpectatorCommentary(player, message.replaceFirst("!MCTV", "").trim());
            event.setCancelled(true);
        }
    }

    private void handleSpectatorCommentary(Player commentator, String message) {
        // Usando ChatColor para personalizar el mensaje
        String formattedMessage = ChatColor.AQUA + "[MCTV Comment] "
                + ChatColor.GOLD + commentator.getName()
                + ChatColor.WHITE + ": "
                + message;

        // Envía el comentario formateado a todos los jugadores en el servidor:
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(formattedMessage);
        }

        // Usa la instancia overlayUtils para llamar al método
        overlayUtils.displayCommentOverlay(commentator, formattedMessage);
    }
}
