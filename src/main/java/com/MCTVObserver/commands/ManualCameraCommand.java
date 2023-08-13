package com.MCTVObserver.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.MCTVObserver.MCTVObserver;

public class ManualCameraCommand implements CommandExecutor, TabCompleter {

    private MCTVObserver plugin;

    public ManualCameraCommand(MCTVObserver plugin) {
        this.plugin = plugin;
        plugin.getCommand("manualcam").setTabCompleter(this); // Asigna el TabCompleter al comando
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("manualcam")) {
            if (args.length == 0) {
                player.sendMessage("Usa /manualcam help para obtener ayuda.");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "follow":
                    if (args.length > 1) {
                        followPlayer(player, args[1]);
                    } else {
                        player.sendMessage(
                                "Especifica a qué jugador quieres seguir. Ejemplo: /manualcam follow [nombreJugador]");
                    }
                    break;
                case "stop":
                    stopFollowing(player);
                    break;
                case "help":
                    displayHelp(player);
                    break;
                default:
                    player.sendMessage("Comando desconocido. Usa /manualcam help para obtener ayuda.");
                    break;
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("follow");
            suggestions.add("stop");
            suggestions.add("help");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("follow")) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                suggestions.add(p.getName());
            }
        }

        return suggestions;
    }

    // Métodos auxiliares

    private void followPlayer(Player player, String targetPlayerName) {
        Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);
        if (targetPlayer != null) {
            plugin.getCameraController().followPlayer(targetPlayer.getUniqueId());
            player.sendMessage("Ahora sigues a " + targetPlayerName + ".");
        } else {
            player.sendMessage("El jugador no está en línea.");
        }
    }

    private void stopFollowing(Player player) {
        plugin.getCameraController().stopCameraForPlayer(player);
        player.sendMessage("Has dejado de seguir al jugador.");
    }

    private void displayHelp(Player player) {
        player.sendMessage("Comandos de cámara manual:");
        player.sendMessage("/manualcam follow [nombreJugador] - Sigue manualmente a un jugador.");
        player.sendMessage("/manualcam stop - Detiene el seguimiento manual.");
        player.sendMessage("/manualcam help - Muestra este mensaje de ayuda.");
    }
}
