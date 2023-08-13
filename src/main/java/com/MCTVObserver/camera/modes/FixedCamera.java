package com.MCTVObserver.camera.modes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.MCTVObserver.interfaces.ICameraMode;

public class FixedCamera extends AbstractCameraMode implements ICameraMode {

    // Mapa para almacenar las ubicaciones originales de los observadores.
    private HashMap<UUID, Location> originalLocations = new HashMap<>();

    // Mapa que relaciona a un observador con su jugador objetivo.
    private HashMap<UUID, UUID> observerTargetMap = new HashMap<>();

    // Lista que contiene todas las ubicaciones fijas.
    private List<Location> fixedCameraLocations;

    // Mapa para almacenar las ubicaciones fijas para cada observador.
    private HashMap<UUID, Location> fixedLocations = new HashMap<>();

    // Constructor que recibe la lista de ubicaciones fijas.
    public FixedCamera(List<Location> fixedCameraLocations) {
        this.fixedCameraLocations = fixedCameraLocations != null ? new ArrayList<>(fixedCameraLocations)
                : new ArrayList<>();
    }

    @Override
    public void activate(UUID observerUUID, UUID targetUUID) {
        Player observer = getPlayerFromUUID(observerUUID);
        Player target = getPlayerFromUUID(targetUUID);

        if (observer != null && target != null && !fixedCameraLocations.isEmpty()) {
            // Almacena la ubicación original del observador.
            originalLocations.put(observerUUID, observer.getLocation());

            // Almacena el jugador objetivo para el observador.
            observerTargetMap.put(observerUUID, targetUUID);

            // Selecciona una ubicación fija al azar de la lista.
            Location fixedLoc = fixedCameraLocations.get(new Random().nextInt(fixedCameraLocations.size()));

            // Almacena esta ubicación fija.
            fixedLocations.put(observerUUID, fixedLoc);

            // Teleporta al observador a esta ubicación fija.
            observer.teleport(fixedLoc);

            // Mostrar información del jugador objetivo al observador.
            displayPlayerInfo(observer, target);
        }
    }

    @Override
    public void deactivate(UUID observerUUID) {
        Player observer = getPlayerFromUUID(observerUUID);

        if (observer != null && originalLocations.containsKey(observerUUID)) {
            // Vuelve al observador a su ubicación original.
            observer.teleport(originalLocations.get(observerUUID));

            // Limpia la información almacenada del observador.
            originalLocations.remove(observerUUID);
            observerTargetMap.remove(observerUUID);
            fixedLocations.remove(observerUUID);
        }
    }

    @Override
    public void update(UUID observerUUID) {
        // No es necesario actualizar para una cámara fija.
    }

    @Override
    public String getName(UUID observerUUID) {
        return "FixedCamera";
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
