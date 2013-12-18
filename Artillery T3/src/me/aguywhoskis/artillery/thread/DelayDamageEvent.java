package me.aguywhoskis.artillery.thread;


import me.aguywhoskis.artillery.event.PlayerHandle;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;
 
public class DelayDamageEvent extends BukkitRunnable {
	private EntityDamageByEntityEvent e;

	public DelayDamageEvent(Plugin myplugin, EntityDamageByEntityEvent e) {
        this.e = e;
    }
 
    public void run() {
    	PlayerHandle.DelayedDamageEvent(this.e);
    }
}
