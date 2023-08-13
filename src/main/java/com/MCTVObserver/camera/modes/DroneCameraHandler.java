package com.MCTVObserver.camera.modes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.MCTVObserver.MCTVObserver;

public class DroneCameraHandler implements Listener {

    private final MCTVObserver plugin;
    private final Player player;
    private ArmorStand cameraDrone;
    private double angle = 0;

    public DroneCameraHandler(MCTVObserver plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void activateDroneModeForPlayer(Player player) {
        cameraDrone = player.getWorld().spawn(player.getLocation().add(0, 2, -3), ArmorStand.class);
        cameraDrone.setVisible(false);
        cameraDrone.setGravity(false);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().equals(player) && cameraDrone != null) {
            angle += Math.PI / 180;
            if (angle >= 2 * Math.PI)
                angle -= 2 * Math.PI;

            double x = player.getLocation().getX() + 3 * Math.sin(angle);
            double z = player.getLocation().getZ() + 3 * Math.cos(angle);

            Location droneLocation = new Location(player.getWorld(), x, player.getLocation().getY() + 2, z);
            droneLocation.setDirection(player.getLocation().subtract(droneLocation).toVector());
            cameraDrone.teleport(droneLocation);
        }
    }

    public void deactivateDroneMode() {
        if (cameraDrone != null) {
            cameraDrone.remove();
            cameraDrone = null;
        }
    }

    // Método añadido para mantener consistencia con la llamada desde
    // MCTVObserver.java
    public void deactivateDroneModeForPlayer(Player player) {
        deactivateDroneMode(); // Usamos el método ya existente ya que el Player ya es una variable de
                               // instancia
    }
}
