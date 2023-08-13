package com.MCTVObserver.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.MCTVObserver.MCTVObserver;
import com.MCTVObserver.camera.CameraController;
import com.MCTVObserver.camera.modes.DroneCameraHandler;

public class MCTVCommandExecutor implements CommandExecutor, TabCompleter {

    private MCTVObserver plugin;
    private CameraController cameraController;
    private DroneCameraHandler droneHandler;

    public MCTVCommandExecutor(MCTVObserver plugin, CameraController cameraController,
            DroneCameraHandler droneHandler) {
        this.plugin = plugin;
        this.cameraController = cameraController;
        this.droneHandler = droneHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("mctv") && args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "enable":
                    enableMCTVMode(player);
                    break;
                case "disable":
                    disableMCTVMode(player);
                    break;
                case "help":
                    displayHelp(player);
                    break;
                case "switch":
                    if (args.length > 1) {
                        switchCameraMode(player, args[1]);
                    } else {
                        player.sendMessage("Especifica el modo de cámara a cambiar.");
                    }
                    break;
                case "camera":
                    if (args.length > 1) {
                        handleCameraCommand(player, args);
                    } else {
                        player.sendMessage("Especifica el comando de la cámara.");
                    }
                    break;
                case "activatedrone":
                    if (droneHandler != null) {
                        droneHandler.activateDroneModeForPlayer(player);
                        player.sendMessage("Modo dron activado.");
                    } else {
                        player.sendMessage("Error: droneHandler no está inicializado.");
                    }
                    break;
                case "deactivatedrone":
                    if (droneHandler != null) {
                        droneHandler.deactivateDroneMode();
                        player.sendMessage("Modo dron desactivado.");
                    } else {
                        player.sendMessage("Error: droneHandler no está inicializado.");
                    }
                    break;
                default:
                    player.sendMessage("Comando desconocido. Usa /mctv help para obtener ayuda.");
                    break;
            }
            return true;
        }

        player.sendMessage("Usa /mctv help para obtener ayuda.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("mctv")) {
            if (args.length == 1) {
                return Arrays.asList("enable", "disable", "help", "switch", "camera", "activatedrone",
                        "deactivatedrone");
            } else if (args.length == 2 && "camera".equalsIgnoreCase(args[0])) {
                return Arrays.asList("start", "stop");
            }
        }
        return new ArrayList<>();
    }

    private void enableMCTVMode(Player player) {
        if (player.isOp()) {
            if (!cameraController.isPlayerInAutoMode(player)) {
                cameraController.startAutoModeForPlayer(player);
                player.sendMessage("Modo MCTV activado.");
            } else {
                player.sendMessage("Ya estás en el modo MCTV.");
            }
        } else {
            player.sendMessage("No tienes permisos para usar el modo MCTV.");
        }
    }

    private void disableMCTVMode(Player player) {
        if (cameraController.isPlayerInAutoMode(player)) {
            cameraController.stopCameraForPlayer(player);
            player.sendMessage("Modo MCTV desactivado.");
        } else {
            player.sendMessage("No estás en el modo MCTV.");
        }
    }

    private void displayHelp(Player player) {
        player.sendMessage("Comandos disponibles:");
        player.sendMessage("/mctv enable - Activa el modo MCTV (solo para OP)");
        player.sendMessage("/mctv disable - Desactiva el modo MCTV");
        player.sendMessage("/mctv switch - Cambia de modo");
        player.sendMessage("/mctv camera start/stop - Controla la cámara");
        player.sendMessage("/mctv activatedrone - Activa el modo dron");
        player.sendMessage("/mctv deactivatedrone - Desactiva el modo dron");
    }

    private void switchCameraMode(Player player, String modeName) {
        player.sendMessage("Los modos de cámara ya no están disponibles.");
    }

    private void handleCameraCommand(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("No tienes permisos para usar los comandos de cámara.");
            return;
        }

        if (args.length == 2) {
            if ("start".equalsIgnoreCase(args[1])) {
                cameraController.startAutoModeForPlayer(player);
                player.sendMessage("Cámara automática iniciada.");
            } else if ("stop".equalsIgnoreCase(args[1])) {
                cameraController.stopCameraForPlayer(player);
                player.sendMessage("Cámara automática detenida.");
            } else {
                player.sendMessage("Uso: /mctv camera [start|stop]");
            }
        }
    }
}