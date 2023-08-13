package com.MCTVObserver.camera.modes;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.MCTVObserver.interfaces.ICameraMode;

public class FirstPersonCamera extends AbstractCameraMode implements ICameraMode {

    // NOTA: Ajusta estos valores según las necesidades del plugin:
    private static final double CAMERA_DISTANCE = 3.0; // Distancia de la cámara detrás del jugador
    private static final double CAMERA_HEIGHT_OFFSET = 1.5; // Altura por encima del jugador
    private static final double CAMERA_PITCH_OFFSET = 10.0; // Corrección del ángulo de inclinación

    private final ConcurrentHashMap<UUID, UUID> observersTargets = new ConcurrentHashMap<>();

    @Override
    public void activate(UUID observerUUID, UUID targetUUID) {
        if (observerUUID == null || targetUUID == null) {
            return;
        }

        observersTargets.put(observerUUID, targetUUID);
        Player observer = getPlayerFromUUID(observerUUID);
        Player target = getPlayerFromUUID(targetUUID);

        if (observer != null && target != null) {
            observer.setGameMode(GameMode.SPECTATOR);
            observer.teleport(target.getLocation());
            displayPlayerInfo(observer, target);
            update(observerUUID);
        }
    }

    @Override
    public void deactivate(UUID observerUUID) {
        Player observer = getPlayerFromUUID(observerUUID);
        if (observer != null) {
            observer.setGameMode(GameMode.SPECTATOR);
            observersTargets.remove(observerUUID);
        }
    }

    @Override
    public void update(UUID observerUUID) {
        UUID targetUUID = observersTargets.get(observerUUID);
        Player observer = getPlayerFromUUID(observerUUID);
        Player target = getPlayerFromUUID(targetUUID);

        if (observer != null && target != null) {
            adjustCameraPosition(observer, target);
            displayPlayerInfo(observer, target);
        }
    }

    private void adjustCameraPosition(Player observer, Player target) {
        Location targetLocation = target.getLocation();
        Vector direction = targetLocation.toVector().subtract(observer.getLocation().toVector());

        // Validar antes de normalizar
        if (direction.lengthSquared() == 0) {
            // El observador y el objetivo están en la misma ubicación
            return;
        }

        direction = direction.normalize();

        Location newCameraLocation = targetLocation.clone()
                .add(direction.multiply(-CAMERA_DISTANCE))
                .add(new Vector(0, CAMERA_HEIGHT_OFFSET, 0));

        float yaw = (float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));

        // Asegurarse de que el valor Y del vector direction esté en el rango adecuado
        // para asin
        double dy = Math.max(-1.0, Math.min(1.0, direction.getY()));
        float pitch = (float) (Math.toDegrees(Math.asin(dy)) - CAMERA_PITCH_OFFSET);

        newCameraLocation.setYaw(yaw);
        newCameraLocation.setPitch(pitch);

        observer.teleport(newCameraLocation);
    }

    @Override
    public String getName(UUID observerUUID) {
        return "FirstPersonCamera"; // NOTA: Puedes cambiar el nombre si es necesario
    }

    @Override
    public boolean isActive(UUID observerUUID) {
        return observersTargets.containsKey(observerUUID);
    }

    @Override
    public UUID getTarget(UUID observerUUID) {
        return observersTargets.get(observerUUID);
    }
}
