package com.MCTVObserver.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.MCTVObserver.MCTVObserver;

public class TransitionUtils {

    private MCTVObserver plugin;

    public TransitionUtils(MCTVObserver plugin) {
        this.plugin = plugin;
    }

    public void smoothTransition(final Player player, Location targetLocation, final long duration) {
        final Location startLocation = player.getLocation();

        final double dx = (targetLocation.getX() - startLocation.getX()) / duration;
        final double dy = (targetLocation.getY() - startLocation.getY()) / duration;
        final double dz = (targetLocation.getZ() - startLocation.getZ()) / duration;
        final float dpitch = (targetLocation.getPitch() - startLocation.getPitch()) / duration;
        final float dyaw = (targetLocation.getYaw() - startLocation.getYaw()) / duration;

        new BukkitRunnable() {
            long elapsedTicks = 0;
            double newX = startLocation.getX();
            double newY = startLocation.getY();
            double newZ = startLocation.getZ();
            float newPitch = startLocation.getPitch();
            float newYaw = startLocation.getYaw();

            @Override
            public void run() {
                if (elapsedTicks++ >= duration) {
                    this.cancel();
                    return;
                }

                newX += dx;
                newY += dy;
                newZ += dz;
                newPitch += dpitch;
                newYaw += dyaw;

                Location newLocation = new Location(player.getWorld(), newX, newY, newZ, newYaw, newPitch);
                player.teleport(newLocation);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void smoothRotation(final Player player, float targetYaw, float targetPitch, final long duration) {
        final float startYaw = player.getLocation().getYaw();
        final float startPitch = player.getLocation().getPitch();

        final float dyaw = (targetYaw - startYaw) / duration;
        final float dpitch = (targetPitch - startPitch) / duration;

        new BukkitRunnable() {
            long elapsedTicks = 0;
            float newYaw = startYaw;
            float newPitch = startPitch;

            @Override
            public void run() {
                if (elapsedTicks++ >= duration) {
                    this.cancel();
                    return;
                }

                newYaw += dyaw;
                newPitch += dpitch;

                Location newLoc = player.getLocation();
                newLoc.setYaw(newYaw);
                newLoc.setPitch(newPitch);
                player.teleport(newLoc);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
