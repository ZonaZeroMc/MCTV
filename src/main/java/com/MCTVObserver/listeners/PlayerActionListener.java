package com.MCTVObserver.listeners;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.MCTVObserver.MCTVObserver;

public class PlayerActionListener implements Listener {

    private MCTVObserver plugin;
    private UUID currentNPCUUID; // UUID del NPC actual al que apunta la cámara
    private int taskID; // ID del scheduler que cambiará el NPC cada 45 segundos

    public PlayerActionListener(MCTVObserver plugin) {
        this.plugin = plugin;
        startNPCCameraRotation();
    }

    private void startNPCCameraRotation() {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                List<UUID> npcUUIDs = getAllNpcUUIDs();
                if (!npcUUIDs.isEmpty()) {
                    UUID randomUUID = npcUUIDs.get((int) (Math.random() * npcUUIDs.size()));
                    centerCameraOnNPC(randomUUID);
                }
            }
        }, 0L, 45 * 20L); // El '45 * 20L' es porque Bukkit/Spigot cuenta el tiempo en ticks, y hay 20
                          // ticks por segundo
    }

    private List<UUID> getAllNpcUUIDs() {
        // Suponiendo que tus NPCs son entidades distintas en el servidor:
        return Bukkit.getWorlds().stream()
                .flatMap(world -> world.getEntities().stream())
                .filter(entity -> isNPC(entity)) // Aquí necesitas algún método que verifique si una entidad es un NPC
                .map(Entity::getUniqueId)
                .collect(Collectors.toList());
    }

    private boolean isNPC(Entity entity) {
        // Implementa esta función según cómo estés manejando NPCs
        return false; // Placeholder
    }

    private void centerCameraOnNPC(UUID uuid) {
        currentNPCUUID = uuid;
        Entity npc = Bukkit.getEntity(uuid);
        if (npc != null) {
            // Suponiendo que tu sistema de cámara tenga una función para centrarse en una
            // entidad:
            plugin.getCameraController().focusOnEntity(npc);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.distanceSquared(to) > 25 && plugin.isAutoCameraMode()) {
            changeCameraView(event.getPlayer(), "¡Error al cambiar la cámara debido al movimiento!");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (plugin.isAutoCameraMode()) {
            changeCameraView(event.getEntity(), "¡Error al cambiar la cámara debido a la muerte del jugador!");
        }
    }

    @EventHandler
    public void onPlayerCombat(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            if (plugin.isAutoCameraMode()) {
                changeCameraView(attacker, "¡Error al cambiar la cámara durante el combate!");
            }
        }
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        if (plugin.isAutoCameraMode()) {
            changeCameraView(event.getPlayer(), "¡Error al cambiar la cámara debido al avance conseguido!");
        }
    }

    private void changeCameraView(Player player, String errorMessage) {
        if (plugin.getCameraController() != null) {
            plugin.getCameraController().followPlayer(player.getUniqueId());
        } else {
            player.sendMessage(plugin.getConfig().getString("messages.cameraError", errorMessage));
        }
    }
}
