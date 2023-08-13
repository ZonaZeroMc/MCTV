package com.MCTVObserver.camera;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.MCTVObserver.MCTVObserver;
import com.MCTVObserver.camera.modes.FirstPersonCamera;
import com.MCTVObserver.camera.modes.FixedCamera;
import com.MCTVObserver.camera.modes.FollowPlayerCamera;
import com.MCTVObserver.interfaces.ICameraMode;

import net.citizensnpcs.api.CitizensAPI;

public class CameraController {

    private final MCTVObserver plugin;
    private Player observer;
    private UUID targetUUID;
    private ICameraMode currentMode;
    private BukkitRunnable cameraTask;
    private final Random random = new Random();

    private FollowPlayerCamera followPlayerCamera;
    private FixedCamera fixedCamera;
    private FirstPersonCamera firstPersonCamera;

    private long autoSwitchDuration;
    private List<Location> fixedCameraLocations;
    private int followDistance;
    private double angle = 0.0;

    public CameraController(MCTVObserver plugin) {
        this.plugin = plugin;
        loadConfigurations();
    }

    private void loadConfigurations() {
        FileConfiguration config = plugin.getConfig();
        autoSwitchDuration = config.getLong("camera.auto-switch-duration", 400L);
        fixedCameraLocations = new ArrayList<>();
        for (String locStr : config.getStringList("camera.fixed-camera-locations")) {
            String[] parts = locStr.split(",");
            if (parts.length == 4) {
                World world = Bukkit.getWorld(parts[0]);
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                double z = Double.parseDouble(parts[3]);
                fixedCameraLocations.add(new Location(world, x, y, z));
            }
        }
        followDistance = config.getInt("camera.follow-distance", 5);
        this.followPlayerCamera = new FollowPlayerCamera(followDistance);
        this.fixedCamera = new FixedCamera(fixedCameraLocations);
        this.firstPersonCamera = new FirstPersonCamera();
    }

    private boolean isNPC(Entity entity) {
        return CitizensAPI.getNPCRegistry().isNPC(entity);
    }

    public void setObserver(Player player) {
        this.observer = player;
    }

    public void setCameraMode(ICameraMode newMode) {
        if (observer == null)
            return;
        if (currentMode != null) {
            currentMode.deactivate(observer.getUniqueId());
        }
        this.currentMode = newMode;
        if (targetUUID != null) {
            Player target = Bukkit.getPlayer(targetUUID);
            if (target != null) {
                this.currentMode.activate(observer.getUniqueId(), targetUUID);
            } else {
                observer.sendMessage("El jugador objetivo no fue encontrado.");
            }
        } else {
            this.currentMode.activate(observer.getUniqueId(), null);
        }
    }

    public void setFollowPlayerCamera() {
        setCameraMode(followPlayerCamera);
    }

    public void setFixedCamera() {
        setCameraMode(fixedCamera);
    }

    public void setFirstPersonCamera() {
        setCameraMode(firstPersonCamera);
    }

    public void stopFollowing() {
        if (observer == null)
            return;
        this.targetUUID = null;
        if (currentMode != null) {
            currentMode.deactivate(observer.getUniqueId());
            observer.sendMessage("Has dejado de seguir al jugador.");
        }
    }

    public void followPlayer(UUID targetPlayerUUID) {
        if (observer == null)
            return;
        if (!isValidObserver()) {
            observer.sendMessage("¡Debes estar en modo espectador para seguir a otros jugadores!");
            return;
        }
        this.targetUUID = targetPlayerUUID;
        if (currentMode instanceof FollowPlayerCamera) {
            currentMode.update(observer.getUniqueId());
        } else {
            setFollowPlayerCamera();
        }
    }

    private boolean isValidObserver() {
        return observer != null && observer.getGameMode() == GameMode.SPECTATOR;
    }

    private void switchCameraRandomly() {
        if (observer == null || observer.getGameMode() != GameMode.SPECTATOR)
            return;
        int choice = random.nextInt(3);
        switch (choice) {
            case 0:
                switchToRandomNPC();
                break;
            case 1:
                setFixedCamera();
                break;
            case 2:
                setFirstPersonCamera();
                break;
        }
    }

