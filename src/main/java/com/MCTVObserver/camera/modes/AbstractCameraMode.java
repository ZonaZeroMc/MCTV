package com.MCTVObserver.camera.modes;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class AbstractCameraMode {

    protected Player getPlayerFromUUID(UUID playerUUID) {
        if (playerUUID == null) {
            throw new IllegalArgumentException("UUID cannot be null.");
        }
        return Bukkit.getPlayer(playerUUID);
    }

    protected void displayPlayerInfo(Player observer, Player target) {
        if (observer != null && target != null) {
            observer.sendMessage("Observando a: " + target.getName());
            observer.sendMessage("Vida: " + target.getHealth() + "/20");
            observer.sendMessage("Hambre: " + target.getFoodLevel() + "/20");
            // Puedes agregar más información aquí si lo necesitas.
        }
    }
}
