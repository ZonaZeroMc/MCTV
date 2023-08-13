package com.MCTVObserver;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.MCTVObserver.camera.CameraController;
import com.MCTVObserver.camera.modes.DroneCameraHandler;
import com.MCTVObserver.camera.modes.FirstPersonCamera;
import com.MCTVObserver.commands.AutoCameraCommand;
import com.MCTVObserver.commands.MCTVCommandExecutor;
import com.MCTVObserver.listeners.ChatListener;
import com.MCTVObserver.listeners.PlayerActionListener;
import com.MCTVObserver.listeners.SpectatorModeListener;

public class MCTVObserver extends JavaPlugin {

    private boolean autoCameraMode;
    private CameraController cameraController;
    private DroneCameraHandler droneCameraHandler;

    @Override
    public void onEnable() {
        handlePluginSetup();
        getLogger().info("MCTVObserver has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MCTVObserver has been disabled!");
    }

    public boolean isAutoCameraMode() {
        return autoCameraMode;
    }

    public void setAutoCameraMode(boolean mode) {
        this.autoCameraMode = mode;
        updateAutoCameraModeConfig(mode);
        if (autoCameraMode) {
            startRandomCameraSwitch();
        } else {
            cameraController.stopRandomCameraSwitch();
        }
    }

    public CameraController getCameraController() {
        return cameraController;
    }

    public void enableMCTVModeForPlayer(Player player) {
        if (player.isOp()) {
            activateMCTVMode(player);
        } else {
            sendPermissionDeniedMessage(player);
        }
    }

    public void disableMCTVModeForPlayer(Player player) {
        deactivateMCTVMode(player);
    }

    public void enableDroneModeForPlayer(Player player) {
        if (player.isOp()) {
            if (droneCameraHandler == null) {
                droneCameraHandler = new DroneCameraHandler(this, player);
                droneCameraHandler.activateDroneModeForPlayer(player);
            } else {
                sendMessageOrFallback(player, "messages.droneAlreadyActive",
                        "El dron ya está activo por otro jugador.");
            }
        } else {
            sendPermissionDeniedMessage(player);
        }
    }

    public void disableDroneModeForPlayer(Player player) {
        if (player.isOp()) {
            if (droneCameraHandler != null) {
                droneCameraHandler.deactivateDroneMode();
                droneCameraHandler = null;
            }
        } else {
            sendPermissionDeniedMessage(player);
        }
    }

    private void handlePluginSetup() {
        saveDefaultConfig();
        loadConfiguration();
        initializeCameraController();
        registerListeners();
        registerCommands();

        if (autoCameraMode) {
            startRandomCameraSwitch();
        }
    }

    private void startRandomCameraSwitch() {
        long ticks = getConfig().getLong("cameraSwitchFrequency", 10) * 20L;
        cameraController.startRandomCameraSwitch(ticks);
    }

    private void activateMCTVMode(Player player) {
        GameMode mctvGameMode = getValidGameMode("mctvMode", "SPECTATOR");
        player.setGameMode(mctvGameMode);
        cameraController.setObserver(player);

        FirstPersonCamera firstPersonCameraMode = new FirstPersonCamera();
        firstPersonCameraMode.activate(player.getUniqueId(), player.getUniqueId());
        cameraController.setCameraMode(firstPersonCameraMode);

        sendMessageOrFallback(player, "messages.mctvEnabled", "You have enabled MCTV mode.");
    }

    private void deactivateMCTVMode(Player player) {
        GameMode defaultGameMode = getValidGameMode("defaultMode", "SURVIVAL");
        player.setGameMode(defaultGameMode);
        cameraController.setObserver(player);
        cameraController.stopFollowing();
        sendMessageOrFallback(player, "messages.mctvDisabled", "You have disabled MCTV mode.");
    }

    private void sendPermissionDeniedMessage(Player player) {
        sendMessageOrFallback(player, "messages.noPermission", "You do not have permission to use this command.");
    }

    private void updateAutoCameraModeConfig(boolean mode) {
        getConfig().set("autoCameraMode", mode);
        saveConfig();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new SpectatorModeListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerActionListener(this), this);
        if (droneCameraHandler != null) {
            getServer().getPluginManager().registerEvents(droneCameraHandler, this);
        }
    }

    private void registerCommands() {
        this.getCommand("mctv").setExecutor(new MCTVCommandExecutor(this, cameraController, droneCameraHandler));

        AutoCameraCommand autoCameraCommand = new AutoCameraCommand(this, cameraController);
        this.getCommand("autocamera").setExecutor(autoCameraCommand);
        this.getCommand("autocamera").setTabCompleter(autoCameraCommand);
    }

    private void initializeCameraController() {
        cameraController = new CameraController(this);
    }

    private void loadConfiguration() {
        autoCameraMode = getConfig().getBoolean("autoCameraMode", true);
    }

    public void sendMessageOrFallback(Player player, String path, String fallbackMessage) {
        if (getConfig() == null) {
            getLogger().warning("La configuración es null cuando intenta enviar un mensaje.");
        }

        String message = getConfig().getString(path, fallbackMessage);
        player.sendMessage(message);
    }

    private GameMode getValidGameMode(String configKey, String defaultValue) {
        String modeName = getConfig().getString(configKey, defaultValue).toUpperCase();
        try {
            return GameMode.valueOf(modeName);
        } catch (IllegalArgumentException e) {
            getLogger().warning("Config value for '" + configKey + "' is not a valid GameMode. Falling back to "
                    + defaultValue + ".");
            return GameMode.valueOf(defaultValue);
        }
    }
}
// hola