    public void startAutoCameraSwitch(long ticks) {
        if (cameraTask != null)
            cameraTask.cancel();
        cameraTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() <= 1) {
                    switchToRandomNPC();
                } else {
                    switchCameraRandomly();
                }
            }
        };
        cameraTask.runTaskTimer(plugin, ticks, ticks);
    }

    public void focusOnEntity(Entity entity) {
        if (observer != null && entity != null) {
            observer.setSpectatorTarget(entity);
        }
    }

    private void switchToRandomNPC() {
        List<Entity> entities = observer.getWorld().getEntities();
        List<Entity> npcs = new ArrayList<>();
        for (Entity entity : entities) {
            if (isNPC(entity)) {
                npcs.add(entity);
            }
        }
        if (!npcs.isEmpty()) {
            Entity randomNPC = npcs.get(random.nextInt(npcs.size()));
            positionCameraRelativeToNPC(randomNPC);
        }
    }

    public void positionCameraRelativeToNPC(Entity npc) {
        if (observer == null || npc == null)
            return;

        Location npcLocation = npc.getLocation().clone();
        double distanceBehind = 5.0;
        double heightAbove = 3.0;
        Location endLocation = npcLocation.add(npcLocation.getDirection().multiply(-distanceBehind)).add(0, heightAbove,
                0);

        BukkitRunnable droneMovement = new BukkitRunnable() {
            double t = 0;
            double step = 0.05; // Puedes ajustar este valor para cambiar la velocidad del dron

            public void run() {
                if (t >= 1) {
                    this.cancel();
                    return;
                }
                Location interpolatedLocation = interpolateLocation(observer.getLocation(), endLocation, t);
                observer.teleport(interpolatedLocation);
                t += step;
            }
        };
        droneMovement.runTaskTimer(plugin, 0, 1);
    }

    private Location interpolateLocation(Location start, Location end, double t) {
        double x = (1 - t) * start.getX() + t * end.getX();
        double y = (1 - t) * start.getY() + t * end.getY();
        double z = (1 - t) * start.getZ() + t * end.getZ();
        Location result = new Location(start.getWorld(), x, y, z);
        result.setDirection(end.getDirection());
        return result;
    }

    public void stopAutoCameraSwitch() {
        if (cameraTask != null) {
            cameraTask.cancel();
            cameraTask = null;
        }
    }

    public void startAutoModeForPlayer(Player player) {
        this.setObserver(player);
        this.startAutoCameraSwitch(autoSwitchDuration);
    }

    public void stopCameraForPlayer(Player player) {
        if (observer != null && observer.equals(player)) {
            this.stopAutoCameraSwitch();
            if (currentMode != null) {
                currentMode.deactivate(observer.getUniqueId());
            }
        }
    }

    public boolean isPlayerInAutoMode(Player player) {
        return observer != null && observer.equals(player) && cameraTask != null;
    }

    public void stopRandomCameraSwitch() {
        stopAutoCameraSwitch();
    }

    public void startRandomCameraSwitch(long ticks) {
        startAutoCameraSwitch(ticks);
    }

    // Dolly In/Out
    public void dollyIn(Entity npc, double distance) {
        Location cameraLocation = observer.getLocation();
        Vector direction = npc.getLocation().toVector().subtract(cameraLocation.toVector()).normalize();
        cameraLocation.add(direction.multiply(distance));
        observer.teleport(cameraLocation);
    }

    // Panorámica
    public void panAround(Entity npc, double angleIncrement) {
        double distanceToNPC = 5.0;
        angle += angleIncrement; // Incrementa el ángulo para cambiar la posición de la cámara
        double offsetX = distanceToNPC * Math.sin(angle);
        double offsetZ = distanceToNPC * Math.cos(angle);
        Location cameraLocation = npc.getLocation().clone().add(offsetX, 1.5, offsetZ);
        observer.teleport(cameraLocation);
        observer.getLocation().setDirection(npc.getLocation().subtract(cameraLocation).toVector());
    }

    // Boom o Jib Shot
    public void jibShot(double heightIncrement) {
        Location cameraLocation = observer.getLocation();
        cameraLocation.add(0, heightIncrement, 0);
        observer.teleport(cameraLocation);
    }

    // Tracking o Travelling Shot
    public void trackNPC(Entity npc) {
        double distanceBehind = 5.0;
        Vector direction = npc.getLocation().toVector().subtract(observer.getLocation().toVector()).normalize();
        Location cameraLocation = npc.getLocation().clone().subtract(direction.multiply(distanceBehind));
        observer.teleport(cameraLocation);
    }

    // Tilt
    public void tilt(double pitchIncrement) {
        float newPitch = observer.getLocation().getPitch() + (float) pitchIncrement;
        observer.getLocation().setPitch(newPitch);
    }
}
