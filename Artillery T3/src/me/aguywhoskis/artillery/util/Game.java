package me.aguywhoskis.artillery.util;

import me.aguywhoskis.artillery.Artillery;
import me.aguywhoskis.artillery.thread.AnnounceMessage;
import me.aguywhoskis.artillery.thread.ChangeMode;
import me.aguywhoskis.artillery.thread.CleanUp;
import me.aguywhoskis.artillery.thread.tick.Announcer;
import me.aguywhoskis.artillery.thread.tick.Forfeit;
import me.aguywhoskis.artillery.thread.tick.Timer;
import me.aguywhoskis.artillery.util.PLUGIN;
import net.minecraft.util.org.apache.commons.io.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Game {
	
	public static ArrayList<String> teamBlue = new ArrayList<String>();
	public static ArrayList<String> teamRed = new ArrayList<String>();
	
    public static String winner = null;
    
    public static ArrayList<Player> absentPlayers = new ArrayList<Player>();
	
    public static HashMap<String, Integer> kills = new HashMap<String, Integer>();
	public static HashMap<String, Integer> deaths = new HashMap<String, Integer>();
	public static HashMap<String, Integer> assists = new HashMap<String, Integer>();
	public static HashMap<String, Integer> exp = new HashMap<String, Integer>();
	public static HashMap<String, Integer> coins = new HashMap<String, Integer>();

    static Plugin myplugin = Artillery.plugin;

	public static void changeMode() {
        BukkitScheduler s = Bukkit.getScheduler();

		if (PLUGIN.gameMode == 0) {
            //Lobby
			String world = WORLD.getRandomWorld();
			WORLD.load(world);
			WORLD.map = Bukkit.getWorld(world);
				
			try {
				WORLD.redSpawn = WORLD.getLocFromFile(WORLD.map, "redspawn.loc");
				WORLD.blueSpawn = WORLD.getLocFromFile(WORLD.map, "bluespawn.loc");
				
			} catch (IOException e) {
				Util.logSevere("Unable to load spawns of "+WORLD.map.getName());
				WORLD.redSpawn = null;
				WORLD.blueSpawn = null;
				return;
						
			}
					
			for (int i:WORLD.getBeaconFiles(WORLD.map, "blue")) {
				try {
					Location loc = WORLD.getLocFromFile(WORLD.map, "bluecore"+i+".loc");
					WORLD.blueCore.add(loc);
				} catch (IOException e) {
					Util.logSevere("Unable to load location from bluecore"+i+".loc");
				}
							
			}	
					
			for (int i:WORLD.getBeaconFiles(WORLD.map, "red")) {
				try {
					WORLD.redCore.add(WORLD.getLocFromFile(WORLD.map,  "redcore"+i+".loc"));
				} catch (IOException e) {
					Util.logSevere("Unable to load location from redcore"+i+".loc");
				}
			}
				
			Timer.start();
			Timer.update();

            PLUGIN.canBuild = false;
            PLUGIN.canBuy = false;
            PLUGIN.canShoot = false;
            PLUGIN.canPvp = false;
		}

        if (PLUGIN.gameMode == 1) {
            //Building
        	
        	Game.changeModeSim1();
        	
        }

        if (PLUGIN.gameMode == 2) {
            //Gameplay

            s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&cPVP and turrets have been enabled! Destroy the opposition's core block(s)! (10 mins remain)"), 0L);
            s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&c5 minutes remain!"), 6000L);
            s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&c2 minutes remain!"), 9600L);
            s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&c1 minute remains!"), 10800L);
            s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&c30 seconds remain!"), 11400L);
            s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&c15 seconds remain!"), 11700L);
            s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&c10 seconds remain!"), 11800L);
            s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&c5 seconds remain!"), 11900L);
            s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&c4 seconds remain!"), 11920L);
            s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&c3 seconds remain!"), 11940L);
            s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&c2 seconds remain!"), 11960L);
            s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&c1 second remains!"), 11980L);
            s.scheduleSyncDelayedTask(myplugin, new ChangeMode(), 12000L);
            

            PLUGIN.canBuild = true;
            PLUGIN.canBuy = true;
            PLUGIN.canShoot = true;
            PLUGIN.canPvp = true;

        }
        if (PLUGIN.gameMode == 3) {
            //Post-Gameplay, deciding winner, & scheduling CleanUp
            if (winner == null) {
                int teamBlueKills = 0;
                for (String name:teamBlue) {
                    teamBlueKills += kills.get(name);
                }
                int teamRedKills = 0;
                for (String name:teamRed) {
                    teamRedKills += kills.get(name);
                }
                if (teamRedKills == teamBlueKills) {

                    int teamBlueAssists = 0;
                    for (String name:teamBlue) {
                        teamBlueAssists += assists.get(name);
                    }

                    int teamRedAssists = 0;
                    for (String name:teamRed) {
                        teamRedAssists += assists.get(name);
                    }
                    
                    if (teamRedAssists == teamBlueAssists) {
                        int teamBlueDeaths = 0;
                        for (String name:teamBlue) {
                            teamBlueDeaths += deaths.get(name);
                        }

                        int teamRedDeaths = 0;
                        for (String name:teamRed) {
                            teamRedDeaths += deaths.get(name);
                        }
                            if (teamRedDeaths == teamBlueDeaths) {
                                if (teamRedDeaths < teamBlueDeaths) {
                                    winner = "red";
                                } else if (teamRedDeaths > teamBlueDeaths){
                                    winner = "blue";
                                }
                            }
                    } else {
                        if (teamRedAssists > teamBlueAssists) {
                            winner = "red";
                        } else if (teamRedAssists < teamRedAssists){
                            winner = "blue";
                        }
                    }

                } else {
                    if (teamRedKills > teamBlueKills) {
                        winner = "red";
                    } else if (teamRedKills < teamBlueKills){
                        winner = "blue";
                    }
                }

            }

            String message;

            //announce win/tie
            if (winner == null) {
                message = "&cThe game is a tie!";
            } else {
                if (winner == "blue") {
                    message = "&1Team blue has won the game and recieves extra coins and exp!";
                } else {
                    message = "&cTeam red has won the game and recieves extra coins and exp!";
                }

            }
            Util.messageServer(message);
            s.scheduleSyncDelayedTask(myplugin, new CleanUp(), 300L);

            PLUGIN.canBuild = false;
            PLUGIN.canBuy = false;
            PLUGIN.canShoot = false;
            PLUGIN.canPvp = false;
            
            Announcer.stop();
            
            Forfeit.stop((JavaPlugin) myplugin);
            Forfeit.timer.clear();
            
        }
		PLUGIN.gameMode+= 1;
	}
	
	public static void assignTeam(Player p) {
		String name = p.getName();
		if (!teamBlue.contains(name)) {
			if (!teamRed.contains(name)) {
				int redLength = teamRed.size();
				int blueLength = teamBlue.size();
				if (redLength > blueLength) {
					teamBlue.add(name);
					p.setDisplayName(ChatColor.BLUE +name+ChatColor.WHITE);
				} else if (blueLength > redLength) {
					teamRed.add(name);
					p.setDisplayName(ChatColor.RED +name+ChatColor.WHITE);
				} else if (blueLength == redLength) {
					int r = 1 +(int)(Math.random()*(2));
					if (r == 1) {
						teamRed.add(name);
						p.setDisplayName(ChatColor.RED +name+ChatColor.WHITE);
					} else if (r == 2) {
						teamBlue.add(name);
						p.setDisplayName(ChatColor.BLUE +name+ChatColor.WHITE);
					} else {
						teamBlue.add(name);
						p.setDisplayName(ChatColor.BLUE +name+ChatColor.WHITE);
					}
				}
			} else {
				p.setDisplayName(ChatColor.RED +name+ChatColor.WHITE);
			}
		} else {
			p.setDisplayName(ChatColor.BLUE +name+ChatColor.WHITE);
		}
	}
	
	public static void changeModeSim1() {
		
        BukkitScheduler s = Bukkit.getScheduler();
		
		for (Player p:Bukkit.getServer().getOnlinePlayers()) {
			
			p.setGameMode(GameMode.SURVIVAL);
			assignTeam(p);
			p.getInventory().clear();
			Util.giveInventory(p);
			
			try {
				if (teamBlue.contains(p.getName())) {
					p.teleport(WORLD.blueSpawn);
					Bukkit.getLogger().info("Teleported "+p.getName()+" to "+WORLD.blueSpawn);
				} else if (teamRed.contains(p.getName())) {
					p.teleport(WORLD.redSpawn);
					Bukkit.getLogger().info("Teleported "+p.getName()+" to "+WORLD.redSpawn);
				}
			} catch (NullPointerException npe) {
				Bukkit.broadcastMessage(ChatColor.RED + "Spawns for that world are not fully set.");
			}
			Game.assists.put(p.getName(), 0);
			Game.kills.put(p.getName(), 0);
			Game.deaths.put(p.getName(), 0);
			ScoreBoard.update(p);
		}

        for (String str:teamBlue) {
            Bukkit.getServer().getPlayer(str).setDisplayName(ChatColor.BLUE +str+ ChatColor.GRAY);
            Bukkit.getServer().getPlayer(str).sendMessage(ChatColor.GRAY + "You are on the "+ChatColor.BLUE + "BLUE "+ChatColor.GRAY + "team!");
        }
        for (String str:teamRed) {
            Bukkit.getServer().getPlayer(str).setDisplayName(ChatColor.RED +str+ ChatColor.GRAY);
            Bukkit.getServer().getPlayer(str).sendMessage(ChatColor.GRAY + "You are on the "+ChatColor.RED + "RED "+ChatColor.GRAY + "team!");
        }
        
      	Util.messageServer(PLUGIN.prefix+"&cYou have 1 minute to set up defences before the battle begins!");

        s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&cThe battle begins in &630&c seconds."), 30*20L);
        s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&cThe battle begins in &615&c seconds."), 45*20L);
        s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&cThe battle begins in &610&c seconds."), 50*20L);
        s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&cThe battle begins in &65&c seconds."), 55*20L);
        s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&cThe battle begins in &64&c seconds."), 56*20L);
        s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&cThe battle begins in &63&c seconds..."), 57*20L);
        s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&cThe battle begins in &62&c seconds..."), 58*20L);
        s.scheduleSyncDelayedTask(myplugin, new AnnounceMessage(myplugin, PLUGIN.prefix+"&cThe battle begins in &61&c second..."), 59*20L);
        s.scheduleSyncDelayedTask(myplugin, new ChangeMode(), 60*20L);

        PLUGIN.started = true;
        PLUGIN.canBuild = true;
        PLUGIN.canBuy = true;
        PLUGIN.canShoot = false;
        PLUGIN.canPvp = false;
        
        Forfeit.start((JavaPlugin) myplugin);
        
        Announcer.init();
        Announcer.start(myplugin);
	}
	
	public static void saveStat(String player, int stat, int amount) {
		
		/**
		 * 0: Exp
		 * 1: Level
		 * 2: Kills
		 * 3: Assists
		 * 4: Deaths
		 * 5: Games
		 * 6: Coins
		 **/
		
		File directory = new File(Artillery.plugin.getDataFolder(), "players");
		File f = new File(directory, player+".txt");
		try {
			String str = FileUtils.readFileToString(f);
			String[] s = str.split(",");
			int val = Integer.parseInt(s[stat]);
			val = amount;
			s[stat] = Integer.toString(val);
			FileWriter writer = new FileWriter(f);
			writer.write(String.format("%s,%s,%s,%s,%s,%s,%s",(Object[])s));
			writer.flush(); writer.close();
			
		} catch (IOException e) {
			Bukkit.getLogger().info("[WARNING] An error occured when trying to edit file "+f);
		}
	}
	
	public static int getStatFromFile(String player, int stat) {
		File directory = new File(Artillery.plugin.getDataFolder(), "players");
		File f = new File(directory, player+".txt");
		try {
			String str;
			str = FileUtils.readFileToString(f);
			String[] s = str.split(",");
			
			return Integer.parseInt(s[stat]);
			
		} catch (IOException e) {
			return 0;
		}
	}
	
	public static void addCoins(Player player, int amount, String reason) {
		Game.coins.put(player.getName(), Game.coins.get(player.getName()) + amount);
		player.sendMessage(ChatColor.GREEN + "("+reason+"): "+ChatColor.GOLD + "+"+amount+" coins! "+ChatColor.AQUA+
				"("+ChatColor.GOLD+Game.coins.get(player.getName()) + ChatColor.AQUA+")");	
	}
	
	public static void stop() {
		Bukkit.getScheduler().cancelTasks(myplugin);
	}
	
	
	//Used to lock out any other team from winning after someone wins; it would be otherwise possible to destroy the other team's core 
	//block after they had destroyed yours. (There is a delay between when the game restarts and when a team wins)
	public static boolean winnerIsLocked;
	
	public static void setWinner(String side) {
		if (!winnerIsLocked) {
			winnerIsLocked = true;
			BukkitScheduler s = Bukkit.getScheduler();
			s.cancelAllTasks();
			Game.winner = side;
			changeMode();
		}
	}
}