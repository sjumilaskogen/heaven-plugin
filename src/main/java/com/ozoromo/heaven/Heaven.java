package com.ozoromo.heaven;


import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.logging.Logger;

public class Heaven extends JavaPlugin {
    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {

        Logger log = Bukkit.getLogger();
        log.info("Heaven enabled");

        SetupConfig();

        if(config.getBoolean("Relative-teleport")) {
            log.info(ChatColor.RED + "Relative teleport is enabled, this could lead to problems. If problems are encountered try disabling in in the config");
        }

        World HeavenWorld = Bukkit.getServer().getWorld(config.getString("Heaven-Dimension-Name"));

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getLocation().getBlockY() >= config.getInt("heaven-height")) {

                        if (playerWorldHash(player) == heavenHash()) {
                            player.sendMessage(config.getString("Teleport-failed-Message"));
                        }else if (playerWorldHash(player) != heavenHash()) {
                            teleport(player, HeavenWorld);
                            player.sendMessage(config.getString("Teleport-Message"));
                        }
                    }
                }
            }
        }, 100, 20*config.getInt("Height-check-delay-in-seconds"));
    }

    public void SetupConfig () {
        config.addDefault("heaven-height", 250);
        config.addDefault("Height-check-delay-in-seconds", 5);
        config.addDefault("Heaven-Dimension-Name", "Heaven");
        config.addDefault("Teleport-Message", "You are now in heaven");
        config.addDefault("Teleport-failed-Message", "You are already in heaven!");
        config.addDefault("Relative-teleport", false);
        config.options().copyDefaults(true);
        saveConfig();
    }

    public int playerWorldHash (Player player) {
        return(player.getWorld().getName().hashCode());
    }
    public int heavenHash () {
        return(config.getString("Heaven-Dimension-Name").hashCode());
    }

    public void teleport (Player player, World HeavenWorld) {
        if(config.getBoolean("Relative-teleport")) {
            Location RelativeHeaven = HeavenWorld.getSpawnLocation();

            RelativeHeaven.setX(player.getLocation().getBlockX());
            RelativeHeaven.setZ(player.getLocation().getBlockZ());
            RelativeHeaven.setY(HeavenWorld.getHighestBlockYAt(RelativeHeaven));

            player.teleport(RelativeHeaven);

        }else if(!config.getBoolean("Relative-teleport")) {
            Location HeavenSpawn = HeavenWorld.getSpawnLocation();
            player.teleport(HeavenSpawn);
        }
    }
}


