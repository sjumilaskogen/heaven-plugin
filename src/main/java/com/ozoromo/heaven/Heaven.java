package com.ozoromo.heaven;


import org.bukkit.Bukkit;
import org.bukkit.Location;
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

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getLocation().getBlockY() >= config.getInt("heaven-height")) {

                        if (playerWorldHash(player) == heavenHash()) {
                            player.sendMessage(config.getString("Teleport-failed-Message"));
                        }else if (playerWorldHash(player) != heavenHash()) {
                            teleport(player, config.getString("Heaven-Dimension-Name"));
                            player.sendMessage(config.getString("Teleport-Message"));
                        }
                    }
                }
            }
        }, 100, 20*config.getInt("Height-check-delay-in-seconds"));
    }

    public int playerWorldHash (Player player) {
        int WorldHash = player.getWorld().getName().hashCode();
        return(WorldHash);
    }

    public int heavenHash () {
        int heaven = config.getString("Heaven-Dimension-Name").hashCode();
        return(heaven);
    }

    public void teleport (Player player, String dimension) {
        if(config.getBoolean("Relative-teleport")) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv tp " + player.getDisplayName() + " e:" + dimension + ":" + player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ());
        }else if(!config.getBoolean("Relative-teleport")) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv tp " + player.getDisplayName() + " " + dimension);
        }
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

}

