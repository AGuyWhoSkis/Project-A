package me.aguywhoskis.artillery.thread;

import me.aguywhoskis.artillery.Artillery;
import me.aguywhoskis.artillery.util.Game;
import me.aguywhoskis.artillery.util.PLUGIN;
import me.aguywhoskis.artillery.util.ScoreBoard;
import me.aguywhoskis.artillery.util.WORLD;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;


public class CleanUp extends BukkitRunnable {

	private Plugin myplugin = Artillery.plugin;
	
	@SuppressWarnings("all")
	public void CleanUp() {
		
	}
	
    public void run() {
        String winner = Game.winner;
        WORLD.lastWorld = WORLD.map;
        int bonusExp = 0;
        int bonusCoins = 0;
        for (Player p: Bukkit.getOnlinePlayers()) {
        	
    		/**
    		 * 0: Exp
    		 * 1: Level
    		 * 2: Kills
    		 * 3: Assists
    		 * 4: Deaths
    		 * 5: Games
    		 * 6: Coins
    		 **/
        	
            p.setDisplayName(p.getName());
            
            String team = "none";
            if (Game.teamBlue.contains(p.getName())) {
            	team = "blue";
            } else if (Game.teamRed.contains(p.getName())) {
            	team = "red";
            }
            if (team == winner) {
            	bonusExp = 300;
            	bonusCoins = 150;
            } else {
            	bonusExp = 150;
            	bonusCoins = 75;
            }
			Game.coins.put(p.getName(), Game.coins.get(p.getName()) + bonusCoins);
			Game.exp.put(p.getName(), Game.exp.get(p.getName()) + bonusExp);
			
			
            Game.saveStat(p.getName(), 0, Game.exp.get(p.getName()));
            Game.saveStat(p.getName(), 2, Game.kills.get(p.getName()));
            Game.saveStat(p.getName(), 3, Game.assists.get(p.getName()));
            Game.saveStat(p.getName(), 4, Game.deaths.get(p.getName()));
            Game.saveStat(p.getName(), 5, 1);
            Game.saveStat(p.getName(), 6, Game.coins.get(p.getName()));
            
            
            p.sendMessage(ChatColor.RED + "~~~~~~~~~~ "+ChatColor.YELLOW + "Stats"+ChatColor.RED+" ~~~~~~~~~~");
            p.sendMessage(ChatColor.GREEN + "Bonus Exp: "+ChatColor.GOLD + bonusExp);
            p.sendMessage(ChatColor.GREEN + "Bonus Coins: "+ChatColor.GOLD + bonusCoins);
            p.sendMessage(ChatColor.GREEN + "Total Exp: "+ChatColor.GOLD+ Game.getStat(p.getName(), 0));
            p.sendMessage(ChatColor.GREEN + "Total Coins: "+ChatColor.GOLD+ Game.getStat(p.getName(), 6));
            p.sendMessage(ChatColor.RED + "~~~~~~~~~~~~~~~~~~~~~~~~~");
            
            ScoreBoard.update(p);
            p.getInventory().clear();
            p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            p.setHealth(20.0D);
            
        }
        WORLD.blueCore.clear();
        WORLD.redCore.clear();
        WORLD.lastWorld = WORLD.map;
        WORLD.unload(WORLD.map.getName());
        WORLD.map = WORLD.main;
        PLUGIN.gameMode = 0;
        BukkitScheduler s = Bukkit.getScheduler();
        s.scheduleSyncDelayedTask(myplugin, new ChangeMode(), 20L);
        Game.winner = null;
        Game.teamBlue.clear();
        Game.teamRed.clear();
        Game.winnerIsLocked = false;
    }
}
