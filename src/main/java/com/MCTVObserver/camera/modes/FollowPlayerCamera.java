package com.MCTVObserver.camera.modes;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.MCTVObserver.interfaces.ICameraMode;

public class FollowPlayerCamera extends AbstractCameraMode implements ICameraMode {

    private HashMap<UUID, Location> originalLocations = new HashMap<>();
    private HashMap<UUID, UUID> observerTargetMap = new HashMap<>();
    private double distance; // Variable para la distancia

    // Valores mínimos y máximos para la distancia.
    // TODO: Modifica estos valores según tus necesidades.
    private static final double MIN_DISTANCE = 1.0; // (Valor tipo double) Distancia mínima detrás del jugador.
    private static final double MAX_DISTANCE = 10.0; // (Valor tipo double) Distancia máxima detrás del jugador.

    public FollowPlayerCamera(double configuredDistance) {
        // Verificar y ajustar la distancia si está fuera de los límites
        if (configuredDistance < MIN_DISTANCE) {
            this.distance = MIN_DISTANCE;
        } else if (configuredDistance > MAX_DISTANCE) {
            this.distance = MAX_DISTANCE;
        } else {
            this.distance = configuredDistance;
        }
    }

    @Override
    public void activate(UUID observerUUID, UUID targetUUID) {
        Player observer = getPlayerFromUUID(observerUUID);
        if (observer != null) {
            originalLocations.put(observerUUID, observer.getLocation());
            observerTargetMap.put(observerUUID, targetUUID);
            update(observerUUID);
        }
    }

    @Override
    public void deactivate(UUID observerUUID) {
        Player observer = getPlayerFromUUID(observerUUID);
        if (observer != null && originalLocations.containsKey(observerUUID)) {
            observer.teleport(originalLocations.get(observerUUID));
            originalLocations.remove(observerUUID);
            observerTargetMap.remove(observerUUID);
        }
    }

    @Override
    public void update(UUID observerUUID) {
        Player observer = getPlayerFromUUID(observerUUID);
        UUID targetUUID = observerTargetMap.get(observerUUID);
        Player target = getPlayerFromUUID(targetUUID);
        if (observer != null && target != null) {
            Location targetLoc = target.getLocation();

            // Mantener una cierta distancia detrás del jugador objetivo
            Vector dir = targetLoc.getDirection().normalize();
            Location behindTarget = targetLoc.subtract(dir.multiply(distance));
            observer.teleport(behindTarget);

            displayPlayerInfo(observer, target);
        }
    }

    protected void displayPlayerInfo(Player observer, Player target) {
        // TODO: Aquí puedes modificar o agregar más información si lo consideras
        // necesario.
        observer.sendMessage("Observando a: " + target.getName());
    }

    @Override
    public String getName(UUID observerUUID) {
        // TODO: Si quieres cambiar el nombre de este modo de cámara, hazlo aquí.
        return "FollowPlayerCamera";
    }

    @Override
    public boolean isActive(UUID observerUUID) {
        return observerTargetMap.containsKey(observerUUID);
    }

    @Override
    public UUID getTarget(UUID observerUUID) {
        return observerTargetMap.get(observerUUID);
    }
}
