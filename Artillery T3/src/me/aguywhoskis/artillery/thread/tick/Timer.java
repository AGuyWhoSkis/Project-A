package me.aguywhoskis.artillery.thread.tick;


import java.util.Arrays;
import java.util.List;

import me.aguywhoskis.artillery.Artillery;
import me.aguywhoskis.artillery.util.Game;
import me.aguywhoskis.artillery.util.Util;
import me.aguywhoskis.artillery.util.WORLD;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer extends BukkitRunnable{
	private static int taskId;
	private static boolean paused = false;
	private static boolean shortened = false;
	
	private static int seconds = 120;
	private List<Integer> sigSeconds = Arrays.asList(120,90,60,30,15,10,5,3,2,1);
	private List<Integer> sigMapSeconds = Arrays.asList(70,45,13);
	
	private static String prefix = "&0[&2!&0]&r ";
	
	private static final int MIN_PLAYERS = 8;
	private static final int MAX_PLAYERS = Bukkit.getServer().getMaxPlayers();
	
	
	public void reset() {
		paused = false;

		shortened = false;
		seconds = 120;
	}
	
	@Override
	public void run() {
		if (!paused) {
			if (sigSeconds.contains(seconds)) {
				Util.messageServer(prefix +"&cNext match in: "+ChatColor.GOLD + optimizeSeconds(seconds));
			}
			if (sigMapSeconds.contains(seconds)) {
				Util.messageServer(prefix +"&cNext map is \""+ChatColor.GOLD+WORLD.map.getName()+"&c\" in: "+ChatColor.GOLD + optimizeSeconds(seconds));
			}
			if (seconds < 1) {
				Game.changeMode();
				stop();
				reset();
				return;
			}
			set(seconds-1, false, null);
		}
	}
	
	public Timer() {
		
	}

	public static void start() {
		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Artillery.plugin, new Timer(), 20L, 20L);
	}
	
	public static void stop() {
		Bukkit.getScheduler().cancelTask(taskId);
	}
	
	public static void pause() {
		Util.messageServer(prefix +"&cThe timer has been paused until more players join.");
		paused = true;
	}
	
	public static void resume() {
		if (Bukkit.getOnlinePlayers().length != 1) {
			Util.messageServer(prefix +"&cThe timer has been resumed.");
		}
		paused = false;
	}
	
	
	public static void update() {
		int n = Bukkit.getOnlinePlayers().length;
		int min = MIN_PLAYERS;
		int max = MAX_PLAYERS;
		
		if (n >= min) {
			//Minimum amount of players
			if (paused) {
				resume();

			}
			
		} else {
			//Less than minimum amount of players
			if (!paused) {
				pause();
			}
			if (shortened) {
				set(seconds+15, true, "l");
				shortened = false;
			}
		}
		if (!shortened) {
			if (n == max) {
				if (seconds > 45) {
					set(45, true, "s");
					shortened = true;
				}
			}
		}
		if (n == 0) {
			pause();
			set(0, false, null);
		}
	}
	
	public static void update(int n) {
		int min = MIN_PLAYERS;
		int max = MAX_PLAYERS;
		
		if (n >= min) {
			//Minimum amount of players
			if (paused) {
				resume();

			}
			
		} else {
			//Less than minimum amount of players
			if (!paused) {
				pause();
			}
		}
		if (!shortened) {
			if (n == max) {
				if (seconds > 45) {
					set(45, true, "s");
					shortened = true;
				}
			}
		}
		if (n == 0) {
			pause();
			set(0, false, null);
		}
	}
	
	//Sets the time of the timer in seconds. Type of "l" is lengthen, Type of "s" is shorten.
	public static void set (int n, boolean shouldAnnounce, String type) {
		Timer.seconds = n;
		if (shouldAnnounce) {
			if (type == "s") {
				Util.messageServer(prefix +"&cThe timer has been reduced to "+ChatColor.GOLD+optimizeSeconds(n)+"&c!");
			} else if (type == "l") {
				Util.messageServer(prefix +"&cThe timer has been increased to "+ChatColor.GOLD+optimizeSeconds(n)+"&c!");
			}
		}
	}
	
	public static String optimizeSeconds(int secs) {
		String s = Integer.toString(secs)+" seconds";
		
		if (secs == 120) {
			s = "2 minutes";
		} else if (secs == 60) {
			s = "1 minute";
		} else if (secs == 1) {
			s = "1 second";
		} else if (secs < 120 && secs > 60) {
			s = "1 minute "+Integer.toString(secs-60)+" seconds";
		} else {
			s = Integer.toString(secs)+" seconds";
		}
		return s;
	}
}
