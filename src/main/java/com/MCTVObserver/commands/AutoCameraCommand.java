package com.MCTVObserver.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.MCTVObserver.MCTVObserver;
import com.MCTVObserver.camera.CameraController;

public class AutoCameraCommand implements CommandExecutor, TabCompleter {

    private MCTVObserver plugin;
    private CameraController cameraController;

    public AutoCameraCommand(MCTVObserver plugin, CameraController cameraController) {
        this.plugin = plugin;
        this.cameraController = cameraController;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("autocamera")) {
            if (args.length == 0) {
                player.sendMessage("Usa /autocamera start para iniciar la cámara automática.");
                player.sendMessage("Usa /autocamera stop para detener la cámara automática.");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "start":
                    if (!cameraController.isPlayerInAutoMode(player)) {
                        cameraController.startAutoModeForPlayer(player);
                        player.sendMessage("Cámara automática iniciada.");
                    } else {
                        player.sendMessage("Ya estás en el modo de cámara automática.");
                    }
                    break;
                case "stop":
                    if (cameraController.isPlayerInAutoMode(player)) {
                        cameraController.stopCameraForPlayer(player);
                        player.sendMessage("Cámara automática detenida.");
                    } else {
                        player.sendMessage("No estás en el modo de cámara automática.");
                    }
                    break;
                case "help":
                default:
                    player.sendMessage("Usa /autocamera start para iniciar la cámara automática.");
                    player.sendMessage("Usa /autocamera stop para detener la cámara automática.");
                    break;
            }
            return true;
        }

        return false;
    }

    // Autocompletado con tabulación
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> list = new ArrayList<>();

        if (cmd.getName().equalsIgnoreCase("autocamera") && args.length == 1) {
            if ("start".startsWith(args[0].toLowerCase())) {
                list.add("start");
            }
            if ("stop".startsWith(args[0].toLowerCase())) {
                list.add("stop");
            }
            if ("help".startsWith(args[0].toLowerCase()) || args[0].isEmpty()) {
                list.add("help");
            }
        }
        return list;
    }
}
