package me.aguywhoskis.artillery.thread.tick;

import java.util.ArrayList;
import java.util.Collections;

import me.aguywhoskis.artillery.util.PLUGIN;
import me.aguywhoskis.artillery.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Announcer extends BukkitRunnable {
	
	private static ArrayList<String> messages = new ArrayList<String>();
	private static ArrayList<String> messagesCopy = new ArrayList<String>();
	
	private static int taskId;
	
	public static void init() {
		messages.add("&cTo use a turret, stand on the centre, look at the block you want to shoot at, and sneak!");
		messages.add("&cDestroy all opposing team's beacons to win the game!");
		messages.add("&cKill players and turrets, get kill assists and destroy core blocks to get coins and exp!");
		messages.add("&cYou can purchase tools, turrets and obstacles with the shop items in your inventory!");
		messages.add("&cStaying in the game for the entire duration gives you bonus coins and exp!");
		messagesCopy = messages;
	}
	
	public static void start(Plugin myplugin) {
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(myplugin, new Announcer(), 900L, 900L);
	}
	
	public static void stop() {
		Bukkit.getScheduler().cancelTask(taskId);
	}
	
	public Announcer() {
		
	}

	@Override
	public void run() {
		if (messagesCopy.size() == 0) {
			messagesCopy = messages;
		}
		Collections.shuffle(messagesCopy);
		Util.messageServer(PLUGIN.prefix+messagesCopy.get(0));
		messagesCopy.remove(0);	
	}
}
