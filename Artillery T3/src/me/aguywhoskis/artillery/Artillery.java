package me.aguywhoskis.artillery;

import me.aguywhoskis.artillery.event.BlockHandle;
import me.aguywhoskis.artillery.event.PlayerHandle;
import me.aguywhoskis.artillery.event.ShopHandle;
import me.aguywhoskis.artillery.thread.tick.Timer;
import me.aguywhoskis.artillery.util.Game;
import me.aguywhoskis.artillery.util.PLUGIN;
import me.aguywhoskis.artillery.util.ScoreBoard;
import me.aguywhoskis.artillery.util.TNTManager;
import me.aguywhoskis.artillery.util.Util;
import me.aguywhoskis.artillery.util.WORLD;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Artillery extends JavaPlugin {


	
	
	public void onEnable() {
		Util.messageServer("test");
		File schemDir = new File(getDataFolder(), "cannons");
		File playerDir = new File (getDataFolder(), "players");
		schemDir.mkdirs();
		playerDir.mkdirs();
		
		plugin = this;
		Bukkit.getPluginManager().registerEvents(new TNTManager(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerHandle(), this);
        Bukkit.getPluginManager().registerEvents(new BlockHandle(), this);
        Bukkit.getPluginManager().registerEvents(new ShopHandle(), this);

        WORLD.mainSpawn = WORLD.loadMainSpawn();

        
        for (Player p: Bukkit.getOnlinePlayers()) {
        	Game.exp.put(p.getName(), Game.getStat(p.getName(), 0));
        	Game.kills.put(p.getName(), 0);
        	Game.assists.put(p.getName(), 0);
        	Game.deaths.put(p.getName(), 0);
        	Game.kills.put(p.getName(), 0);
        	Game.coins.put(p.getName(), Game.getStat(p.getName(), 6));
        	ScoreBoard.create(p);
		}
        
		/**
		 * 0: Exp
		 * 1: Level
		 * 2: Kills
		 * 3: Assists
		 * 4: Deaths
		 * 5: Games
		 * 6: Coins
		 **/
        
        Util.initIs();
        ShopHandle.initIs();
        Game.winnerIsLocked = false;
        //ScoreBoard.updateAll();
        
        for (World w:Bukkit.getWorlds()) {
        	w.setAutoSave(false);
        }
        
        Game.changeMode();
        Timer.update();
        
	}
	
	
	public void onDisable() {
		for (Player p:Bukkit.getServer().getOnlinePlayers()) {
			p.setDisplayName(p.getName());
			p.teleport(WORLD.mainSpawn);
			p.getInventory().clear();
		}
		WORLD.unload(WORLD.map.getName());
		Game.teamBlue.clear();
		Game.teamRed.clear();
		Game.exp.clear();
		Game.kills.clear();
		Game.assists.clear();
		Game.deaths.clear();
		Game.winner = null;

		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "The game has been stopped.");
		Bukkit.broadcastMessage("");
	}

	public static Plugin plugin;
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.isOp()) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("You must be a player to do that.");
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("a")) {
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("setspawn")) {
						
						Player p = (Player) sender;
						if (p.getLocation().getWorld() != WORLD.main) {
							if (args.length == 2) {
								if (args[1].equalsIgnoreCase("blue")) {
									try {
										WORLD.saveLocToFile(p.getLocation(), "bluespawn.loc");
										p.sendMessage("Set.");
									} catch (IOException e) {
										p.sendMessage(ChatColor.RED + "An error occured when setting the blue spawn.");
										e.printStackTrace();
									}
								} else if (args[1].equalsIgnoreCase("red")) {
									try {
										WORLD.saveLocToFile(p.getLocation(), "redspawn.loc");
										p.sendMessage("Set.");
									} catch (IOException e) {
										p.sendMessage(ChatColor.RED + "An error occured when setting the red spawn.");
										e.printStackTrace();
									}
								}	
							} else {
								sender.sendMessage(ChatColor.RED + "Invalid arguments. "+ChatColor.GREEN + "Use /cc setspawn <red/blue>");	
							}
						}
						
					} else if (args[0].equalsIgnoreCase("world")) {
						String name = "";
						for (int i = 1; i < args.length; i++) {
							name = name + args[i] + " ";
						}
						name = name.trim();
						Player p = (Player) sender;
						if (WORLD.isValid(name)) {
							WORLD.load(name);
							World currentWorld = p.getLocation().getWorld();
							p.sendMessage(ChatColor.GREEN + "Teleporting to map \""+name+"\"...");
							if (name == WORLD.main.getName()) {
								p.teleport(WORLD.mainSpawn);
							} else {
								p.teleport(Bukkit.getWorld(name).getSpawnLocation());
								if (currentWorld != Bukkit.getWorlds().get(0) && (currentWorld.getPlayers().size() == 0)) {
									WORLD.unload(currentWorld.getName());
								}	
							}							
						} else if (name.startsWith("--create")){
							name = name.replaceFirst("--create", "");
							name = name.trim();
							if (!WORLD.isValid(name)) {
								p.sendMessage(ChatColor.GREEN+"Creating world \""+name+"\"...");
								WORLD.load(name);
								World w = Bukkit.getWorld(name);
								w.setAutoSave(false);
								w.setMonsterSpawnLimit(0);
							} else {
								p.sendMessage(ChatColor.RED+"That world already exists.");
							}
						} else {
							p.sendMessage("Invalid command");
						}

						
					} else if (args[0].equalsIgnoreCase("worlds")) {
						ArrayList<String> list = WORLD.getAllWorlds();
						sender.sendMessage(ChatColor.GREEN+"All valid worlds:");
						for (String s:list) {
							sender.sendMessage(ChatColor.RED + s);
						}
					} else if (args[0].equalsIgnoreCase("start")) {
						if (PLUGIN.gameMode == 0) {
							Game.changeMode();
						}
					} else if (args[0].equalsIgnoreCase("test")) {
						Player p = (Player) sender;
						p.getInventory().clear();
						Util.giveInventory(p);
						Game.coins.put(p.getName(), Game.coins.get(p.getName()) +100);
						ScoreBoard.update(p);
						
					} else if (args[0].equalsIgnoreCase("setcore")) {
						
						Player p = (Player) sender;
						if (p.getLocation().getWorld() != WORLD.main) {
							if (args.length == 2) {
								if (args[1].equalsIgnoreCase("blue")) {
									try {
										Location loc = Util.roundLoc(p.getLocation());
										loc.setY(loc.getY()-1);
										if (loc.getBlock().getType().equals(Material.BEACON)) {
											int i = WORLD.getAvailBeaconFileName(loc.getWorld(), "blue");
											WORLD.saveLocToFile(loc, "bluecore"+i+".loc");
											p.sendMessage("Saved location as: bluecore"+i+".loc");
										}
									} catch (IOException e) {
										p.sendMessage(ChatColor.RED + "An error occured when saving the blue core.");
										e.printStackTrace();
									}
								} else if (args[1].equalsIgnoreCase("red")) {
									try {
										Location loc = Util.roundLoc(p.getLocation());
										loc.setY(loc.getY()-1);
										if (loc.getBlock().getType().equals(Material.BEACON)) {
											int i = WORLD.getAvailBeaconFileName(loc.getWorld(), "red");
											WORLD.saveLocToFile(loc, "redcore"+i+".loc");
											p.sendMessage("Saved location as: redcore"+i+".loc");
										}
									} catch (IOException e) {
										p.sendMessage(ChatColor.RED + "An error occured when saving the blue core.");
										e.printStackTrace();
									}
								}	
							} else {
								sender.sendMessage(ChatColor.RED + "Invalid arguments. "+ChatColor.GREEN + "Use /a setcore <red/blue>");	
							}
						}
						
						
					
					} else if (args[0].equalsIgnoreCase("delcore")) {
						Player p = (Player) sender;
						if (args.length == 3) {
							try {
								int num = Integer.parseInt(args[2]);
								if (args[1].equalsIgnoreCase("red")) {
									WORLD.delCore(p.getWorld(), "red", num);
								} else if (args[1].equalsIgnoreCase("blue")) {
									WORLD.delCore(p.getWorld(), "blue", num);
								}
							} catch (NumberFormatException e) {
								p.sendMessage("Proper usage: /cc delcore <red:blue> <number>");
							}
						}
					} else if (args[0].equalsIgnoreCase("skip")) {
					
					
						Bukkit.getScheduler().cancelTasks(plugin);
						Game.changeMode();
						Bukkit.broadcastMessage(sender.getName() +" skipped the timer.");
					} else if (args[0].equalsIgnoreCase("save")) {
						Player p = (Player) sender;
						World w = p.getWorld();
						w.save();
						p.sendMessage("Saved world \""+w.getName()+"\".");
					} else if (args[0].equalsIgnoreCase("stop")) {
						sender.sendMessage("Stopping...");
						Bukkit.getScheduler().cancelTasks(plugin);
						sender.sendMessage("Stopped.");
						PLUGIN.gameMode = 0;
					} else if (args[0].equalsIgnoreCase("start")) {
						sender.sendMessage("Starting...");
						Game.changeMode();
					} else if (args[0].equalsIgnoreCase("fakeupdate")) {
						if (args.length > 1) {
							Timer.update(Integer.parseInt(args[1]));
						}
					}
				}
			}
		}
		return true;
	}
}
