package me.aguywhoskis.artillery.thread;


import me.aguywhoskis.artillery.event.PlayerHandle;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
 
public class DelayDeathEvent extends BukkitRunnable {
	private PlayerDeathEvent e;

	public DelayDeathEvent(Plugin myplugin, PlayerDeathEvent e) {
        this.e = e;
    }
 
    public void run() {
    	PlayerHandle.delayedDeathEvent(e);
    }
}
