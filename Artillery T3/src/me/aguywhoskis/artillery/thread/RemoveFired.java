package me.aguywhoskis.artillery.thread;

import me.aguywhoskis.artillery.util.PLUGIN;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
 
public class RemoveFired extends BukkitRunnable {
	private Location loc;

	public RemoveFired(Location loc) {
        this.loc = loc;
    }
 
    public void run() {
        PLUGIN.turretDelay.remove(loc);
    }
}
