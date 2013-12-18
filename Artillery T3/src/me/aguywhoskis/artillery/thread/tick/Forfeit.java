package me.aguywhoskis.artillery.thread.tick;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.aguywhoskis.artillery.Artillery;
import me.aguywhoskis.artillery.util.Game;
import me.aguywhoskis.artillery.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Forfeit extends BukkitRunnable {
	
	public static Map<String, Integer> timer = new HashMap<String, Integer>();
	static int taskId;
	
	@SuppressWarnings("unused")
	private final JavaPlugin plugin;
	
	public static void start(JavaPlugin myplugin) {
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Artillery.plugin, new Forfeit(myplugin), 20L, 20L);
	}
	
	public static void stop(JavaPlugin myplugin) {
		Bukkit.getScheduler().cancelTask(taskId);
	}


	public Forfeit(Plugin myplugin) {
        this.plugin = (JavaPlugin) myplugin;
    }
	
	String prefix = "&0[&2!&0]&r ";
    public void run() {
    	for (Entry<String, Integer> entry : timer.entrySet()) {
    	    if (entry.getValue() < 1) {
    	    	String playerName = entry.getKey();
    	    	Player p = Bukkit.getPlayer(playerName);
    	    	if (p != null) {
	    	    	Util.messageServer(prefix+Bukkit.getPlayer(playerName).getDisplayName()+ChatColor.DARK_RED+" has forfeit!");
    	    	} else {
    	    		Util.messageServer(prefix+playerName+ChatColor.DARK_RED+" has forfeit");
    	    	}
    	    	
    	    	timer.remove(playerName);
    	    	Game.teamBlue.remove(p);
    	    	Game.teamRed.remove(p);
    	    } else {
    	    	timer.put(entry.getKey(), entry.getValue() -1);
    	    }
    	}
    }
}
