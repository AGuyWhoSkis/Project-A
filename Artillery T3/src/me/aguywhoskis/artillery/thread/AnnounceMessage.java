package me.aguywhoskis.artillery.thread;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

public class AnnounceMessage extends BukkitRunnable {

    private String message;


    public AnnounceMessage (Plugin myplugin, String message) {
        this.message = message;
    }

    public void run() {
        this.message = ChatColor.translateAlternateColorCodes('&',message);
        Bukkit.broadcastMessage(message);
    }
}
