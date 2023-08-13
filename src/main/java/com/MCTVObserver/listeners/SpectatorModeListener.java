package com.MCTVObserver.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.MCTVObserver.MCTVObserver;

public class SpectatorModeListener implements Listener {

    private MCTVObserver plugin;

    public SpectatorModeListener(MCTVObserver plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin instance cannot be null!");
        }
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();

        if (event.getNewGameMode() == GameMode.SPECTATOR) {
            String message = plugin.getConfig().getString("messages.enterSpectatorMode",
                    "¡Bienvenido al modo MCTV Observer!");
            if (message != null) {
                player.sendMessage(message);
            }

            if (plugin.isAutoCameraMode() && plugin.getCameraController() != null) { // added null check
                activateAutoCamera(player);
            }

        } else if (event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            String message = plugin.getConfig().getString("messages.exitSpectatorMode",
                    "Has salido del modo MCTV Observer.");
            if (message != null) {
                player.sendMessage(message);
            }

            if (plugin.getCameraController() != null) { // added null check
                deactivateCamera(player);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.SPECTATOR) {
            String message = plugin.getConfig().getString("messages.spectatorGreeting",
                    "¡Hola, Observador MCTV! Esperamos que disfrutes del espectáculo.");
            if (message != null) {
                player.sendMessage(message);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.SPECTATOR && plugin.getCameraController() != null) { // added null check
            deactivateCamera(player);
        }
    }

    /**
     * Activa la cámara automática para el jugador dado.
     * 
     * @param player El jugador para quien se activará la cámara.
     */
    private void activateAutoCamera(Player player) {
        if (plugin.getCameraController() != null) { // added null check
            plugin.getCameraController().startAutoModeForPlayer(player);
        }
    }

    /**
     * Desactiva la cámara para el jugador dado.
     * 
     * @param player El jugador para quien se desactivará la cámara.
     */
    private void deactivateCamera(Player player) {
        if (plugin.getCameraController() != null) { // added null check
            plugin.getCameraController().stopCameraForPlayer(player);
        }
    }
}
