package me.aguywhoskis.artillery.thread.tick;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.aguywhoskis.artillery.event.PlayerHandle;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public class TurretDelay extends BukkitRunnable {
	
	public static Map<Location, Integer> turrets = new HashMap<Location, Integer>();

	@Override
	public void run() {
    	for (Entry<Location, Integer> e : turrets.entrySet()) {
    		if (e.getValue() < 1) {
    			turrets.remove(e.getKey());
    			continue;
    		} else {
    			turrets.put(e.getKey(), e.getValue() -1);
    		}
    	}

		
	}
	
	public TurretDelay() {
		
	}
	
	public static void addTurret(Location loc) {
		int delay = 0;
		Material m = loc.getWorld().getBlockAt(loc).getType();
		if (m.equals(Material.COAL_BLOCK)) {
			delay = 20*PlayerHandle.COAL_DELAY;
		} else if (m.equals(Material.IRON_BLOCK)) {
			delay = 20*PlayerHandle.IRON_DELAY;
		} else if (m.equals(Material.GOLD_BLOCK)) {
			delay = 20*PlayerHandle.GOLD_DELAY;
		} else if (m.equals(Material.EMERALD_BLOCK)) {
			delay = 20*PlayerHandle.EMERALD_DELAY;
		} else if (m.equals(Material.DIAMOND_BLOCK)) {
			delay = 20*PlayerHandle.DIAMOND_DELAY;
		}
		turrets.put(loc, delay);
	}
}
