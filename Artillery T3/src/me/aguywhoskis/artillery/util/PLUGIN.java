package me.aguywhoskis.artillery.util;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;


public class PLUGIN {

	public static String prefix = "&0[&2!&0]&r ";
	
	public static int gameMode = 0;
	public static ArrayList<Location> turretDelay = new ArrayList<Location>();

    public static boolean canShoot = false;
    public static boolean canBuild = false;
    public static boolean canBuy = false;
    public static boolean canPvp = false;
    public static boolean started = false;
    public static boolean isPaused = false;
	
    public static List<BukkitTask> pendingTasks= new ArrayList<BukkitTask>();
    
}
