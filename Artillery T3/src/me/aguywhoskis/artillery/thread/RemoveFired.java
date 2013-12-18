package me.aguywhoskis.artillery.thread;

import me.aguywhoskis.artillery.util.PLUGIN;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;
 
public class RemoveFired extends BukkitRunnable {
	private Location loc;
	private Plugin myplugin;

	public RemoveFired(Plugin myplugin, Location loc) {
        this.loc = loc;
        this.myplugin = myplugin;
    }
 
    public void run() {
        PLUGIN.turretDelay.remove(loc);
    }
}
