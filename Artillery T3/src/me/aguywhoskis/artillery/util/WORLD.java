package me.aguywhoskis.artillery.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import me.aguywhoskis.artillery.Artillery;

import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WORLD {
	
	public static Location redSpawn;
	public static Location blueSpawn;
	public static ArrayList<Location> blueCore = new ArrayList<Location>();
	public static ArrayList<Location> redCore = new ArrayList<Location>();
	public static World lastWorld = Bukkit.getServer().getWorlds().get(0);
	public static World map = lastWorld;
	public static World main = Bukkit.getWorlds().get(0);
	public static Location mainSpawn;
	
	
	Plugin myplugin = Artillery.plugin;
	
	
	public static void saveLocToFile(Location loc, String name) throws IOException {
    	File f = new File(loc.getWorld().getWorldFolder(), name);
    	if (!f.exists()) {
    		f.createNewFile();
    	}
		FileWriter writer = new FileWriter(f);
		writer.write(loc.getWorld()+","+loc.getX()+","+loc.getY()+","+loc.getZ()+","+loc.getYaw()+","+loc.getPitch());
		writer.flush(); writer.close();
	}
	
	public static Location getLocFromFile(World w, String name) throws IOException {
    	File f = new File (w.getWorldFolder(), name);
    	if (f.exists()) {
			String l = FileUtils.readFileToString(f);
			String[] s = l.split(",");
			Location loc = new Location(w, Double.parseDouble(s[1]), Double.parseDouble(s[2]), Double.parseDouble(s[3]));
			loc.setYaw(Float.parseFloat(s[4]));
			loc.setPitch(Float.parseFloat(s[5]));
			return loc;
			
    	} else {
    		Util.logSevere("File "+f+" not found. Could not parse location.");
    		return null;
    	}
	}
    
    public static String getRandomWorld() {
    	
    	ArrayList<String> worldList = getAllWorlds();
    	worldList.remove(WORLD.main.getName());
    	if (worldList.size() > 1) {
    		worldList.remove(WORLD.lastWorld.getName());
    	}
    	Collections.shuffle(worldList);
    	return worldList.get(0);
    }
    
    public static ArrayList<String> getAllWorlds() {
    	ArrayList<String> list = new ArrayList<String>();
    	File f = Bukkit.getServer().getWorldContainer();
    	File[] files = f.listFiles();
    	for (File file:files) {
    		try {
    			boolean check = new File(file, "level.dat").exists();
    			if (check) {
    				
        			list.add(file.getName());
    			}
    		} catch (Exception e) {
    		}
    	}
    	return list;
    }
    
    public static void load(String name) {
    	if (Bukkit.getWorld(name) == null) {
    		World w = WorldCreator.name(name).createWorld();
    		
    		w.setAutoSave(false);
    		w.setTime(0L);
    		w.setStorm(false);
    		w.setMonsterSpawnLimit(0);
    		Util.logInfo("Loaded world '"+name+"'");
    		
        }
    }
    
    public static void unload(String name) {
    	if (Bukkit.getWorld(name) != null) {
    		Bukkit.getServer().unloadWorld(name, false);
    		Util.logInfo("Unloaded world '"+name+"'");
    	}
    }
    
    public static boolean isValid(String name) {
    	if (WORLD.getAllWorlds().contains(name)) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public static void setMainSpawn(Player p) {
    	if (p.getWorld().equals(WORLD.main)) {
	    	Location loc = p.getLocation();
	    	File f = new File(loc.getWorld().getWorldFolder(), "main.spawn");
	    	if (!f.exists()) {
	    		try {
					f.createNewFile();
				} catch (IOException e) {
					Bukkit.getLogger().info("An error occured when trying to set the main spawn.");
					e.printStackTrace();
				}
	    	}
				
			try {
				FileWriter writer = new FileWriter(f);
				writer.write(loc.getWorld()+","+loc.getX()+","+loc.getY()+","+loc.getZ()+","+loc.getYaw()+","+loc.getPitch());
				writer.flush(); writer.close();
				p.sendMessage("Main spawn set.");
			} catch (IOException e) {
				Bukkit.getLogger().info("An error occured when trying to set the main spawn.");
				e.printStackTrace();
			}		
	    }
    }
    
    public static Location loadMainSpawn() {
    	World w = Bukkit.getWorlds().get(0);
    	File f = new File (w.getWorldFolder(), "red.spawn");
    	if (f.exists()) {
			try {
				String l = FileUtils.readFileToString(f);
				String[] s = l.split(",");
				Location loc = new Location(w, Double.parseDouble(s[1]), Double.parseDouble(s[2]), Double.parseDouble(s[3]));
				loc.setYaw(Float.parseFloat(s[4]));
				loc.setPitch(Float.parseFloat(s[5]));
				return loc;
			} catch (IOException e) {
				return null;
			}
    	}
    	return WORLD.main.getSpawnLocation();
    }
    
    public static ArrayList<Integer> getBeaconFiles(World w, String side) {
    	ArrayList<Integer> ints = new ArrayList<Integer>();
    	for (File file:w.getWorldFolder().listFiles()) {
    		String name = file.getName();
    		if (name.contains("core")) {
    			if (name.contains(side)) {
        			name = name.replace(side, "");
        			name = name.replace(".loc", "");
        			name = name.replace("core", "");
        			name = name.trim();
        			ints.add(Integer.parseInt(name));	
    			}
    		}
    	}
    	return ints;
    }
    
  //returns lowest available number (ex: 1,2,5 would return 3):
    public static int getAvailBeaconFileName(World w, String side) {
    	ArrayList<Integer> ints = getBeaconFiles(w, side);
    	if (ints.size() > 0) {
    		int lowest = ints.get(0);
    		for (int i:ints) {
    			if (i<lowest) {
    				lowest = i;
    			}
    		}
    		lowest+=1;
    		while (true) {
    			if (ints.contains(lowest)) {
    				lowest+=1;
    			} else {
    				return lowest;
    			}
    		}
    	}
    	return 1;
    }
    
    
    public static void delCore(World w, String side, int num) throws NullPointerException {
    	ArrayList<Integer> ints = getBeaconFiles(w, side);
    	if (ints.contains(num)) {
    		File f = new File (w.getWorldFolder(), side+"core"+num+".loc");
    		f.delete();
    	} else throw new NullPointerException("That file does not exist");
    }
}
